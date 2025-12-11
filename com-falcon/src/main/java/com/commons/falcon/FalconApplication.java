package com.commons.falcon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FalconApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(FalconApplication.class, args);

		Thread.currentThread().join();
	}
}