package com.commons.jsonforjackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * 属性控制注解
 */
@Data
@Builder
public class BaseJackson {
	@JsonView(Views.Public.class)
	@JsonProperty("user_name") // 自定义JSON字段名
	private String username;

	@JsonView(Views.Internal.class)
	private String password;

	@JsonView(Views.Admin.class)
	@JsonInclude(Include.NON_NULL) // 仅当非null时包含
	private String email;

	// 无注解字段默认不包含在任何视图
	private LocalDateTime createdAt;

	@JsonIgnore // 忽略该属性
	private LocalDateTime updateAt;

	public static class Views {
		// 公共视图 - 基本字段
		public interface Public {}

		// 内部视图 - 包含敏感信息
		public interface Internal extends Public {}

		// 管理员视图 - 包含所有信息
		public interface Admin extends Internal {}
	}

	public static void main(String[] args) throws Exception {
		ObjectMapper mapper = new ObjectMapper()
				// 注册Java8时间模块
                .registerModule(new JavaTimeModule());
		BaseJackson baseJackson = BaseJackson.builder()
				.username("小米").email("11@qq.com").password("123").createdAt(LocalDateTime.now())
				.build();
		// 使用公共视图
		String publicJson = mapper.writerWithView(Views.Public.class)
				.writeValueAsString(baseJackson);
		System.out.println(publicJson);
		// 结果: {"createdAt":[2025,10,16,16,54,20,111000000],"user_name":"小米"}

		// 使用内部视图
		String internalJson = mapper.writerWithView(Views.Internal.class)
				.writeValueAsString(baseJackson);
		System.out.println(internalJson);
		// 结果: {"password":"123","createdAt":[2025,10,16,16,54,20,111000000],"user_name":"小米"}

		// 使用管理员视图
		String adminJson = mapper.writerWithView(Views.Admin.class)
				.writeValueAsString(baseJackson);
		System.out.println(adminJson);
		// 结果： {"password":"123","email":"11@qq.com","createdAt":[2025,10,16,16,54,20,111000000],"user_name":"小米"}
	}

}