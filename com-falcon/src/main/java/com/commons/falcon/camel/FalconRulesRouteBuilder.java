package com.commons.falcon.camel;

import com.commons.falcon.dto.StrategyDTO;
import com.commons.falcon.service.FalconConverterPrometheus;
import com.commons.falcon.service.FalconStrategyServiceImpl;
import java.sql.SQLException;
import java.util.List;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FalconRulesRouteBuilder extends RouteBuilder {

	@Value("${app.rules.file-path}")
	private String rulesFilePath;

	@Value("${app.rules.scan-interval}")
	private long scanInterval;

	@Autowired
	private FalconStrategyServiceImpl falconStrategyService;

	@Autowired
	private FalconConverterPrometheus rulesConverter;

	@Override
	public void configure() throws Exception {

		// 错误处理
		errorHandler(defaultErrorHandler()
				.maximumRedeliveries(3)
				.redeliveryDelay(1000));

		// 主路由：每分钟扫描数据库并更新规则文件
		from("timer://falconRulesTimer?fixedRate=true&period=" + scanInterval)
				.routeId("falcon-rules-update-route")
				.log("开始扫描数据库策略...")
				.process(exchange -> {
					try {
						// 查询数据库
						List<StrategyDTO> strategies = falconStrategyService.queryStrategies();
						exchange.getIn()
								.setBody(strategies);
					} catch (SQLException e) {
						throw new RuntimeException("数据库查询失败", e);
					}
				})
				.process(exchange -> {
					try {
						@SuppressWarnings("unchecked")
						List<StrategyDTO> strategies = exchange.getIn()
								.getBody(List.class);

						// 转换为 Prometheus 规则
						String rulesYaml = rulesConverter.convertToPrometheusRules(strategies);
						exchange.getIn()
								.setBody(rulesYaml);

					} catch (Exception e) {
						throw new RuntimeException("规则转换失败", e);
					}
				})
				// 读取现有文件内容进行比较
				.process(new FileContentComparator())
				.choice()
				.when(header("contentChanged").isEqualTo(true))
				.log("检测到规则变化，更新文件...")
				.to("file:" + getDirectoryFromPath(rulesFilePath) +
						"?fileName=" + getFileNameFromPath(rulesFilePath) +
						"&fileExist=Override")
				.log("Prometheus 规则文件已更新: " + rulesFilePath)
				.process(new FileContentReload())
				.otherwise()
				.log("规则文件内容无变化，跳过更新")
				.end();
	}

	private String getDirectoryFromPath(String filePath) {
		int lastSlash = filePath.lastIndexOf("/");
		return lastSlash > 0 ? filePath.substring(0, lastSlash) : ".";
	}

	private String getFileNameFromPath(String filePath) {
		int lastSlash = filePath.lastIndexOf("/");
		return lastSlash > 0 ? filePath.substring(lastSlash + 1) : filePath;
	}
}