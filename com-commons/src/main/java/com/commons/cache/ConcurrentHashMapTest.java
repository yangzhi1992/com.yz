package com.commons.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapTest {
    public static void main(String[] args) {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap();
        /**
         * put 返回null或者上一个存放的value
         */
        concurrentHashMap.put(1, 1);
        concurrentHashMap.put(2, 2);
        Integer value = (Integer)concurrentHashMap.put(2, 3);
        System.out.println(value);//返回2
        Integer value2 = (Integer)concurrentHashMap.put(3, 4);
        System.out.println(value2);//返回null

        /**
         * 只有在 key 不存在或者映射为 null 时，才放入键值对
         */
        Integer value3 = (Integer)concurrentHashMap.putIfAbsent(1, 2);
        System.out.println(value3);//返回1
        System.out.println(concurrentHashMap.get(1));//返回1
        //适合map的value嵌套List
        ConcurrentHashMap<Integer, List<Integer>> concurrentHashMap2 = new ConcurrentHashMap();
        concurrentHashMap2.putIfAbsent(1, new ArrayList<>());
        concurrentHashMap2.get(1)
                          .add(1);
        concurrentHashMap2.putIfAbsent(1, new ArrayList<>());
        System.out.println(concurrentHashMap2.get(1));//返回【1】

        /**
         * 尝试为 key 计算一个新的映射值;原子操作
         * 流程：
         * 获取 key 的当前值。
         * 将 key 和当前值传给 remappingFunction 函数，计算出新值。
         * 如果新值不为 null，则将 (key, 新值) 放入 Map。
         * 如果新值为 null，并且 key 存在，则移除该 key 的映射。
         */
        concurrentHashMap.compute(1, (k, currentValue) -> {
            if (currentValue == null) {
                return 1;
            } else {
                return currentValue + 1;
            }
        });
        System.out.println(concurrentHashMap.get(1));

        concurrentHashMap.compute(4, (k, currentValue) -> {
            if (currentValue == null) {
                return 1;
            } else {
                return currentValue + 1;
            }
        });
        System.out.println(concurrentHashMap.get(4));

        /**
         * 如果 key 不存在（或映射为 null），则使用 mappingFunction 计算其值并放入 Map。
         * 由于原先key 1的value值为 2所以8赋值无效
         */
        concurrentHashMap.computeIfAbsent(1, k -> {
            return 8;
        });
        System.out.println(concurrentHashMap.get(1));
        concurrentHashMap.computeIfAbsent(9, k -> {
            return 8;
        });
        System.out.println(concurrentHashMap.get(9));

        /**
         * 如果 key 存在（且不为 null），则根据旧值计算一个新值并更新。
         */
        concurrentHashMap.computeIfPresent(1, (k, currentValue) -> {
            if (currentValue == null) {
                return 1;
            } else {
                return currentValue + 1;
            }
        });
        System.out.println(concurrentHashMap.get(1));
        concurrentHashMap.computeIfPresent(10, (k, currentValue) -> {
            if (currentValue == null) {
                return 10;
            } else {
                return currentValue + 10;
            }
        });
        System.out.println(concurrentHashMap.get(10));

    }
}

