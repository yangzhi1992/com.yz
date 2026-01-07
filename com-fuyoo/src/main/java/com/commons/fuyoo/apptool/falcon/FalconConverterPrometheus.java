package com.commons.fuyoo.apptool.falcon;

import com.alibaba.fastjson2.JSONObject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FalconConverterPrometheus {
	private static final Map<String, String> SEVERITY_MAP = new HashMap<>();
	private static final int DEFAULT_SCRAPE_INTERVAL = 20;

	static {
		// 初始化严重性映射（可根据实际业务调整）
		SEVERITY_MAP.put("ThreadPoolFullError", "critical");
		SEVERITY_MAP.put("KafkaConsumerError", "critical");
		SEVERITY_MAP.put("UnhandledException", "critical");
		SEVERITY_MAP.put("SqlError", "warning");
		SEVERITY_MAP.put("RedisError", "warning");
		// 默认严重性
		SEVERITY_MAP.put("default", "warning");
	}

	public String convertToPrometheusRules(List<JSONObject> strategyDTOS) throws SQLException {
		StringBuilder yamlContent = new StringBuilder();
		yamlContent.append("groups:\n");
		yamlContent.append("- name: falcon.rules\n");
		yamlContent.append("  rules:\n");
		strategyDTOS.forEach(v -> {
			try {
				String ruleYaml = convertJsonRuleToYaml(v);
				yamlContent.append(ruleYaml)
						.append("\n");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return yamlContent.toString();
	}

	private static String convertJsonRuleToYaml(JSONObject strategyDTO) throws Exception {
		String metric = strategyDTO.getString("metric");
		String tags = strategyDTO.getString("tags");
		String func = strategyDTO.getString("func");
		String op = strategyDTO.getString("op");
		String rightValue = strategyDTO.getString("rightValue");

		// 解析tags
		Map<String, String> tagMap = parseTags(tags);

		// 构建PromQL表达式
		String promQL = buildPromQL(metric, tagMap, func, op, rightValue);

		// 获取严重性级别
		String severity = SEVERITY_MAP.getOrDefault(metric, SEVERITY_MAP.get("default"));

		// 构建YAML规则
		StringBuilder yaml = new StringBuilder();
		yaml.append("  ")
				.append("- alert: ")
				.append(tags)
				.append("_")
				.append(metric)
				.append("\n");
		yaml.append("  ")
				.append("  expr: \n");
		yaml.append("  ")
				.append("    ")
				.append(promQL)
				.append("\n");
		yaml.append("  ")
				.append("  for: 0m\n");
		yaml.append("  ")
				.append("  labels:\n");
		yaml.append("  ")
				.append("    severity: ")
				.append(severity)
				.append("\n");
		yaml.append("  ")
				.append("  annotations:\n");
		yaml.append("  ")
				.append("    summary: ")
				.append(generateSummary(metric, tagMap, func, op, rightValue))
				.append("\n");

		return yaml.toString();
	}

	private static Map<String, String> parseTags(String tags) {
		Map<String, String> tagMap = new HashMap<>();
		String[] pairs = tags.split(",");
		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			if (keyValue.length == 2) {
				tagMap.put(keyValue[0].trim(), keyValue[1].trim());
			}
		}
		return tagMap;
	}

	private static String buildPromQL(String metric, Map<String, String> tagMap,
			String func, String op, String rightValue) {
		// 解析func
		FuncInfo funcInfo = parseFunc(func);

		// 构建标签选择器
		StringBuilder labelSelector = new StringBuilder();
		labelSelector.append("logType=\"")
				.append(metric)
				.append("\"");
		for (Map.Entry<String, String> entry : tagMap.entrySet()) {
			labelSelector.append(", ")
					.append(entry.getKey())
					.append("=\"")
					.append(entry.getValue())
					.append("\"");
		}

		// 计算时间范围
		int windowMinutes = DEFAULT_SCRAPE_INTERVAL * ((funcInfo.points - 1) > 0 ? (funcInfo.points - 1) : 1);
		String rangeSelector = windowMinutes + "s:" + DEFAULT_SCRAPE_INTERVAL + "s";

		// 选择聚合函数
		String aggFunc;
		switch (funcInfo.funcType) {
			case "all":
				aggFunc = "min_over_time";
				break;
			case "sum":
				aggFunc = "sum_over_time";
				break;
			default:
				aggFunc = "min_over_time";
		}

		if ("==".equals(op) && "0".equals(rightValue)) {
			op = ">";
		}

		if (">=".equals(op) && "0".equals(rightValue)) {
			op = ">";
		}
		// 构建完整PromQL
		return String.format("%s(sum by (logType, partner) (increase(request_app_log_total{%s}[1m]))[%s]) %s %s",
				aggFunc, labelSelector.toString(), rangeSelector, op, rightValue);
	}

	private static FuncInfo parseFunc(String func) {
		FuncInfo funcInfo = new FuncInfo();

		if (func.startsWith("all(")) {
			funcInfo.funcType = "all";
		} else if (func.startsWith("sum(")) {
			funcInfo.funcType = "sum";
		} else {
			funcInfo.funcType = "all"; // 默认
		}

		// 提取点数
		String pointsStr = func.replaceAll("[^0-9]", "");
		funcInfo.points = pointsStr.isEmpty() ? 1 : Integer.parseInt(pointsStr);

		return funcInfo;
	}

	private static String generateSummary(String metric, Map<String, String> tagMap, String func, String op, String rightValue) {
		FuncInfo funcInfo = parseFunc(func);
		if ("==".equals(op) && "0".equals(rightValue)) {
			op = ">";
		}

		if (">=".equals(op) && "0".equals(rightValue)) {
			op = ">";
		}

		String partner = tagMap.getOrDefault("partner", "unknown");

		int windowMinutes = DEFAULT_SCRAPE_INTERVAL * ((funcInfo.points - 1) > 0 ? (funcInfo.points - 1) : 1);
		return partner + "-" + metric + " 触发报警规则:持续" + windowMinutes + "秒每" + DEFAULT_SCRAPE_INTERVAL + "秒统计" + op + " " + rightValue;
	}

	static class FuncInfo {
		String funcType; // "all" or "sum"
		int points;      // number of points
	}
}
