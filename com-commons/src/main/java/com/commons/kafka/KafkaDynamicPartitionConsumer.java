//package com.commons.kafka;
//
//import com.alibaba.fastjson.JSON;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.common.TopicPartition;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.BatchMessageListener;
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
//import org.springframework.kafka.listener.ContainerProperties;
//import org.springframework.kafka.support.TopicPartitionOffset;
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
//public class KafkaDynamicPartitionConsumer {
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
//    public void addTopicListener(String topic,
//            Map<String, ConcurrentMessageListenerContainer> containerMap,
//            List<Integer> list,
//            Map<String, String> kafkaTopicPartitionMap) {
//        try {
//            TopicPartitionOffset[] topicPartitionInitialOffsets = new TopicPartitionOffset[list.size()];
//            Map<TopicPartition, Long> topicPartitionIffsets = getTopicPartitionIffsets(topic, list);
//            for (int i = 0; i < list.size(); i++) {
//                int index = list.get(i);
//                TopicPartitionOffset topicPartitionInitialOffset = new TopicPartitionOffset(topic, index,
//                        topicPartitionIffsets.get(new TopicPartition(topic, index)));
//                topicPartitionInitialOffsets[i] = topicPartitionInitialOffset;
//            }
//
//            ContainerProperties properties = new ContainerProperties(topicPartitionInitialOffsets);
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
//                        log.error("KafkaDynamicPartitionConsumer kafka work thread error is {}", e.getMessage(), e);
//                    }
//                }
//            });
//
//            log.info("KafkaDynamicPartitionConsumer.addTopicListener for {} partitions begin", topic);
//            ConcurrentMessageListenerContainer container =
//                    new ConcurrentMessageListenerContainer(consumerFactory(), properties);
//            container.setConcurrency(list.size());
//            container.start();
//
//            containerMap.put(topic, container);
//            kafkaTopicPartitionMap.put(topic, JSON.toJSONString(list));
//            log.info("KafkaDynamicPartitionConsumer.addTopicListener end");
//        } catch (Exception e) {
//            log.error("KafkaDynamicPartitionConsumer addTopicListener.error:{}", e.getMessage(), e);
//        }
//    }
//
//    /**
//     * @param topic topic
//     */
//    public Map<TopicPartition, Long> getTopicPartitionIffsets(String topic, List<Integer> list) {
//        log.info("KafkaDynamicPartitionConsumer.consumer begin");
//        KafkaConsumer<Integer, String> consumer = null;
//        Map<TopicPartition, Long> map = new HashMap();
//        try {
//            consumer = new KafkaConsumer<>(consumerConfigsConsumer());
//            consumer.subscribe(Arrays.asList(topic));
//            log.info("KafkaDynamicPartitionConsumer.consumer topic:{}-list:{}", topic, list);
//            List<TopicPartition> partitions = new ArrayList<>();
//            for (int i = 0; i < list.size(); i++) {
//                TopicPartition partition = new TopicPartition(topic, list.get(i));
//                partitions.add(partition);
//            }
//            consumer.poll(0);
//            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);
//            Set<TopicPartition> set = endOffsets.keySet();
//            for (TopicPartition topicPartition : set) {
//                log.info("KafkaDynamicPartitionConsumer.consumer topicPartition:{}-size:{}", topicPartition,
//                        endOffsets.get(topicPartition));
//                try {
//                    map.put(topicPartition, endOffsets.get(topicPartition));
//                } catch (Exception e) {
//                    log.error("KafkaDynamicPartitionConsumer.seek：", e);
//                } finally {
//                }
//            }
//
//        } catch (Exception e) {
//            log.error("KafkaDynamicPartitionConsumer.consumer：", e);
//        } finally {
//            if (consumer != null) {
//                consumer.close();
//            }
//        }
//
//        log.info("KafkaDynamicPartitionConsumer.consumer end");
//        return map;
//    }
//
//    /**
//     * @param topic topic
//     */
//    public Map<TopicPartition, Long> setPartitionsOffset(String topic, List<Integer> list) {
//        log.info("KafkaDynamicPartitionConsumer.setPartitionsOffset begin");
//        KafkaConsumer<Integer, String> consumer = null;
//        Map<TopicPartition, Long> map = new HashMap();
//        try {
//            consumer = new KafkaConsumer<>(consumerConfigs());
//            consumer.subscribe(Arrays.asList(topic));
//            log.info("KafkaDynamicPartitionConsumer.setPartitionsOffset topic:{}-list:{}", topic, list);
//            List<TopicPartition> partitions = new ArrayList<>();
//            for (int i = 0; i < list.size(); i++) {
//                TopicPartition partition = new TopicPartition(topic, list.get(i));
//                partitions.add(partition);
//            }
//            consumer.assign(partitions);
//            consumer.poll(0);
//            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);
//            Set<TopicPartition> set = endOffsets.keySet();
//            for (TopicPartition topicPartition : set) {
//                log.info("KafkaDynamicPartitionConsumer.setPartitionsOffset topicPartition:{}-size:{}", topicPartition,
//                        endOffsets.get(topicPartition));
//                try {
//                    consumer.seek(topicPartition, endOffsets.get(topicPartition));
//                    consumer.commitSync();
//                } catch (Exception e) {
//                    log.error("KafkaDynamicPartitionConsumer.setPartitionsOffset.seek：", e);
//                } finally {
//                }
//            }
//
//        } catch (Exception e) {
//            log.error("KafkaDynamicPartitionConsumer.setPartitionsOffset：", e);
//        } finally {
//            if (consumer != null) {
//                consumer.close();
//            }
//        }
//
//        log.info("KafkaDynamicPartitionConsumer.setPartitionsOffset end");
//        return map;
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
//    public Map<String, Object> consumerConfigsConsumer() {
//        Map<String, Object> propsMap = new HashMap<>();
//        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "consumer");
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
//    /**
//     * @return Map<String, Object>
//     */
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> propsMap = new HashMap<>();
//        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "partition");
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
