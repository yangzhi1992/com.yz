package com.commons.falcon.camel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileContentComparator implements Processor {

	private static final Logger log = LoggerFactory.getLogger(FileContentComparator.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		String newContent = exchange.getIn().getBody(String.class);
		String filePath = exchange.getContext()
				.resolvePropertyPlaceholders("{{app.rules.file-path}}");

		boolean contentChanged = true;
		String existingContent = null;

		try {
			// 读取现有文件内容
			File file = new File(filePath);
			if (file.exists()) {
				existingContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

				// 比较内容是否相同（忽略首尾空白字符）
				if (normalizeContent(newContent).equals(normalizeContent(existingContent))) {
					contentChanged = false;
					log.debug("文件内容相同，无需更新");
				} else {
					log.info("检测到文件内容变化");
					log.debug("新内容:\n{}", newContent);
					log.debug("旧内容:\n{}", existingContent);
				}
			} else {
				log.info("规则文件不存在，将创建新文件");
			}
		} catch (IOException e) {
			log.warn("读取现有规则文件失败，将创建新文件", e);
		}

		// 设置比较结果头信息
		exchange.getIn()
				.setHeader("contentChanged", contentChanged);

		// 确保body是新内容
		exchange.getIn()
				.setBody(newContent);
	}

	/**
	 * 标准化内容，用于比较 移除首尾空白，统一换行符等
	 */
	private String normalizeContent(String content) {
		if (content == null) {
			return "";
		}
		return content.trim()
				.replaceAll("\\r\\n", "\n")  // 统一换行符
				.replaceAll("\\r", "\n")     // 处理Mac换行符
				.replaceAll("\\s+$", "")     // 移除行尾空白
				.replaceAll("^\\s+", "");    // 移除行首空白
	}
}