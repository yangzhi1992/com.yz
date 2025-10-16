package com.commons.hibernateValidator.java;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class CheckValidatorExec {
	public static void main(String[] args) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		UserRegistrationForm user = new UserRegistrationForm();
		user.setUsername("a");  // 违反@Size
		user.setPassword("weak"); // 违反@Pattern
		user.setEmail("invalid"); // 违反@Email
		user.setAge(17); // 违反@Min
		user.setTermsAccepted(false); // 违反@AssertTrue

		Set<ConstraintViolation<UserRegistrationForm>> violations = validator.validate(user);

		violations.forEach(v -> {
			System.out.printf("%s: %s (实际值: %s)%n",
					v.getPropertyPath(),
					v.getMessage(),
					v.getInvalidValue());
		});
	}
}
