package com.tom.service.knowledges.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class AwsStorageConfig {

    private final AwsProperties properties;
    
    @Getter
    S3Client s3Client;

    @PostConstruct
    public void init() {
        this.s3Client = S3Client.builder()
                .region(Region.of(properties.getRegion())) //
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        
        /*
        if(properties.isAccelerateEnabled()) {
        	enableAccelerateMode();
        }
        enableVersioning();
        
        */
    }
    
    /*
    
	private void enableAccelerateMode() {
        try {
			s3Client.putBucketAccelerateConfiguration(
			        PutBucketAccelerateConfigurationRequest.builder()
			                .bucket(properties.getBucket())
			                .accelerateConfiguration(
			                        AccelerateConfiguration.builder()
			                                .status(BucketAccelerateStatus.ENABLED)
			                                .build())
			                .build());
		} catch (S3Exception e) {
			ServiceLogger.error("Failed to enable aceleration: {}", e.awsErrorDetails().errorMessage());
		}
    }

    private void enableVersioning() {
        try {
			s3Client.putBucketVersioning(
			        PutBucketVersioningRequest.builder()
			                .bucket(properties.getBucket())
			                .versioningConfiguration(
			                        VersioningConfiguration.builder()
			                                .status(BucketVersioningStatus.ENABLED)
			                                .build())
			                .build());
		} catch (S3Exception e) {
			ServiceLogger.error("Failed to enable bucket versioning: {}", e.awsErrorDetails().errorMessage());
		}
    }
	
	*/

    public String getBucketName() {
        return properties.getBucket();
    }
}
