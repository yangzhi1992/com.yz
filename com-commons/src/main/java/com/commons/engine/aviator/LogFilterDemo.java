package com.commons.engine.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogFilterDemo {
    public static void main(String[] args) {
        // 日志数据（每条日志是一个 Map）
        List<Map<String, Object>> logs = new ArrayList<>();
        logs.add(Map.of("level", "INFO", "message", "User login successful"));
        logs.add(Map.of("level", "ERROR", "message", "Database connection failed"));
        logs.add(Map.of("level", "DEBUG", "message", "Debugging user module"));
        logs.add(Map.of("level", "WARN", "message", "User quota nearing limit"));

        // 定义筛选条件：日志级别是 ERROR 或消息中包含 "User"
        String filterExpression = "level == 'ERROR' || string.contains(message, 'User')";

        // 遍历日志并筛选符合条件的日志
        logs.stream()
            .filter(log -> (boolean) AviatorEvaluator.execute(filterExpression, log))
            .forEach(log -> System.out.println("筛选日志: " + log));
    }
}
