package com.tom.tool.knowledge.exception;

import com.tom.tool.knowledge.exception.Global.IllegalGlobalException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class IllegalStatusException extends IllegalGlobalException {

	public IllegalStatusException(String msg) {
		super(msg);
	}

	public IllegalStatusException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
