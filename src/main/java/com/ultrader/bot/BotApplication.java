package com.ultrader.bot;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class})
public class BotApplication {
	private static ConfigurableApplicationContext context;
	private static String[] arguments;
	public static void main(String[] args) {
		context = SpringApplication.run(BotApplication.class, args);
		arguments = args;
	}


}
