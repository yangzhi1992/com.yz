package com.commons.java8;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MapExample {
	public static void main(String[] args) {
		List<InfoDTO> infoDTOS = new ArrayList<>();

		// 每个部门的平均工资
		Map<String, Double> avgSalaryByDept = infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getDepartment,
						Collectors.averagingDouble(InfoDTO::getSalary)
				));

		// 每个部门的工资总和
		Map<String, Double> sumSalaryByDept = infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getDepartment,
						Collectors.summingDouble(InfoDTO::getSalary)
				));

		// 每个部门的员工数
		Map<String, Long> countByDept = infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getDepartment,
						Collectors.counting()
				));

		// 每个部门工资最高的员工
		Map<String, Optional<InfoDTO>> maxSalaryByDept = infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getDepartment,
						Collectors.maxBy(Comparator.comparing(InfoDTO::getSalary))
				));

		// 获取实际值而非Optional
		Map<String, InfoDTO> maxSalaryByDept2 = infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getDepartment,
						Collectors.collectingAndThen(
								Collectors.maxBy(Comparator.comparing(InfoDTO::getSalary)),
								Optional::get
						)
				));

		// 按年龄段分组
		Map<String, List<InfoDTO>> byAgeGroup = infoDTOS.stream()
				.collect(Collectors.groupingBy(emp -> {
					int age = emp.getAge();
					if (age < 25) return "Under 25";
					else if (age < 35) return "25-34";
					else if (age < 45) return "35-44";
					else return "45+";
				}));

		// 分组后收集为Set而不是List
		Map<String, Set<InfoDTO>> byDeptSet = infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getDepartment,
						Collectors.toSet()
				));

		// 分组后只收集员工姓名
		Map<String, List<String>> namesByDept = infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getDepartment,
						Collectors.mapping(InfoDTO::getName, Collectors.toList())
				));

		// 分组后收集为逗号分隔的字符串
		Map<String, String> namesStrByDept = infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getDepartment,
						Collectors.mapping(InfoDTO::getName,
								Collectors.joining(", "))
				));


		// 根据用户选择的多个字段动态分组
		List<String> groupFields = Arrays.asList("department", "gender");
		Map<List<Object>, List<InfoDTO>> dynamicGroups = infoDTOS.stream()
				.collect(Collectors.groupingBy(emp ->
						groupFields.stream()
								.map(field -> {
									try {
										Method method = InfoDTO.class.getMethod("get" +
												field.substring(0, 1).toUpperCase() + field.substring(1));
										return method.invoke(emp);
									} catch (Exception e) {
										throw new RuntimeException(e);
									}
								})
								.collect(Collectors.toList())
				));

		// 按部门分组，每组员工按工资降序排列
		Map<String, List<InfoDTO>> sortedBySalary = infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getDepartment,
						Collectors.collectingAndThen(
								Collectors.toList(),
								list -> {
									list.sort(Comparator.comparing(InfoDTO::getSalary).reversed());
									return list;
								}
						)
				));


		// 每个部门取工资最高的3名员工
		Map<String, List<InfoDTO>> top3ByDept = infoDTOS.stream()
				.collect(Collectors.groupingBy(
						InfoDTO::getDepartment,
						Collectors.collectingAndThen(
								Collectors.toList(),
								list -> list.stream()
										.sorted(Comparator.comparing(InfoDTO::getSalary).reversed())
										.limit(3)
										.collect(Collectors.toList())
						)
				));

		// 只保留员工数大于5的部门
		Map<String, List<InfoDTO>> largeDepts = infoDTOS.stream()
				.collect(Collectors.groupingBy(InfoDTO::getDepartment))
				.entrySet().stream()
				.filter(entry -> entry.getValue().size() > 5)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

	}
}
