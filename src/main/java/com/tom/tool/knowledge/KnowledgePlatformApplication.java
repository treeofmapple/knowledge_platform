package com.tom.tool.knowledge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class KnowledgePlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnowledgePlatformApplication.class, args);
	}

}
