package com.twitter.mudbl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class MudblApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudblApplication.class, args);
	}

}
