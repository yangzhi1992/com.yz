package com.commons.javase.collectionframework;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ExSetExample {

	public static void main(String[] args) {
		EnumSet<Day> weekend = EnumSet.of(Day.SATURDAY, Day.SUNDAY);
		System.out.println(weekend); // [SATURDAY, SUNDAY]

		// 线程安全
		Set<String> synchronizedSet = Collections.synchronizedSet(new HashSet<>());

		// 遍历-forEach
		synchronizedSet.forEach(System.out::println);

		// 遍历-iterator
		Iterator<String> iterator = synchronizedSet.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}

		// 使用增强型 for 循环
		for (String element : synchronizedSet) {
			System.out.println(element);
		}

	}

	public static enum Day {
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
	}
}
