package com.commons.hibernateValidator.java;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UniqueElements;

public class CheckValidatorExample {
	/**
	 * 1. 空值检查
	 */
	public static class NullCheckExample {
		@NotNull(message = "字段不能为null")
		private String notNullField;

		@Null(message = "字段必须为null")
		private String mustBeNullField;

		@NotEmpty(message = "字符串/集合不能为null或空")
		private String notEmptyString;

		@NotBlank(message = "字符串不能为空且必须包含非空白字符")
		private String notBlankString;
	}

	/**
	 * 2. 布尔检查
	 */
	public static class BooleanCheckExample {
		@AssertTrue(message = "必须为true")
		private boolean mustBeTrue;

		@AssertFalse(message = "必须为false")
		private boolean mustBeFalse;
	}

	/**
	 * 3. 数值检查
	 */
	public static class NumberCheckExample {
		@Min(value = 10, message = "最小值10")
		private int minValue;

		@Max(value = 100, message = "最大值100")
		private int maxValue;

		@DecimalMin(value = "0.5", message = "最小0.5")
		private BigDecimal decimalMin;

		@DecimalMax(value = "10.5", message = "最大10.5")
		private BigDecimal decimalMax;

		@Digits(integer = 3, fraction = 2, message = "整数3位，小数2位")
		private BigDecimal digitLimited;

		@Positive(message = "必须为正数")
		private int positiveNumber;

		@PositiveOrZero(message = "必须为正数或零")
		private int positiveOrZero;

		@Negative(message = "必须为负数")
		private int negativeNumber;

		@NegativeOrZero(message = "必须为负数或零")
		private int negativeOrZero;
	}

	/**
	 * 4. 字符串检查
	 */
	public static class StringCheckExample {
		@Size(min = 2, max = 10, message = "长度2-10")
		private String sizedString;

		@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "只允许字母数字")
		private String alphanumeric;

		@Email(message = "必须是有效邮箱")
		private String email;

		@URL(protocol = "https", host = "example.com", message = "必须是https://example.com的URL")
		private String website;
	}

	/**
	 * 5. 日期时间检查
	 */
	public static class DateTimeCheckExample {
		@Past(message = "必须是过去时间")
		private Date pastDate;

		@PastOrPresent(message = "必须是过去或现在时间")
		private LocalDateTime pastOrPresent;

		@Future(message = "必须是未来时间")
		private Instant futureTime;

		@FutureOrPresent(message = "必须是未来或现在时间")
		private ZonedDateTime futureOrPresent;
	}

	/**
	 * 6. 集合和数组验证
	 */
	public static class CollectionCheckExample {
		@Size(min = 1, max = 5, message = "集合大小1-5")
		private List<String> sizedList;

		@javax.validation.constraints.NotEmpty(message = "数组不能为空")
		private String[] notEmptyArray;

		@Valid // 级联验证集合元素
		private List<@NotNull Address> addresses;
	}

	/**
	 * 7. 自定义对象验证
	 */
	@Data
	public static class NestedObjectExample {
		@Valid // 级联验证
		private Address address;

		@Valid // 级联验证集合
		private List<Phone> phones;

	}

	/**
	 * 9. 范围验证
	 */
	public class RangeExample {
		@Range(min = 1, max = 100, message = "范围1-100")
		private int rangedValue;
	}

	/**
	 * 10. 唯一元素验证
	 */
	public class UniqueExample {
		@UniqueElements(message = "元素必须唯一")
		private List<String> uniqueList;
	}

	/**
	 * 11. 脚本验证
	 */
	public class ScriptExample {
		@ScriptAssert(lang = "javascript", script = "_this.start.before(_this.end)",
				message = "开始时间必须在结束时间之前")
		public class Event {
			private Date start;
			private Date end;
		}
	}


	public static class Address {
		@javax.validation.constraints.NotBlank
		private String street;

		@javax.validation.constraints.NotBlank
		private String city;
	}

	public static class Phone {
		@Pattern(regexp = "^\\+?[0-9]{7,15}$")
		private String number;
	}

}
