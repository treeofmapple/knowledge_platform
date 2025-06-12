package com.tom.service.knowledges.attachments;


import java.security.Principal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/attachment")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class AttachmentController {

	private final AttachmentService service;
	
	@PostMapping(value = "/upload",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AttachmentResponse> uploadObjectToNote(@RequestParam("name") String noteName, MultipartFile file, Principal connectedUser) {
		var response = service.uploadObjectToNote(noteName, file, connectedUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping(value = "/download", produces = "application/zip")
	public ResponseEntity<byte[]> downloadObjectFromNote(@RequestParam("name") String noteName, Principal connectedUser) {
		var response = service.downloadObjectFromNote(noteName, connectedUser);
		
	    if (response == null) {
	        return ResponseEntity.noContent().build();
	    }
		
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/zip"));
        headers.setContentDispositionFormData("attachment", noteName + "_attachments.zip");
		
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response);
	}
	
	@DeleteMapping(value = "/delete")
	public ResponseEntity<Void> deleteObjectFromNote(@RequestParam("name") String noteName, @RequestParam("attachment") String attachmentName, Principal connectedUser) {
		service.deleteObjectFromNote(noteName, attachmentName, connectedUser);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
