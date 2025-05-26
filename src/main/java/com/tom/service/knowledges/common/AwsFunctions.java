package com.tom.service.knowledges.common;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.tom.service.knowledges.attachments.Attachment;
import com.tom.service.knowledges.config.AwsStorageConfig;
import com.tom.service.knowledges.exception.DataTransferenceException;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@RequiredArgsConstructor
public class AwsFunctions {

	private final AwsStorageConfig awsConfig;
	
	public String buildS3Url(String key) {
	    String s3Url = awsConfig.getS3Client()
	    		.utilities()
	    		.getUrl(builder -> builder.bucket(awsConfig.getBucketName()).key(key))
	    		.toExternalForm();
	    return s3Url;
	}
	
	public void putObject(String key, MultipartFile file) {
		try {
		    awsConfig.getS3Client()
		    		.putObject(PutObjectRequest.builder()
			        .bucket(awsConfig.getBucketName())
			        .key(key)
			        .contentType(file.getContentType())
			        .build(),
			        RequestBody.fromInputStream(file.getInputStream(), file.getSize())
		        );
		    ServiceLogger.info("Successfully uploaded");
		} catch (IOException e) {
			ServiceLogger.error("Error uploading image", e);
			throw new DataTransferenceException("Error uploading image", e);
	    } catch (S3Exception | SdkClientException e) {
	        ServiceLogger.error("Error uploading file to S3 with key: " + key, e);
	        throw new DataTransferenceException("Error during S3 file transfer", e);
	    }
	}
	
	public ResponseInputStream<GetObjectResponse> objectAsBytes(Attachment image) {
		return awsConfig.getS3Client()
				.getObject(GetObjectRequest.builder()
						.bucket(awsConfig.getBucketName())
						.key(image.getObjectKey())
						.build());
	}
	
	public void deleteObject(Attachment image) {
        awsConfig.getS3Client()
			.deleteObject(DeleteObjectRequest.builder()
			.bucket(awsConfig.getBucketName())
			.key(image.getObjectKey())
			.build());
	}
	
	public String renameObject(Attachment image, String newName) {
	    String originalKey = image.getObjectKey();
	    String extension = originalKey.contains(".") ? originalKey.substring(originalKey.lastIndexOf(".")) : "";
	    
	    String newKey = newName + extension;
	    
	    awsConfig.getS3Client().copyObject(builder -> builder
	            .sourceBucket(awsConfig.getBucketName())
	            .sourceKey(image.getObjectKey())
	            .destinationBucket(awsConfig.getBucketName())
	            .destinationKey(newKey)
        );
	    
	    awsConfig.getS3Client().deleteObject(builder -> builder
	            .bucket(awsConfig.getBucketName())
	            .key(image.getObjectKey())
        );
	    
	    return newKey;
	}
	
	
}
