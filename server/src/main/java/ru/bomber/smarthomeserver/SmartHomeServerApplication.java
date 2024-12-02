package ru.bomber.smarthomeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.bomber.core", "ru.bomber.smarthomeserver"})
public class SmartHomeServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartHomeServerApplication.class, args);
	}

}
