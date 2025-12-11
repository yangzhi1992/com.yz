package com.commons.falcon.camel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileContentReload implements Processor {

	@Value("${app.rules.prometheus-url}")
	private String prometheusUrl;

	@Override
	public void process(Exchange exchange) throws Exception {
		String contentChanged = exchange.getIn().getHeader("contentChanged",String.class);
		if("true".equals(contentChanged)) {
			OkHttpClient client = new OkHttpClient();

			// 1. 构建 Basic Auth 认证头
			String credentials = Credentials.basic("admin", "ya157156");

			// 2. 构造 Request
			Request request = new Request.Builder()
					.url(prometheusUrl)
					.post(RequestBody.create("", null)) // 空请求体
					.header("Authorization", credentials)
					.build();

			// 3. 发送请求
			try (Response response = client.newCall(request).execute()) {
				if (response.isSuccessful()) {
					System.out.println("Prometheus 配置重载成功！");
				} else {
					System.out.println("请求失败: " + response.code() + " " + response.message());
				}
			} catch (IOException e) {
				System.out.println("网络错误: " + e.getMessage());
			}
		}
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