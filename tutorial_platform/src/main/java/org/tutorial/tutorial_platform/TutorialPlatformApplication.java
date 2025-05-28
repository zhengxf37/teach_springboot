package org.tutorial.tutorial_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TutorialPlatformApplication {

	public static void main(String[] args) {

		SpringApplication.run(TutorialPlatformApplication.class, args);
	}

}
