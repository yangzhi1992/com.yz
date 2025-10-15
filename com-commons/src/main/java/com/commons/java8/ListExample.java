package com.commons.java8;

import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
	}

	//map InfoDTO->String 1->1 数据量越大效果越好（通常 >10,000元素）
	public static List<String> getInfoDtoNameToListByParallelStream(List<InfoDTO> infoDTOS) {
		return infoDTOS.parallelStream().map(InfoDTO::getName).collect(Collectors.toList());
	}

	//map InfoDTO->String 1->1
	public static List<String> getInfoDtoNameToList(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream().map(InfoDTO::getName).collect(Collectors.toList());
	}

	//List->String
	public static String getInfoDtoNameToString(List<InfoDTO> infoDTOS) {
		return String.join(",",getInfoDtoNameToList(infoDTOS));
	}
	public static String getInfoDtoNameToString1(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream().map(InfoDTO::getName).collect(Collectors.joining(","));
	}
	public static String getInfoDtoNameToString2(List<InfoDTO> infoDTOS) {
		return StringUtils.join(getInfoDtoNameToList(infoDTOS),",");
	}

	//List<List<InfoDTO>> -> List<InfoDTO> flatMap 1->多
	public static List<InfoDTO> getInfoDtoToList(List<List<InfoDTO>> infoDTOS) {
		return infoDTOS.stream().flatMap(List::stream).collect(Collectors.toList());
	}

	//List->Set
	public static Set<InfoDTO> getInfoDtoToSet(List<InfoDTO> infoDTOS) {
		return new HashSet<>(infoDTOS);
	}
	public static Set<InfoDTO> getInfoDtoToSet1(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream().collect(Collectors.toSet());
	}

	//List<InfoDTO>->Map<Long,InfoDTO> 简单分类
	public static Map<Long, InfoDTO> getInfoDtoToMapForNew(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.filter(v -> v.getId() != null)
				.collect(Collectors.toMap(InfoDTO::getId, k -> k, (oldV, newV) -> newV));
	}
	public static Map<Long, InfoDTO> getInfoDtoToMapForOld(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.filter(v -> v.getId() != null)
				.collect(Collectors.toMap(InfoDTO::getId, k -> k, (oldV, newV) -> oldV));
	}
	public static Map<Long, String> getInfoDtoToMapMapForNew(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.filter(v -> v.getId() != null)
				.collect(Collectors.toMap(InfoDTO::getId, k -> k.getName(), (oldV, newV) -> newV));
	}
	//List<InfoDTO>->Map<Long,List<InfoDTO>> 简单分类
	public static Map<Long, List<InfoDTO>> getInfoDtoToMapList(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.filter(v -> v.getId() != null)
				.collect(
						Collectors.groupingBy(InfoDTO::getId)
				);
	}

	//List<InfoDTO>->Map<Long,Set<String>> 简单分类
	public static Map<Long, Set<String>> getInfoDtoToMapSet(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getId,
						Collectors.mapping(InfoDTO::getName, Collectors.toSet())
				));
	}

	//List<InfoDTO>->Map<Long, Map<String,List<InfoDTO>>> 多级分类
	public static Map<Long, Map<String,List<InfoDTO>>> getInfoDtoToMapMap(List<InfoDTO> infoDTOS) {
		return infoDTOS.stream()
				.collect(Collectors.groupingBy(InfoDTO::getId, Collectors.groupingBy(InfoDTO::getCategory)));
	}
}

