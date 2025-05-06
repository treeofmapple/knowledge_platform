package com.tom.tool.knowledge.exception;

import com.tom.tool.knowledge.exception.Global.CustomGlobalException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class BadRequestException extends CustomGlobalException{

	public BadRequestException(String msg) {
		super(msg);
	}
	
	public BadRequestException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
