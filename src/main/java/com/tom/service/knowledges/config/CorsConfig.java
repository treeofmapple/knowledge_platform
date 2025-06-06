package com.tom.service.knowledges.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${application.cors.allowed-origins}")
    private String[] allowedOrigins;
	
	@Value("${application.cors.time:300}")
	private Long corsTime;
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOrigins(Arrays.asList(allowedOrigins));
	    config.setAllowedMethods(Arrays.asList("*"));
	    config.setAllowedHeaders(Arrays.asList("*"));
	    config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
	    config.setAllowCredentials(true);
	    config.setMaxAge(corsTime);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	    return source;
	}
}
