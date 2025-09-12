package com.commons.engine.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import java.util.HashMap;
import java.util.Map;

public class DynamicRuleEngineDemo {
    public static void main(String[] args) {
        // 定义规则：如果是普通用户并且订单总额 > 100，则享受 20% 折扣
        String rule = "userType == 'VIP' ? orderTotal * 0.8 : " +
                      "(orderTotal > 100 ? orderTotal * 0.9 : orderTotal)";

        // 创建动态变量（上下文）
        Map<String, Object> env = new HashMap<>();
        env.put("userType", "VIP"); // 用户类型：VIP
        env.put("orderTotal", 120.0); // 订单总金额：120

        // 执行规则
        Object result = AviatorEvaluator.execute(rule, env);
        System.out.println("最终应付金额: " + result); // 输出：最终应付金额: 96.0
    }
}
