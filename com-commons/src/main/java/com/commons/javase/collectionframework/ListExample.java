package com.commons.javase.collectionframework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ListExample {
	public static void main(String[] args) {
		sort();
		sortObject();
	}

	//对实现了 Comparable 接口的类进行按自然顺序排序。
	//Collections.sort()
	//list.sort()
	//Stream.sorted()
	public static void sort() {
		List<Integer> list = Arrays.asList(5, 3, 7, 2);
		Collections.sort(list); // 自然顺序升序排序
		System.out.println(list); // 输出: [2, 3, 5, 7]

		list.sort((p1, p2) -> {
			return p2 - p1;
		}); // 倒序排序
		System.out.println(list); // 输出: [7, 5, 3, 2]

		System.out.println(list.stream()
				.sorted()
				.collect(Collectors.toList())); // 输出: [2, 3, 5, 7]
		System.out.println(list.stream()
				.sorted(Comparator.comparingInt(p1 -> p1))
				.collect(Collectors.toList())); // 输出: [2, 3, 5, 7]
		System.out.println(list.stream()
				.sorted((p1, p2) -> p2.compareTo(p1))
				.collect(Collectors.toList())); // 输出: [7, 5, 3, 2]
	}

	//自定义类列表排序
	public static void sortObject() {
		List<Person> list = Arrays.asList(
				new Person("Alice", 30),
				new Person("Bob", 25),
				new Person("Charlie", 35)
		);
		Collections.sort(list, (p1, p2) -> {
			return p1.age - p2.age;
		}); // 按年龄升序
		System.out.println(list);

		list.sort((p1, p2) -> p2.age - p1.age); // 按年龄降序
		System.out.println(list);
	}

	//遍历
	public static void forEach() {
		List<Integer> list = Arrays.asList(5, 3, 7, 2);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}

		for (Integer item : list) {
			System.out.println(item);
		}

		Iterator<Integer> iterator = list.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}

		list.forEach(item -> System.out.println(item));
	}

	//线程安全
	public static void safe() {
		List<Integer> list = Collections.synchronizedList(new ArrayList<>());
		CopyOnWriteArrayList<String> threadSafeList = new CopyOnWriteArrayList<>();
	}

	public static class Person {
		String name;
		int age;

		Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		@Override
		public String toString() {
			return name + " (" + age + ")";
		}
	}
}
