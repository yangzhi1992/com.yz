package com.commons.boomfilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class BloomFilterExample {
    public static void main(String[] args) {
        // 创建布隆过滤器：总容量 1000，误判率 0.01（1%）
        BloomFilter<Integer> bloomFilter = BloomFilter.create(
            Funnels.integerFunnel(), // Integer 类型处理器
            1000,                    // 总容量（预计要存储的元素数量）
            0.01                     // 容忍误判率（false positive rate）
        );

        // 添加元素
        for (int i = 0; i < 1000; i++) {
            bloomFilter.put(i);
        }

        // 检测元素是否可能存在
        System.out.println(bloomFilter.mightContain(500)); // true
        System.out.println(bloomFilter.mightContain(2000)); // false（大概率）

        // 检测"误判情况"（可能发生假阳性情况）
        int falsePositive = 0;
        for (int i = 1000; i < 2000; i++) {
            if (bloomFilter.mightContain(i)) {
                falsePositive++;
            }
        }
        System.out.println("误判率：" + (falsePositive / 1000.0));
    }
}
