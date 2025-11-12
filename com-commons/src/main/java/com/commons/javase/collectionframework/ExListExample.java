package com.commons.javase.collectionframework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ExListExample {
	public static void main(String[] args) {
		sort();
		sortObject();
	}

	// 浅拷贝
	public static void shallowCopy() {
		//使用集合的构造函数进行copy;操作简单，常用，适用于大多数浅拷贝场景；注意： 该方法是浅拷贝，对引用类型对象，该方法并不会创建对象的深拷贝。
		// 原始列表
		List<String> originalList = new ArrayList<>();
		originalList.add("A");
		originalList.add("B");
		originalList.add("C");
		// 使用构造函数复制
		List<String> copyList = new ArrayList<>(originalList);
		System.out.println(copyList); // [A, B, C]
		// 修改原始列表，不影响复制后的列表
		originalList.add("D");
		System.out.println(originalList); // [A, B, C, D]
		System.out.println(copyList);    // [A, B, C]

		// 使用 addAll() 方法进行copy
		List<String> copyList2 = new ArrayList<>();
		copyList2.addAll(originalList);
		System.out.println(copyList); // [1, 2, 3]

		// 使用 Collections.copy()进行copy
		List<String> copyList3 = new ArrayList<>(Collections.nCopies(originalList.size(), null));
		Collections.copy(copyList3, originalList);
		System.out.println(copyList3); // [1, 2, 3]

		// 使用 Java 8 Stream 的 collect() 方法进行copy
		List<String> copyList4 = originalList.stream()
				.collect(Collectors.toList());
		System.out.println(copyList4); // [1, 2, 3]
	}

	// 深拷贝 - 自定义深拷贝
	public static void deepCopy() throws CloneNotSupportedException {
		// 原始列表
		List<Person> originalList = new ArrayList<>();
		originalList.add(new Person("Alice",11));
		originalList.add(new Person("Bob",11));

		// 深拷贝（新建一个列表，并克隆每个元素）
		List<Person> deepCopyList = new ArrayList<>();
		for (Person p : originalList) {
			deepCopyList.add(p.clone());
		}

		// 修改原始列表中的元素
		originalList.get(0).name = "Changed";

		System.out.println("Original List: " + originalList); // [Changed, Bob]
		System.out.println("Deep Copy List: " + deepCopyList); // [Alice, Bob]
	}

	// 深拷贝 - 使用序列化实现深拷贝
	public static void deepCopy2() throws CloneNotSupportedException, IOException, ClassNotFoundException {
		// 原始列表
		List<Person2> originalList = new ArrayList<>();
		originalList.add(new Person2("Alice"));
		originalList.add(new Person2("Bob"));

		// 使用序列化进行深拷贝
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
		objectOut.writeObject(originalList);

		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream objectIn = new ObjectInputStream(byteIn);
		List<Person> deepCopyList = (List<Person>) objectIn.readObject();

		// 修改原始数据
		originalList.get(0).name = "Changed";

		System.out.println("Original List: " + originalList); // [Changed, Bob]
		System.out.println("Deep Copy List: " + deepCopyList); // [Alice, Bob]
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

	public static class Person implements Cloneable {
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

		@Override
		protected Person clone() throws CloneNotSupportedException {
			return (Person) super.clone();
		}
	}

	public static class Person2 implements Serializable {
		private static final long serialVersionUID = 9166978294119772436L;
		String name;

		Person2(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
