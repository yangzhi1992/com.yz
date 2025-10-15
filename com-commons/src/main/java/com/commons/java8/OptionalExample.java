package com.commons.java8;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OptionalExample {
	public static void main(String[] args) {
		// 1. 创建 Optional 对象
		// 1.1 创建包含非空值的Optional
		Optional<String> nonEmpty = Optional.of("Hello");           //如果传入null会抛NPE

		// 1.2 创建可能为空的Optional
		Optional<String> nullable = Optional.ofNullable("Hello");

		// 1.3 创建空Optional
		Optional<String> empty = Optional.empty();

		Optional<String> optionalValue = Optional.of("Hello");
		// 2. 基本使用模式
		// 2.1 判断是否有值
		if (optionalValue.isPresent()) {
			System.out.println(optionalValue.get());
		}

		// 2.2 获取值(不安全，可能抛NoSuchElementException)
		String value = optionalValue.get();                              //不安全，可能抛NoSuchElementException，如果Hello是不能存在的值回抛异常

		// 2.3 安全获取值(提供默认值)
		String safeValue = optionalValue.orElse("default");
		System.out.println("safeValue:" + safeValue);

		// 2.4 安全获取值(使用Supplier延迟计算)
		String lazyValue = optionalValue.orElseGet(() -> String.valueOf(System.currentTimeMillis()));
		System.out.println("lazyValue:" + safeValue);

		// 3. 链式操作
		// 3.1 map - 值存在时执行转换
		Optional<String> upper = optionalValue.map(String::toUpperCase);

		// 3.2 flatMap - 避免Optional嵌套
		Optional<Optional<String>> bad = Optional.of(Optional.of("value"));
		Optional<String> good = Optional.of("value")
				.flatMap(v -> Optional.of(v.toUpperCase()));

		// 3.3 filter - 条件过滤
		Optional<String> filtered = optionalValue.filter(s -> s.length() > 3);

		// 3.4 ifPresent - 值存在时执行操作
		optionalValue.ifPresent(v -> System.out.println("Found: " + v));

		// 3.5 orElseThrow - 值不存在时抛出异常
		optionalValue.orElseThrow(() -> new IllegalArgumentException("Value is required"));

		List<String> names = Arrays.asList("Alice", null, "Bob", null, "Charlie");
		// 过滤null值
		List<String> nonNullNames = names.stream()
				.map(Optional::ofNullable)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());

	}
}
