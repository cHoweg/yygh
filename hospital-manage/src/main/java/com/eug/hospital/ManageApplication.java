package com.eug.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.eug")
public class ManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManageApplication.class, args);
	}

}
