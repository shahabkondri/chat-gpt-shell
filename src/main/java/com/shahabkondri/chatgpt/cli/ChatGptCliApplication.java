package com.shahabkondri.chatgpt.cli;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;

/**
 * @author Shahab Kondri
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ChatGptCliApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ChatGptCliApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.setDefaultProperties(Collections.singletonMap("server.port", "0"));
		ConfigurableApplicationContext context = app.run(args);
		SpringApplication.exit(context);
	}

}
