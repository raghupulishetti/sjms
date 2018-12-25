package com.sjms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.sjms.entity"})
public class SjmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SjmsApplication.class, args);
	}

}

