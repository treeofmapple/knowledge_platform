package com.tom.service.knowledges.exception;

import com.tom.service.knowledges.exception.Global.CustomGlobalException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class ConflictException extends CustomGlobalException {

	public ConflictException(String msg) {
		super(msg);
	}
	
	public ConflictException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
