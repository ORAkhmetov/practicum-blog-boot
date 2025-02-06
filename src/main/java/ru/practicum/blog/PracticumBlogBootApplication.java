package ru.practicum.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.practicum.blog")
public class PracticumBlogBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(PracticumBlogBootApplication.class, args);
	}

}
