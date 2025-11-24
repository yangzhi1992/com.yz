package com.commons.falcon.service;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.commons.falcon.camel.FileContentReload;
import com.commons.falcon.dto.StrategyDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FalconStrategyServiceImpl {

	private static final Logger log = LoggerFactory.getLogger(FileContentReload.class);

	@Value("${app.rules.mysql-datasource}")
	private String datasourceName;

	public List<StrategyDTO> queryStrategies() throws SQLException {
		// 创建数据库连接
		Db db = Db.use(datasourceName);

		List<Entity> entities = db.query(
				"SELECT\n"
						+ "  metric,tags,func,op, right_value\n"
						+ "FROM\n"
						+ "  `strategy`\n"
						+ "WHERE tags NOT IN (\n"
						+ "    'partner=ppclive-core',\n"
						+ "    'partner=ppclive-job',\n"
						+ "    'partner=qlive-user-server',\n"
						+ "    'partner=qlive-user',\n"
						+ "    'partner=qlive-sensor-jobpy',\n"
						+ "    'partner=qlive-sensor-job',\n"
						+ "    'partner=qlive-sensor-api',\n"
						+ "    'partner=qlive-sensor-admin',\n"
						+ "    'partner=qlive-recommend-server',\n"
						+ "    'partner=qlive-play-server',\n"
						+ "    'partner=qlive-play-job',\n"
						+ "    'partner=qlive-im-server',\n"
						+ "    'partner=qlive-gift-server',\n"
						+ "    'partner=qlive-play',\n"
						+ "    'partner=qlive-pgc-job',\n"
						+ "    'partner=qlive-pgc',\n"
						+ "    'clustername=qlive-image',\n"
						+ "    'clustername=qlive-im',\n"
						+ "    'clustername=qlive-gift',\n"
						+ "    'partner=qlive-live-user',\n"
						+ "    'clustername=qlive-pgc-admin',\n"
						+ "    'clustername=qlive-live',\n"
						+ "    'clustername=qlive-job',\n"
						+ "    'partner=qlive-live-server',\n"
						+ "    'clustername=qlive-user',\n"
						+ "    'clustername=qlive-recommend',\n"
						+ "    'clustername=qlive-ppc-job',\n"
						+ "    'partner=qlive-live',\n"
						+ "    'partner=glive-im-server',\n"
						+ "    'partner=epg-api-server',\n"
						+ "    'paratner=qlive-edge-server',\n"
						+ "    'partner=qlive-job',\n"
						+ "    'partner=ppc_live',\n"
						+ "    'partner=play-service',\n"
						+ "    'partner=play-server',\n"
						+ "    'partner=play-job',\n"
						+ "    'partner=pgclive-admin',\n"
						+ "    'partner=qlive-image',\n"
						+ "    'partner=qlive-hub-job',\n"
						+ "    'partner=qlive-epg-server',\n"
						+ "    'partner=qlive-edge-server',\n"
						+ "    'partner=qlive-edge-saturn',\n"
						+ "    'partner=qlive-edge-job',\n"
						+ "    'partner=qlive-admin-node'\n"
						+ "  ) AND tags IS NOT NULL AND tags <> '' ORDER BY id DESC"
		);
		entities.forEach(v -> {
			try {
				log.debug("metric：" + v.get("metric") + ",tags：" + v.getStr("tags")
						.split("=")[1]);
			} catch (Exception e) {
				log.error("error:" + v.getStr("tags"));
			}
		});

		List<StrategyDTO> strategyList = entities.stream()
				.map(v -> {
					StrategyDTO strategyDTO = StrategyDTO.builder()
							.id(v.getLong("id"))
							.metric(v.getStr("metric"))
							.tags(v.getStr("tags"))
							.func(v.getStr("func"))
							.op(v.getStr("op"))
							.rightValue(v.getStr("right_value"))
							.build();
					return strategyDTO;
				})
				.collect(Collectors.toList());
		return strategyList;
	}
}
