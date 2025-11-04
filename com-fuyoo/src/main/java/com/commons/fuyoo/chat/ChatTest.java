package com.commons.fuyoo.chat;

import com.alibaba.fastjson.JSONObject;
import com.iqiyi.stream.sdk.common.commons.StreamProducerConfiguration;
import com.iqiyi.stream.sdk.java.kafka23.StreamJavaProducerUtils23;
import com.iqiyi.stream.sdk.java.kafka23.StreamKafkaProducer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ChatTest {
	public static void main(String[] args) throws Exception {
		String token = "b53d136be1d84131ab674d7218580242";  // 设置用户 Token
		String projectName = "qlive";        // 设置项目名称
		String queueName = "user_online_test";          // 设置队列名称
		String producerGroup = "PG-user_online_test";      // 设置生产组

		StreamProducerConfiguration producerConfiguration = new StreamProducerConfiguration(token);
		producerConfiguration.setProject(projectName);
		producerConfiguration.setQueue(queueName);
		producerConfiguration.setProducerGroup(producerGroup);
		producerConfiguration.setCustomerProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producerConfiguration.setCustomerProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


		StreamKafkaProducer<String, String> producer = StreamJavaProducerUtils23.createStreamKafkaProducer(producerConfiguration);

		JSONObject msg = new JSONObject();
		msg.put("partnerId",20);
		msg.put("roomId", 9001000009080849L);
		msg.put("userIp", "123.126.30.178");
		msg.put("status", 1);
		msg.put("deviceId", "device123456");
		msg.put("updateTime", (new Date()).getTime());
		msg.put("platform", "pcw");
		msg.put("dfp", "a060eabbd56e2c475daec0d0684b3ab71db7257c6144eba161de2b73dffd0b3aea");
		msg.put("ext", "{\"tp\":1}");
		List<String> lines = Arrays.asList("");
		int count = 0;
		for (String line : lines) {
			ProducerRecord<String, String> record = new ProducerRecord<>(queueName, null, msg.toJSONString());
			try {
				RecordMetadata result = producer.send(record).get();
				System.out.println(++count);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
