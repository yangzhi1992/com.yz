package com.commons.jsonforjackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnumEntity {

	// 序列化为对象
	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	private OrderStatus orderStatus;

	// 序列化为字符串（默认）
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private UserType userType;

	// 序列化为数字
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Priority priority;

	// 枚举定义
    @AllArgsConstructor
	public static enum OrderStatus {
		PENDING("待处理", 1),
		PROCESSING("处理中", 2),
		COMPLETED("已完成", 3),
		CANCELLED("已取消", 4);

		private String description;
		private int code;

    }

	public static enum UserType {
		ADMIN, USER, GUEST
	}

	public static enum Priority {
		LOW, MEDIUM, HIGH
	}
}
