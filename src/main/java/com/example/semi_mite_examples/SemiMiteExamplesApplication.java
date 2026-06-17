package com.example.semi_mite_examples;

import org.example.client.EnableMiteClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMiteClients
public class SemiMiteExamplesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SemiMiteExamplesApplication.class, args);
	}

}
