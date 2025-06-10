package com.tom.service.knowledges.attachments;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tom.service.knowledges.common.AwsFunctions;
import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.common.SystemUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {
	
	private final AwsFunctions functions;
	private final AttachmentRepository repository;
	private final AttachmentMapper mapper;
	private final SystemUtils utils;
	private final AttachmentUtils repoCall;
	
	public AttachmentResponse findObjectByName(String name) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is searching for object by name: {}", userIp, name);
		var image = repoCall.findObject(name);
		return mapper.fromResponse(image);
	}
	
	@Transactional
	public AttachmentResponse uploadObject(MultipartFile file) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is uploading an object", userIp);
		String key = "images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
	    
		repoCall.ensureObjectExist(file.getOriginalFilename());
		functions.putObject(key, file);
		String s3Url = functions.buildS3Url(key);
		
		var image = new Attachment();
	    repoCall.mergeData(image, file.getOriginalFilename(), key, s3Url, file.getContentType(), file.getSize());
	    repository.save(image);
	    return mapper.fromResponse(image);
	}
	
	@Transactional
	public byte[] downloadObject(String name) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is downloading image with name: {}", userIp, name);

		repoCall.ensureObjectNotExist(name);
		Attachment images = repoCall.findObject(name);
		return functions.objectAsBytes(images);
	}
	
	@Transactional
	public String deleteObject(String name) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is searching for all objects", userIp);
		Attachment images = repoCall.findObject(name);
	
		repoCall.ensureObjectNotExist(name);
		functions.deleteObject(images);
        repository.delete(images);
        ServiceLogger.info("Successfully deleted image with name: {}", name);
        return "Image deleted successfully";
	}
	
	@Transactional
	public AttachmentResponse renameObject(String name , String newName) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is renaming object with name: {} to: {}", userIp, name, newName);
		repoCall.ensureObjectExist(name);
		Attachment images = repoCall.findObject(name);
		
		String newKey = functions.renameObject(images, newName);
		String newUrl = functions.buildS3Url(newKey);

		repoCall.mergeData(images, newKey, newUrl);
	    repository.save(images);
		
		return mapper.fromResponse(images);
	}
	
}
