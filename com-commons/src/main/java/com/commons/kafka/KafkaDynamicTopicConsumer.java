//package com.commons.kafka;
//
//import java.time.LocalDateTime;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.TopicPartition;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.BatchMessageListener;
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
//import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
//import org.springframework.kafka.listener.ContainerProperties;
//import org.springframework.stereotype.Service;
//
///**
// * kafka 实现类
// *
// * @author yangz
// * @date 2020/03/09
// */
//@Service
//@Slf4j
//public class KafkaDynamicTopicConsumer {
//
//    @Value("${kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${kafka.group-id:alert_collector_kafka}")
//    private String groupId;
//
//    @Value("${kafka.consumer.enable-auto-commit:false}")
//    private boolean enableAutoCommit;
//
//    @Value("${kafka.consumer.auto-commit-interval:2000}")
//    private Integer autoCommitInterval;
//
//    @Value("${kafka.consumer.auto-offset-reset:latest}")
//    private String autoOffsetReset;
//
//    @Value("${kafka.consumer.session.timeout.ms:30000}")
//    private Integer sessionTimeoutMs;
//
//    @Value("${kafka.consumer.nodes:12}")
//    private Integer nodes;
//
//    /**
//     * 添加单个topic
//     *
//     * @param topic topic
//     * @param containerMap containerMap
//     */
//    public void addTopicListener(String topic, Map<String, ConcurrentMessageListenerContainer> containerMap) {
//        try {
//            ContainerProperties properties = new ContainerProperties(topic);
//            properties.setMessageListener(new BatchMessageListener() {
//                @Override
//                public void onMessage(Object o) {
//                    try {
//                        List<ConsumerRecord> consumerRecords = (List<ConsumerRecord>)o;
//                        LocalDateTime now = LocalDateTime.now();
//                        consumerRecords.forEach(value -> {
//                            String data = (String)value.value();
//                            //todo 业务逻辑
//                        });
//                    } catch (Exception e) {
//                        log.error("KafkaDynamicTopicConsumer kafka work thread error is {}", e.getMessage(), e);
//                    }
//                }
//            });
//
//            properties.setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {
//                @Override
//                public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
//                    Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);
//
//                    partitions.forEach(t -> {
//                        log.info("KafkaDynamicTopicConsumer topic is {}, partition is {}", t.topic(), t.partition());
//                        TopicPartition partition = new TopicPartition(t.topic(), t.partition());
//                        consumer.seek(partition, endOffsets.get(partition));
//                        consumer.commitSync();
//                    });
//                }
//            });
//
//            int size = topicPartitionSize(topic);
//            if (size > 0) {
//                if (size % nodes > 0) {
//                    size = size / nodes + 1;
//                } else {
//                    size = size / nodes;
//                }
//
//                log.info("KafkaDynamicTopicConsumer.addTopicListener for {} {} partitions begin", topic, size);
//                ConcurrentMessageListenerContainer container =
//                        new ConcurrentMessageListenerContainer(consumerFactory(), properties);
//                container.setConcurrency(size);
//                container.start();
//
//                containerMap.put(topic, container);
//                log.info("KafkaDynamicTopicConsumer.addTopicListener end");
//            }
//        } catch (Exception e) {
//            log.error("KafkaDynamicTopicConsumer addTopicListener.error:{}", e.getMessage(), e);
//        }
//    }
//
//    /**
//     * @param topic topic
//     * @return int
//     */
//    public int topicPartitionSize(String topic) {
//        log.info("KafkaDynamicTopicConsumer.topicPartitionSize begin");
//        KafkaProducer kafkaProducer = null;
//
//        try {
//            Properties props = new Properties();
//            props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
//            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//            kafkaProducer = new KafkaProducer<String, byte[]>(props);
//            return kafkaProducer.partitionsFor(topic)
//                                .size();
//        } catch (Exception e) {
//            log.error("KafkaDynamicTopicConsumer.topicPartitionSize.error: ", e);
//        } finally {
//            if (kafkaProducer != null) {
//                kafkaProducer.close();
//            }
//        }
//        log.info("KafkaDynamicTopicConsumer.topicPartitionSize end");
//        return 0;
//    }
//
//    /**
//     * 穿件kafka消费
//     *
//     * @return ConsumerFactory
//     */
//    public ConsumerFactory<String, String> consumerFactory() {
//        ConsumerFactory factory = new DefaultKafkaConsumerFactory<>(consumerConfigs());
//        return factory;
//    }
//
//    /**
//     * @return Map<String, Object>
//     */
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> propsMap = new HashMap<>();
//        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "topic");
//        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
//        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
//        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
//        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
//        propsMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5000);
//        propsMap.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
//                "org.apache.kafka.clients.consumer.RoundRobinAssignor");
//        return propsMap;
//    }
//
//}
