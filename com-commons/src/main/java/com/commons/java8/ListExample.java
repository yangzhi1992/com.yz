package com.commons.java8;

import com.alibaba.fastjson.JSON;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

public class ListExample {
	public static void main(String[] args) {
		List<InfoDTO> infoDTOS = new ArrayList<>();
		//1 列表转数组
		// 方式1: toArray() - 返回Object[]
		Object[] array1 = infoDTOS.toArray();
		// 方式2: toArray(T[]) - 类型安全
		String[] array2 = infoDTOS.toArray(new String[0]);

		//2 流转集合
		// 流转List
		List<String> listFromStream = Stream.of("A", "B", "C")
				.collect(Collectors.toList());
		// 流转Set
		Set<String> setFromStream = Stream.of("A", "B", "A")
				.collect(Collectors.toSet());
		// 流转数组
		String[] arrayFromStream = Stream.of("X", "Y", "Z")
				.toArray(String[]::new);

		int[] ints = {1, 2, 3};
		// 错误方式: 会把整个数组当作一个元素
		List<int[]> wrongList = Arrays.asList(ints);
		// 正确方式1: 使用包装类
		Integer[] boxedInts = {1, 2, 3};
		List<Integer> correctList1 = Arrays.asList(boxedInts);
		// 正确方式2: Java8流式处理
		List<Integer> correctList2 = Arrays.stream(ints)
				//转化包装类型
				.boxed()
				.collect(Collectors.toList());

		// 二维数组转嵌套列表
		Integer[][] matrix = {{1, 2}, {3, 4}};
		List<List<Integer>> matrixList = Arrays.stream(matrix)
				.map(Arrays::asList)
				.collect(Collectors.toList());
		Integer[][] array2D = matrixList.stream()
				.map(list -> list.toArray(new Integer[0]))
				.toArray(Integer[][]::new);
		System.out.println(JSON.toJSONString(array2D));

		//3 distinct - 去重
		List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3);
		List<Integer> distinct = numbers.stream()
				.distinct()
				.collect(Collectors.toList());

		//4 skip/limit - 分页控制
		distinct = numbers.stream()
				.skip(20)  // 跳过前20条 (0-19)
				.limit(10) // 取10条
				.collect(Collectors.toList());

		//anyMatch，allMatch，noneMatch，findFirst，findAny
		List<String> fruits = Arrays.asList("Apple", "Banana", "Orange", "Grapes", "Peach");
		// 1. 检查是否存在名字以 "Ap" 开头的水果
		boolean hasApFruit = fruits.stream()
				.anyMatch(fruit -> fruit.startsWith("Ap"));
		System.out.println("是否包含以 'Ap' 开头的水果? " + hasApFruit);
		// 2. 检查所有水果名是否含有字母 "e"
		boolean allContainE = fruits.stream()
				.allMatch(fruit -> fruit.contains("e"));
		System.out.println("所有水果名称中是否含有 'e': " + allContainE);
		// 3. 检查是否没有以 "Z" 开头的水果
		boolean noneStartsWithZ = fruits.stream()
				.noneMatch(fruit -> fruit.startsWith("Z"));
		System.out.println("没有水果以 'Z' 开头: " + noneStartsWithZ);
		// 4. 查找第一个水果
		fruits.stream()
				.findFirst()
				.ifPresent(fruit -> System.out.println("第一个水果是: " + fruit));
		// 5. 查找任意一个水果
		fruits.parallelStream()
				.findAny()
				.ifPresent(fruit -> System.out.println("任意一个水果是: " + fruit));

