package org.tutorial.tutorial_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableAsync
@EnableWebMvc
@EnableSpringDataWebSupport
public class TutorialPlatformApplication {

	public static void main(String[] args) {

		SpringApplication.run(TutorialPlatformApplication.class, args);
	}

}
