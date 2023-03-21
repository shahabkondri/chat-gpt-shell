package com.shahabkondri.chatgpt.cli;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

/**
 * @author Shahab Kondri
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@PropertySource("classpath:chat-gpt-cli.config")
public class ChatGptCliApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ChatGptCliApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.setDefaultProperties(Map.of("server.port", "0"));
		ConfigurableApplicationContext context = app.run(args);
		SpringApplication.exit(context);
	}

}