		// sort
		listAllSorted();
	}

	//map InfoDTO->String 1->1 数据量越大效果越好（通常 >10,000元素）
	public static List<String> getInfoDtoNameToListByParallelStream(List<InfoDTO> infoDTOS) {
		return infoDTOS.parallelStream()
				.map(InfoDTO::getName)
				.collect(Collectors.toList());
	}

	//map InfoDTO->String 1->1 map
	public static List<String> getInfoDtoNameToList(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.map(InfoDTO::getName)
				.collect(Collectors.toList());
	}

	//map InfoDTO->String 1->1 Collectors.mapping
	public static List<String> getInfoDtoNameToList2(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.collect(Collectors.mapping(InfoDTO::getName, Collectors.toList()));
	}

	//List->String
	public static String getInfoDtoNameToString(List<InfoDTO> infoDTOS) {
		return String.join(",", getInfoDtoNameToList(infoDTOS));
	}

	public static String getInfoDtoNameToString1(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.map(InfoDTO::getName)
				.collect(Collectors.joining(","));
	}

	public static String getInfoDtoNameToString2(List<InfoDTO> infoDTOS) {
		return StringUtils.join(getInfoDtoNameToList(infoDTOS), ",");
	}

	//List<List<InfoDTO>> -> List<InfoDTO> flatMap 1->多
	public static List<InfoDTO> getInfoDtoToList(List<List<InfoDTO>> infoDTOS) {
		return infoDTOS.stream()
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	//List->Set 会无序
	public static Set<InfoDTO> getInfoDtoToSet(List<InfoDTO> infoDTOS) {
		return new HashSet<>(infoDTOS);
	}

	public static Set<InfoDTO> getInfoDtoToSet1(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.collect(Collectors.toSet());
	}

	//List<InfoDTO>->Map<Long,InfoDTO> 简单分类
	public static Map<Long, InfoDTO> getInfoDtoToMapForNew(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.filter(v -> v.getId() != null)
				.collect(Collectors.toMap(InfoDTO::getId,
						k -> k, //value获取原对象
						(oldV, newV) -> newV)); //新的数据覆盖旧的数据
	}

	public static Map<Long, InfoDTO> getInfoDtoToMapForOld(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.filter(v -> v.getId() != null)
				.collect(Collectors.toMap(InfoDTO::getId,
						k -> k, //value获取原对象
						(oldV, newV) -> oldV)); //旧的数据覆盖新的数据
	}

	public static Map<Long, String> getInfoDtoToMapMapForNew(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.filter(v -> v.getId() != null)
				.collect(Collectors.toMap(InfoDTO::getId,
						k -> k.getName(), //value获取原对象的name属性
						(oldV, newV) -> newV));
	}

	public static Map<Long, String> getInfoDtoToMapMapForNew2(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.filter(v -> v.getId() != null)
				.collect(Collectors.toMap(InfoDTO::getId,
						InfoDTO::getName, //value获取原对象的name属性
						(oldV, newV) -> newV));
	}

	//List<InfoDTO>->Map<Long,List<InfoDTO>> 简单分类 values->List<InfoDTO>
	public static Map<Long, List<InfoDTO>> getInfoDtoToMapList(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.filter(v -> v.getId() != null)
				.collect(
						Collectors.groupingBy(InfoDTO::getId)
				);
	}

	public static Map<Long, List<InfoDTO>> getInfoDtoToMapList2(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.filter(v -> v.getId() != null)
				.collect(
						Collectors.groupingBy(
								InfoDTO::getId,
								Collectors.mapping(Function.identity(), Collectors.toList())
						));
	}

	//List<InfoDTO>->Map<Long,List<String>> 简单分类 values->List<InfoDTO.getName>
	public static Map<Long, List<String>> getInfoDtoToMapNameList(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getId,
						Collectors.mapping(InfoDTO::getName, Collectors.toList())
				));
	}

	public static Map<String, Optional<InfoDTO>> getCategoryMaxAgeInfoDTO(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getCategory,
						Collectors.maxBy(Comparator.comparing(InfoDTO::getAge))
				));
	}

	public static Map<String, Optional<InfoDTO>> getCategoryMaxAgeInfoDTO2(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getCategory,
						Collectors.mapping(
								Function.identity(),
								Collectors.maxBy(Comparator.comparing(InfoDTO::getAge))
						)
				));
	}

	//List<InfoDTO>->Map<Long,Set<String>> 简单分类 values->Set<InfoDTO.getName>
	public static Map<Long, Set<String>> getInfoDtoToMapSet(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getId,
						Collectors.mapping(InfoDTO::getName, Collectors.toSet())
				));
	}

	//List<InfoDTO>->Map<Long, Map<String,List<InfoDTO>>> 多级分类
	public static Map<Long, Map<String, List<InfoDTO>>> getInfoDtoToMapMap(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.collect(Collectors.groupingBy(InfoDTO::getId, Collectors.groupingBy(InfoDTO::getCategory)));
	}

	public static void foreach() {
		List<String> list = new ArrayList<>();
		//1. 普通 for 循环（按索引遍历）
		//需要索引时使用（例如修改元素或定位）。
		//适合 ArrayList（随机访问高效），但对 LinkedList 性能较差（get(i) 是 O(n) 操作）。
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}

		//2. 增强 for-each 循环（推荐基础遍历）
		//语法简洁，无需处理索引。
		//所有 List 实现均高效（内部使用 Iterator）。
		//遍历中不能直接修改集合（会抛 ConcurrentModificationException）。
		for (String str : list) {
			System.out.println(str);
		}

		//3. 使用 Iterator 显式迭代
		//唯一能在遍历中安全删除元素的方式（iterator.remove()）。
		//适用于需要条件删除的场景。
		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext()) {
			String item = iterator.next();
			System.out.println(item);
			// 可安全删除元素：iterator.remove();
		}

		//4. 使用 ListIterator（双向遍历）
		//支持向前/向后遍历。
		//可获取当前索引（nextIndex()/previousIndex()）。
		ListIterator<String> listIterator = list.listIterator();
		while (listIterator.hasNext()) {
			String item = listIterator.next();
			System.out.println("Next: " + item);
		}
		// 反向遍历
		while (listIterator.hasPrevious()) {
			String item = listIterator.previous();
			System.out.println("Previous: " + item);
		}

		//5. Java 8+ 的 forEach + Lambda
		//代码简洁，内部使用增强 for-each 实现。
		//适合简单遍历（无法使用 break/continue）。
		list.forEach(v -> {
			System.out.println(v);
		});

		//6. 使用 Stream API（Java 8+）
		list.stream()
				.forEach(System.out::println);          // 最终操作

		//7. 并行流遍历（大数据量优化）
		list.parallelStream()
				.forEach(item -> System.out.println(item)); // 并行处理

	}

	public static void listAllSorted() {
		List<SortedInfo> sortedInfos = new ArrayList<>();
		sortedInfos.add(SortedInfo.builder()
				.id(1)
				.name("a")
				.age(2)
				.updateTime(LocalDateTime.now())
				.build());
		sortedInfos.add(SortedInfo.builder()
				.id(2)
				.name("c")
				.age(1)
				.updateTime(LocalDateTime.now())
				.build());
		sortedInfos.add(SortedInfo.builder()
				.id(3)
				.name("b")
				.age(2)
				.updateTime(LocalDateTime.now())
				.build());
		sortedInfos.add(SortedInfo.builder()
				.id(4)
				.name("b")
				.age(1)
				.updateTime(LocalDateTime.now())
				.build());

		// 1、先按 updateTime 降序，再按 age 升序
		// 1.1 使用Comparator.comparing配合lambda
		sortedInfos.sort(
				Comparator.comparing(SortedInfo::getUpdateTime)
						.reversed()
						.thenComparing(SortedInfo::getAge)
		);
		// 1.2 使用完整的lambda表达式
		sortedInfos.sort((v1, v2) -> {
			// 先按updateTime降序比较
			int timeCompare = v2.getUpdateTime()
					.compareTo(v1.getUpdateTime());
			if (timeCompare != 0) {
				return timeCompare;
			}
			// 再按age升序比较
			return Integer.compare(v1.getAge(), v2.getAge());
		});
		// 1.3 将排序逻辑提取为独立方法
		sortedInfos.sort(new Comparator<SortedInfo>() {
			@Override
			public int compare(SortedInfo u1, SortedInfo u2) {
				int timeCompare = u2.getUpdateTime()
						.compareTo(u1.getUpdateTime());
				if (timeCompare != 0) {
					return timeCompare;
				}
				return Integer.compare(u1.getId(), u2.getId());
			}
		});

		List<SortedInfoCompare> sortedInfoCompares = new ArrayList<>();
		LocalDateTime localDateTime = LocalDateTime.now();
		sortedInfoCompares.add(SortedInfoCompare.builder()
				.id(1)
				.name("a")
				.age(2)
				.updateTime(localDateTime)
				.build());
		sortedInfoCompares.add(SortedInfoCompare.builder()
				.id(2)
				.name("c")
				.age(1)
				.updateTime(localDateTime)
				.build());
		sortedInfoCompares.add(SortedInfoCompare.builder()
				.id(3)
				.name("b")
				.age(2)
				.updateTime(localDateTime)
				.build());
		sortedInfoCompares.add(SortedInfoCompare.builder()
				.id(4)
				.name("b")
				.age(1)
				.updateTime(localDateTime)
				.build());
		Collections.sort(sortedInfoCompares);
		System.out.println(sortedInfoCompares);

	}

	@Data
	@Builder
	public static class SortedInfo {
		private int id;
		private String name;
		private int age;
		private LocalDateTime updateTime;
	}

	@Data
	@Builder
	public static class SortedInfoCompare implements Comparable<SortedInfoCompare> {
		private int id;
		private String name;
		private int age;
		private LocalDateTime updateTime;

		@Override
		public int compareTo(SortedInfoCompare sortedInfoCompare) {
			int timeCompare = this.updateTime.compareTo(sortedInfoCompare.updateTime);
			if (timeCompare != 0) {
				return timeCompare;
			}
			return Integer.compare(this.id, sortedInfoCompare.id);
		}
	}
}

