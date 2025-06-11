package com.tom.service.knowledges.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "aws.r2")
public class AwsProperties {
 
	private String endpoint;
	private String accessKeyId;
	private String secretAccessKey;
    private String bucket;
	private String region;
	private String ddlAuto;
	
}