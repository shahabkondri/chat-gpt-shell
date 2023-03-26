package com.shahabkondri.chatgpt.cli;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/**
 * The main entry point of the ChatGPT CLI application. This class configures and starts
 * the Spring Boot application, disabling the banner, setting a random server port, and
 * scanning for configuration properties from the specified property source.
 *
 * @author Shahab Kondri
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ChatGptCliApplication {

	/**
	 * The main method that starts the ChatGPT CLI application, configuring and launching
	 * the Spring Boot application with the specified command line arguments.
	 * @param args The command line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ChatGptCliApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.setDefaultProperties(Map.of("server.port", "0"));
		ConfigurableApplicationContext context = app.run(args);
		SpringApplication.exit(context);
	}

}
