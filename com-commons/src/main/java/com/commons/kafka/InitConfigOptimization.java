//package com.commons.kafka;
//
//import com.alibaba.fastjson.JSON;
//import com.google.common.collect.Lists;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
///**
// * elasticsearch 配置文件
// *
// * @author yangz
// * @date 2020/03/09
// */
//@Configuration
//@EnableScheduling
//@Slf4j
//public class InitConfigOptimization {
//
//    @Autowired
//    private KafkaDynamicPartitionConsumer kafkaDynamicPartitionConsumer;
//
//    @Autowired
//    private KafkaDynamicTopicConsumer kafkaDynamicTopicConsumer;
//
//    @Autowired
//    private KafkaAlertRulesLocalOptimizationCache2Impl kafkaAlertRulesLocalOptimizationCache2Impl;
//
//    @Autowired
//    private HttpReportAlertRulesLocalCacheImpl httpReportAlertRulesLocalCacheImpl;
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Autowired
//    private KafkaTopicAssignCacheImpl kafkaTopicAssignCacheImpl;
//
//    /**
//     * container
//     */
//    private static Map<String, ConcurrentMessageListenerContainer> httpContainerMap = new HashMap();
//
//    /**
//     * container
//     */
//    private static Map<String, ConcurrentMessageListenerContainer> kafkaContainerMap = new HashMap();
//
//    /**
//     * container
//     */
//    private static Map<String, String> kafkaTopicPartitionMap = new HashMap();
//
//    @Value("${kafka.consumer.topic.group:1}")
//    private int topicGroupCount;
//
//    public static String MACHINE_IP = "";
//
//    /**
//     * scheduled
//     */
//    @Scheduled(cron = "0/15 * * * * ?")
//    private void configureTasks() {
//        log.info("configureTasks begin");
//        log.info("configureTasks kafka begin");
//        List<Object> kafkaTopics = Lists.newArrayList();
//        topicConsumer(kafkaTopics);
//
//        log.info("configureTasks kafka end");
//        log.info("configureTasks end");
//    }
//
//    /**
//     * @param topics topics
//     */
//    private void partitionConsumer(List<Object> topics) {
//        log.info("partitionConsumer topics:{}", topics);
//        Map<String, Byte> topicsMapNow = new HashMap<>();
//
//        for (Object value : topics) {
//            String topic = (String) value;
//            if (!topic.matches("^[^\\u4e00-\\u9fa5]+$")) {
//                log.info("partitionConsumer error topic:{}", topic);
//                continue;
//            }
//
//            ConcurrentMessageListenerContainer container = kafkaContainerMap.get(topic);
//            TopicPartitionMachine topicPartitionMachine = getTopicPartitionMachine(topic);
//            List<Integer> list = new ArrayList<>();
//            if (topicPartitionMachine != null) {
//                list = JSON.parseArray(topicPartitionMachine.getTopicPartitions()).toJavaList(Integer.class);
//            }
//
//            if (container == null) {
//                if (topicPartitionMachine == null) {
//                    continue;
//                }
//
//                kafkaDynamicPartitionConsumer.addTopicListener(topic, kafkaContainerMap, list, kafkaTopicPartitionMap);
//                log.info("partitionConsumer start0:{}", topic);
//            } else {
//                log.info("partitionConsumer container begin", topic);
//                if (topicPartitionMachine == null) {
//                    contaierStopSingle(topic);
//                    log.info("partitionConsumer start1:{}", topic);
//                } else if (topicPartitionMachine.getTopicPartitions().equals(kafkaTopicPartitionMap.get(topic))) {
//                    if (!container.isRunning()) {
//                        log.info("partitionConsumer start3:{}", topic);
//                        contaierStopSingle(topic);
//                        kafkaDynamicPartitionConsumer.addTopicListener(topic, kafkaContainerMap, list, kafkaTopicPartitionMap);
//                    }
//                    log.info("partitionConsumer star4:{}", topic);
//                } else if (!topicPartitionMachine.getTopicPartitions().equals(kafkaTopicPartitionMap.get(topic))) {
//                    log.info("partitionConsumer star5:{}", topic);
//                    contaierStopSingle(topic);
//                    kafkaDynamicPartitionConsumer.addTopicListener(topic, kafkaContainerMap, list, kafkaTopicPartitionMap);
//                }
//            }
//
//            topicsMapNow.put(topic, Byte.valueOf("1"));
//        }
//
//        containerStop(kafkaContainerMap, topicsMapNow);
//    }
//
//    /**
//     * @param topics topics
//     */
//    private void topicConsumer(List<Object> topics) {
//        log.info("topicConsumer type:{} topics:{}", topics);
//        if (CollectionUtils.isEmpty(topics)) {
//            return;
//        }
//        Map<String, Byte> topicsMapNow = new HashMap<>();
//
//        //新增
//        topics.forEach(value -> {
//            String topic = (String) value;
//            if (topic.matches("^[^\\u4e00-\\u9fa5]+$")) {
//                ConcurrentMessageListenerContainer container = null;
//                container = kafkaContainerMap.get(topic);
//
//                if (container == null) {
//                    kafkaDynamicTopicConsumer.addTopicListener(topic,  kafkaContainerMap);
//                    log.info("topicConsumer start0:{}", topic);
//                } else if (!container.isRunning()) {
//                    //如果缓存中有并且开关是关的则表示原先关闭了现在开启
//                    kafkaContainerMap.get(topic).start();
//                    log.info("topicConsumer start1:{}", topic);
//                }
//                topicsMapNow.put(topic, Byte.valueOf("1"));
//            } else {
//                log.info("topicConsumer error topic:{}", topic);
//            }
//        });
//
//        containerStop(kafkaContainerMap, topicsMapNow);
//    }
//
//    private void contaierStopSingle(String topic) {
//        if (kafkaContainerMap.get(topic).isRunning()) {
//            kafkaContainerMap.get(topic).stop();
//            log.info("configureTasks stop:{}", topic);
//        }
//        kafkaContainerMap.remove(topic);
//        kafkaTopicPartitionMap.remove(topic);
//    }
//
//    /**
//     * @param containerMap containerMap
//     * @param topicsMapNow topicsMapNow
//     */
//    private void containerStop(Map<String, ConcurrentMessageListenerContainer> containerMap, Map<String, Byte> topicsMapNow) {
//        containerMap.keySet().forEach(key -> {
//            try {
//                if (topicsMapNow.get(key) == null) {
//                    if (containerMap.get(key) != null && containerMap.get(key).isRunning()) {
//                        containerMap.get(key).stop();
//                        log.info("configureTasks stop:{}", key);
//                    }
//                }
//            } catch (Exception e) {
//                log.error("containerStop stop error:{}", key, e);
//            }
//        });
//    }
//
//    private TopicPartitionMachine getTopicPartitionMachine(String topic) {
//        TopicPartitionMachine topicPartitionMachine = kafkaTopicAssignCacheImpl.getTopicPartitionMachine(topic, getIp());
//        return topicPartitionMachine;
//    }
//
//    /**
//     * @return String
//     */
//    private String getIp() {
//        try {
//            if (StringUtils.isEmpty(MACHINE_IP)) {
//                MACHINE_IP = InetAddress.getLocalHost().getHostAddress();
//            }
//        } catch (UnknownHostException e) {
//            log.error("getIp error:", e);
//        }
//        log.info("MACHINE_IP INFO:{}", MACHINE_IP);
//        return MACHINE_IP;
//    }
//
//}
