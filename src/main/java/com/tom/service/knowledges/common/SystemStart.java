package com.tom.service.knowledges.common;

import org.springframework.stereotype.Component;

import com.tom.aws.awstest.config.AwsProperties;
import com.tom.aws.awstest.config.AwsStorageConfig;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SystemStart {

    private final AwsProperties properties;
	private final AwsStorageConfig awsConfig;
	
	@Getter
	private boolean s3Connected = false;
	
	@PostConstruct
	public void verifyS3Connection() {
	    try {
	        awsConfig.getS3Client().getBucketLocation(b -> b.bucket(properties.getBucket()));
	        ServiceLogger.info("Connection successful on bucket: {}", properties.getBucket());
	        s3Connected = true;
	    } catch (Exception e) {
	        ServiceLogger.error("S3 connection failed: {}", e.getMessage(), e);
	        s3Connected = false;
	    }
	}
	
}