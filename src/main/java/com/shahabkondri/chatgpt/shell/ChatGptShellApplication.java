package com.shahabkondri.chatgpt.shell;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/**
 * The main entry point of the ChatGPT Shell application. This class configures and starts
 * the Spring Boot application, disabling the banner, setting a random server port, and
 * scanning for configuration properties from the specified property source.
 *
 * @author Shahab Kondri
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ChatGptShellApplication {

	/**
	 * The main method that starts the ChatGPT Shell application, configuring and
	 * launching the Spring Boot application with the specified command line arguments.
	 * @param args The command line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ChatGptShellApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.setDefaultProperties(Map.of("server.port", "0", "spring.shell.command.stacktrace.enabled", "false",
				"spring.shell.command.history.enabled", "false", "spring.shell.command.script.enabled", "false"));
		ConfigurableApplicationContext context = app.run(args);
		SpringApplication.exit(context);
	}

}