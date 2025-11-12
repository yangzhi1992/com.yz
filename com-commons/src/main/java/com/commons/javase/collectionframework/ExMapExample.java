package com.commons.javase.collectionframework;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ExMapExample {
	public static void main(String[] args) {
		// 遍历键集合 -（keySet）
		Map<String, Integer> map = new HashMap<>();
		map.put("A", 1);
		map.put("B", 2);
		for (String key : map.keySet()) {
			System.out.println("Key: " + key + ", Value: " + map.get(key));
		}

		// 遍历键集合 - 遍历键值对集合（entrySet）
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
		}

		// 遍历键集合 - 使用 Streams 方式
		map.entrySet().forEach(entry -> System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue()));

		// 遍历键集合 - 使用 forEach 方法
		map.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

		// 线程安全 - Collections.synchronizedMap
		Map<String, String> synchronizedMap = Collections.synchronizedMap(new HashMap<>());

		// 排序 - 按Key排序
		Map<String, Integer> sortedByKey = map.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey()) // 按Key排序
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(e1, e2) -> e1,
						LinkedHashMap::new) // 保持插入顺序
				);

		// 排序 - 按值排序
		Map<String, Integer> sortedByValue = map.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(e1, e2) -> e1,
						LinkedHashMap::new) // 保持插入顺序
				);

	}
}
