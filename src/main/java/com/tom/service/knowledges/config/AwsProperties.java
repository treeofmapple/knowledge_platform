package com.tom.service.knowledges.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperties {
 
	private String region;
    private String bucket;
    private boolean accelerateEnabled = false;

}