package com.project.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "persistences")
public class AuthoriseandauthenApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthoriseandauthenApplication.class, args);
	}

}
