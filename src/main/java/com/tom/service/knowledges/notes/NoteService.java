package com.tom.service.knowledges.notes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tom.service.knowledges.attachments.AttachmentService;
import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.common.SystemUtils;
import com.tom.service.knowledges.image.ImageService;
import com.tom.service.knowledges.tag.TagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {

	@Value("${application.page.size:10}")
	private int PAGE_SIZE;
	
	private final NoteRepository repository;
	private final NoteMapper mapper;
	private final SystemUtils utils;
	private final NoteUtils repoCall;
	
	private final ImageService imageService;
	private final AttachmentService attachmentService;
	private final TagService tagService;
	
	public NotePageResponse findAllNotes(int value) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);
		
		Pageable pageable = PageRequest.of(value, PAGE_SIZE);
		Page<Note> tagNote = repository.findAll(pageable);
		return mapper.toNotePageResponse(tagNote);
	}

	public NotePageResponse findNoteByName(String name, int value) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);
	
		Pageable pageable = PageRequest.of(value, PAGE_SIZE);
		Page<Note> tagNote = repository.findAll(pageable);
		
		return mapper.toNotePageResponse(tagNote);
	}

	public NotePageResponse findNoteByTag(String tagName, int value) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);
		
		Pageable pageable = PageRequest.of(value, PAGE_SIZE);
		Page<Note> tagNote = repository.findAll(pageable);
		
		return mapper.toNotePageResponse(tagNote);
	}

	public NoteResponse createNote(CreateNoteRequest request) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);
		
		
		byte[] store = request.annotation();
		
		ServiceLogger.info(userIp);
		return mapper.toRepsonse(null);
	}

	public NoteResponse editNote(EditNoteRequest request) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);
		
		
		
		
		
		return mapper.toRepsonse(null);
	}
	
	public void deleteNote(String name) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);
		
		
		repository.delete(null);
		
		ServiceLogger.info(userIp);
	}
	
}
