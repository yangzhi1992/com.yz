package com.commons.java8.business;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BusinessMan {
	public static void main(String[] args) {
		//用户观看直播记录
		List<UserViewHistoryDTO> userViewHistorys = new ArrayList<>();

		//根据userViewHistorys的liveStudioId获取anchorId 列表数据， liveStudioId->anchorId 多对一关系
		List<LiveStudioDTO> liveStudioDTOS = new ArrayList<>();

		//创建 studioId 到 anchorId 的映射：将 userViewHistorys 列表转换为 studioId -> anchorId 的 Map
		Map<Long, Long> studioToAnchorMap = liveStudioDTOS.stream()
				.collect(Collectors.toMap(
						LiveStudioDTO::getLiveStudioId, // Key: liveStudioId
						LiveStudioDTO::getAnchorId,     // Value: anchorId
						(existing, replacement) ->  existing)); // 合并函数：遇到重复key时保留已有值

		//生成 anchorId 的最大 viewTime 的映射
		Map<Long,Long> anchorIdViewTimeMap = userViewHistorys.stream()
				.filter(v -> studioToAnchorMap.containsKey(v.getLiveStudioId()))     // 1. 过滤
				.collect(Collectors.groupingBy(                                                        // 2. 分组
						history -> studioToAnchorMap.get(history.getLiveStudioId()), // 分组键：anchorId
						Collectors.mapping(                                                            // 3. 映射
								UserViewHistoryDTO::getViewTime,                                       // 提取viewTime
								Collectors.maxBy(Comparator.naturalOrder())                            // 找出最大值
						)
				))
				.entrySet().stream()                                                                    // 4. 转为流
				.filter(entry -> entry.getValue().isPresent())                   // 5. 过滤空值
				.collect(Collectors.toMap(                                                              // 6. 最终收集
						Map.Entry::getKey,                                                              // Key: anchorId
						entry -> entry.getValue().get()                          // Value: max viewTime
				));

		/**
		 * 详细步骤解析：
		 * 过滤阶段：
		 * 只保留 reqDTOList 中 studioId 存在于 studioToAnchorMap 中的元素
		 * 分组收集：
		 *
		 * 按 anchorId 分组（通过 studioId 查找对应的 anchorId）
		 * 对每组中的元素：
		 * 提取 viewTime 字段
		 * 使用 maxBy 找出每组中的最大 viewTime
		 * 中间结果：
		 *
		 * 此时得到的是 Map<Long, Optional<Long>> 结构
		 * Key 是 anchorId
		 * Value 是该 anchorId 下所有 viewTime 的最大值（包装在 Optional 中）
		 * 后处理：
		 *
		 * 将 Map 的 entrySet 转为流
		 * 过滤掉值为空的条目（Optional 为空的）
		 * 最终收集为 Map<Long, Long>，其中：
		 * Key 是 anchorId
		 * Value 是该 anchorId 下的最大 viewTime
		 */
	}
}
