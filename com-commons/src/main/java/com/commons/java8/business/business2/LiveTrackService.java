package com.commons.java8.business.business2;

import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LiveTrackService {
	public static void main(String[] args) {
		List<Long> partnerIdList = Arrays.asList(20L,19L,3L,16L,5L);

		List<LiveTrackDTO> liveTrackDTOS = new ArrayList<>();
		List<LiveTrackDTO> filteredAndSortedList = liveTrackDTOS.stream()
				.filter(track -> {
					return (track.getPlayPlatform() == -1 || track.getPlayPlatform() == 12 || track.getPlayPlatform() == 10) &&
							track.getPlatformAvailableStatus() == 1 &&
							track.getPlatformRejectSearch() == 0 &&
							track.getPlatformRejectRecommend() == 0;
				})
				// 第一步：按anchorId分组
				.collect(Collectors.groupingBy(LiveTrackDTO::getAnchorId))
				.values()
				.stream()
				// 第二步：处理每个anchorId的多条记录
				.map(anchorTracks -> {
					// 按照liveTrackId分组
					Map<Long, List<LiveTrackDTO>> liveTrackIdMap = anchorTracks.stream()
							.collect(Collectors.groupingBy(LiveTrackDTO::getLiveTrackId));

					// 过滤platPlatform条件-对应liveTrackId进行分组
					List<LiveTrackDTO> platformFiltered = anchorTracks.stream()
							.filter(track -> {
								if(track.getPlayPlatform() == 12 || track.getPlayPlatform() == 10) {
									List<LiveTrackDTO> list = liveTrackIdMap.get(track.getLiveTrackId());
									AtomicBoolean ios = new AtomicBoolean(false);
									AtomicBoolean andorid = new AtomicBoolean(false);
									list.forEach(v->{
										if(v.getPlayPlatform() == 12) {
											ios.set(true);
										}
										if(v.getPlayPlatform() == 10) {
											andorid.set(true);
										}
									});
									if(ios.get() && andorid.get()) {
										return true;
									}
								}

								// 保留条件：platPlatform为-1 或者 (12和10同时存在)
								return track.getPlayPlatform() == -1;
							})
							.collect(Collectors.toList());

					// 过滤完的数据进行分组：按partnerId分组
					Map<Long, List<LiveTrackDTO>> partnerGroups = platformFiltered.stream()
							.collect(Collectors.groupingBy(LiveTrackDTO::getPartnerId));

					// 按照partnerId优先级进行获取每个业务方每个anchorId只获取一次
					List<LiveTrackDTO> selectedTracks = Stream.of(1,2,3)
							.filter(partnerGroups::containsKey)
							.findFirst()
							.map(partnerGroups::get)
							.orElse(new ArrayList<>());

					// 按realStartTime升序排序，取第一条
					return selectedTracks.stream()
							.sorted(Comparator.comparing(LiveTrackDTO::getRealStartTime))
							.findFirst()
							.orElse(null);
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		//优先按照partnerIdList的index升序，然后按照realStartTime升序排序
		filteredAndSortedList.sort(
				Comparator.comparingInt((LiveTrackDTO v) -> partnerIdList.indexOf(v.getPartnerId()))
				.thenComparing(LiveTrackDTO::getRealStartTime)
		);

		String[] liveArray = filteredAndSortedList.stream()
				.map(JSON::toJSONString)
				.toArray(String[]::new);
	}
}
