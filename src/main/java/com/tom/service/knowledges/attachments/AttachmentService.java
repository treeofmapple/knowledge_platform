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
	
	@Transactional
	public AttachmentResponse uploadObject(MultipartFile file) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is uploading an object", userIp);

		String key = "object/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
		repoCall.checkIfObjectAlreadyExists(file.getOriginalFilename());

		functions.putObject(key, file);
		String s3Url = functions.buildS3Url(key);
		
		var object = new Attachment();
	    mapper.mergeFromMultipartFile(object, file, key, s3Url);
	    
	    repository.save(object);
	    return mapper.toResponse(object);
	}
	
	@Transactional
	public byte[] downloadObject(String name) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is downloading image with name: {}", userIp, name);

		var images = repoCall.ensureObjectExistAndGet(name);
		return functions.objectAsBytes(images);
	}
	
	@Transactional
	public void deleteObject(String name) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is searching for all objects", userIp);

		var images = repoCall.ensureObjectExistAndGet(name);
		functions.deleteObject(images);
		repository.delete(images);
        
		ServiceLogger.info("Successfully deleted image with name: {}", name);
	}
	
	@Transactional
	public AttachmentResponse renameObject(String name, String newName) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is renaming object with name: {} to: {}", userIp, name, newName);
		
		var images = repoCall.ensureObjectExistAndGet(name);
		repoCall.checkIfAttachmentIsSame(images, newName);
		
		String newKey = functions.renameObject(images, newName);
		String newUrl = functions.buildS3Url(newKey);

		mapper.mergeFromKeyAndUrl(images, newKey, newUrl);
	    repository.save(images);
		
		return mapper.toResponse(images);
	}
	
}
