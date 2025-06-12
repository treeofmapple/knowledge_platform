package com.tom.service.knowledges.common;

import org.springframework.stereotype.Component;

import com.tom.service.knowledges.config.AwsProperties;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;

@Component
@RequiredArgsConstructor
public class SystemStart {

    private final AwsProperties properties;
	private final S3Client s3Client;
	
	@Getter
	private boolean s3Connected = false;
	
	@PostConstruct
	public void verifyS3Connection() {
	    try {
	        s3Client.getBucketLocation(b -> b.bucket(properties.getBucket()));
	        ServiceLogger.info("Connection successful on bucket: {}", properties.getBucket());
	        s3Connected = true;
	    } catch (Exception e) {
	        ServiceLogger.error("S3 connection failed: {}", e.getMessage(), e);
	        s3Connected = false;
	    }
	}
	
}