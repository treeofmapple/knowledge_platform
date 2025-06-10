package com.tom.service.knowledges.image;

import java.security.Principal;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tom.service.knowledges.common.AwsFunctions;
import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.common.SystemUtils;
import com.tom.service.knowledges.notes.Note;
import com.tom.service.knowledges.notes.NoteRepository;
import com.tom.service.knowledges.notes.NoteUtils;
import com.tom.service.knowledges.user.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final AwsFunctions functions;
	private final ImageRepository imageRepository;
	private final NoteRepository noteRepository;
	private final ImageMapper mapper;
	private final NoteUtils noteUtils;
	private final SystemUtils utils;
	
	@Transactional
	public ImageResponse uploadImageToNote(String noteName, MultipartFile file, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is uploading an object", userIp);
		
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		
		var note = noteUtils.ensureNoteExistsAndGet(noteName);
        if (!note.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not have permission to modify this note.");
        }
		
        if (note.getImage() != null) {
            removeImageFromNote(note);
        }
		
		String key = "images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
		functions.putObject(key, file);
		String s3Url = functions.buildS3Url(key);

		var image = new Image();
		mapper.mergeFromMultipartFile(image, file, key, s3Url);
		note.setImage(image);
		noteRepository.save(note);

	    return mapper.toResponse(image);
	}
	
	@Transactional
	public void removeImageFromNote(String noteName, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is searching for all objects", userIp);
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		
		var note = noteUtils.ensureNoteExistsAndGet(noteName);
        if (!note.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not have permission to modify this note.");
        }
		
		var imageToDelete = note.getImage();

        if (imageToDelete != null) {
            note.setImage(null);
            noteRepository.save(note);

            functions.deleteObject(imageToDelete.getObjectKey());
            imageRepository.delete(imageToDelete);

            ServiceLogger.info("Successfully deleted image for note: {}", noteName);
        }

	}
	
	@Transactional
	private void removeImageFromNote(Note note) {
		var imageToDelete = note.getImage();

        if (imageToDelete != null) {
            note.setImage(null);
            noteRepository.save(note);

            functions.deleteObject(imageToDelete.getObjectKey());
            imageRepository.delete(imageToDelete);

        }
	}
	
}
