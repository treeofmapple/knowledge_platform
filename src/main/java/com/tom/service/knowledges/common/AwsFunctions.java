package com.tom.service.knowledges.common;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.tom.aws.awstest.config.AwsStorageConfig;
import com.tom.aws.awstest.exception.DataTransferenceException;
import com.tom.aws.awstest.image.Image;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
		    awsConfig.getS3Client().putObject(PutObjectRequest.builder()
			        .bucket(awsConfig.getBucketName())
			        .key(key)
			        .contentType(file.getContentType())
			        .build(),
			        RequestBody.fromInputStream(file.getInputStream(), file.getSize())
		        );
		} catch (IOException e) {
			ServiceLogger.error("Error uploading image", e);
			throw new DataTransferenceException("Error uploading image", e);
		}
	}
	
	public byte[] objectAsBytes(Image image) {
		return awsConfig.getS3Client()
				.getObjectAsBytes(
						GetObjectRequest.builder()
						.bucket(awsConfig.getBucketName())
						.key(image.getObjectKey())
						.build())
				.asByteArray();
	}
	
	public void deleteObject(Image image) {
        awsConfig.getS3Client()
			.deleteObject(
			DeleteObjectRequest.builder()
			.bucket(awsConfig.getBucketName())
			.key(image.getObjectKey())
			.build());
	}
	
	public String renameObject(Image image, String newName) {
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
	
	/*
	
	public void addTags(Image image, String tagKey, String tagValue) {
		awsConfig.getS3Client()
			.putObjectTagging(PutObjectTaggingRequest.builder()
		    .bucket(awsConfig.getBucketName())
		    .key(image.getObjectKey())
		    .tagging(Tagging.builder()
		        .tagSet(List.of(Tag.builder()
		        		.key(tagKey)
		        		.value(tagValue)
		        		.build())).build())
		    .build());
	}
	
	public void removeTag(Image image, String tagKey, String tagValue) { // remove one tag from a current image
	    awsConfig.getS3Client().putObjectTagging(
	            PutObjectTaggingRequest.builder()
	                .bucket(awsConfig.getBucketName())
	                .key(image.getObjectKey())
	                .tagging(Tagging.builder()
	                    .tagSet(List.of(Tag.builder()
	                    		.key(tag)
	                    		))
	                    .build())
	                .build()
        );
	}
	
	public void deleteTags(String tagKey, String tagValue) { // delete the tag, and if is on a image remove it also
		
	}
	
	*/
	
}
