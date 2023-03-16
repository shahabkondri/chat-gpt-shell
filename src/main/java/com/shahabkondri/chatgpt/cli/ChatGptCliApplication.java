package com.shahabkondri.chatgpt.cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * @author Shahab Kondri
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ChatGptCliApplication {

	public static void main(String[] args) {
		SpringApplication.exit(SpringApplication.run(ChatGptCliApplication.class, args));
	}

}
