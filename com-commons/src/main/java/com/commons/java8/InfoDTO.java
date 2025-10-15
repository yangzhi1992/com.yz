package com.commons.java8;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfoDTO implements Serializable {
	private static final long serialVersionUID = -543178927786832714L;

	private Long id;

	private String name;

	private String category;

	private Double salary;

	private String department;

	private String gender;

	private int age;
}
