package com.sjms.exception;

public class JobExpiredException extends RuntimeException {
	public JobExpiredException() {
		super();
	}

	public JobExpiredException(String message) {
		super(message);
	}

	public JobExpiredException(String message, Throwable t) {
		super(message, t);
	}
}
