package com.tom.service.knowledges.attachments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tom.service.knowledges.common.AwsFunctions;
import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.common.SystemUtils;
import com.tom.service.knowledges.exception.DataTransferenceException;
import com.tom.service.knowledges.exception.IllegalStatusException;
import com.tom.service.knowledges.notes.NoteUtils;
import com.tom.service.knowledges.user.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {
	
	private final AwsFunctions functions;
	private final AttachmentRepository repository;
	private final AttachmentMapper mapper;
	private final AttachmentUtils attachmentUtils;
	private final NoteUtils noteUtils;
	private final SystemUtils utils;
	
	@Transactional
	public AttachmentResponse uploadObjectToNote(String noteName, MultipartFile file, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is uploading an object", userIp);

		long maxSizeInBytes = 20 * 1024 * 1024;
		if (file.getSize() > maxSizeInBytes) {
			throw new IllegalStatusException("File size exceeds the 20MB limit.");
		}
		
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

		var note = noteUtils.ensureNoteExistsAndGet(noteName);
        if (!note.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not have permission to modify this note.");
        }
		
		String key = "attachments/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
		attachmentUtils.checkIfObjectAlreadyExists(file.getOriginalFilename());

		functions.putObject(key, file);
		String s3Url = functions.buildS3Url(key);
		
		var attachements = new Attachment();
	    mapper.mergeFromMultipartFile(attachements, file, key, s3Url);
	    attachements.setNote(note);
	    
	    repository.save(attachements);
	    return mapper.toResponse(attachements);
	}
	
	@Transactional
	public byte[] downloadObjectFromNote(String noteName, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is downloading image with name: {}", userIp, noteName);

		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		
		var note = noteUtils.ensureNoteExistsAndGet(noteName);
        if (!note.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not have permission to access this note's attachments.");
        }
        
        if (note.getAttachments() == null || note.getAttachments().isEmpty()) {
            return null;
        }
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
	             ZipOutputStream zos = new ZipOutputStream(baos)) {

	            for (Attachment attachment : note.getAttachments()) {
	                byte[] fileBytes = functions.objectAsBytes(attachment.getObjectKey());
	                ZipEntry zipEntry = new ZipEntry(attachment.getName());
	                zos.putNextEntry(zipEntry);
	                zos.write(fileBytes);
	                zos.closeEntry();
	            }
	            zos.finish();
	            return baos.toByteArray();

	        } catch (IOException e) {
	            ServiceLogger.error("Error creating zip file for note: " + noteName, e);
	            throw new DataTransferenceException("Error creating zip file.", e);
	        }
	}
	
	@Transactional
	public void deleteObjectFromNote(String noteName, String attachmentName, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is searching for all objects", userIp);

		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		
		var notes = noteUtils.ensureNoteExistsAndGet(noteName);
        if (!notes.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not have permission to modify this note.");
        }
		
		var attachment = attachmentUtils.ensureObjectExistAndGet(noteName);
		
        if (!attachment.getNote().getId().equals(notes.getId())) {
            throw new SecurityException("Attachment does not belong to the specified note.");
        }
		
		functions.deleteObject(attachment.getObjectKey());
		repository.delete(attachment);
        
		ServiceLogger.info("Successfully deleted image with name: {}", noteName);
	}
	
	/*
	
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
	
	*/
	
}
