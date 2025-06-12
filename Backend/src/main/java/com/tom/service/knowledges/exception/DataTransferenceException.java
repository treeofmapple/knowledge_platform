package com.tom.service.knowledges.exception;

import com.tom.service.knowledges.exception.Global.CustomGlobalException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class DataTransferenceException extends CustomGlobalException {
	
	public DataTransferenceException(String msg) {
		super(msg);
	}
	
	public DataTransferenceException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
