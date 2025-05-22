package com.tom.service.knowledges.exception.Global;

@SuppressWarnings("serial")
public abstract class IllegalGlobalException extends IllegalStateException {

	public IllegalGlobalException(String msg) {
		super(msg);
	}

	public IllegalGlobalException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
