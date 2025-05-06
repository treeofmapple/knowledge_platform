package com.tom.tool.knowledge.exception;

import com.tom.tool.knowledge.exception.Global.DateGlobalException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class InvalidDateException extends DateGlobalException {
	
	public InvalidDateException(String msg) {
		super(msg);
	}
	
	public InvalidDateException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
