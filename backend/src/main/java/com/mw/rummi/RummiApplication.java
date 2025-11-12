package com.mw.rummi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RummiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RummiApplication.class, args);
		Rules.defaultRules();
	}

}
