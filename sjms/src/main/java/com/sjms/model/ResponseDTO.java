package com.sjms.model;

import java.io.Serializable;

/**
 * This is the response class which will be sent to User either with
 * failure/success message and status code. 0 is for filure and 1 is for success
 * and T is the data which will be sent.
 * 
 * @author Raghu
 *
 * @param <T>
 */
public class ResponseDTO<T> implements Serializable {
	private Integer code;
	private String message;
	private T t;

	public ResponseDTO(int code, String message, T t) {
		this.code = code;
		this.message = message;
		this.t = t;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getT() {
		return t;
	}

	public void setT(T t) {
		this.t = t;
	}

	@Override
	public String toString() {
		return "ResponseDTO{" + "code=" + code + ", message='" + message + '\'' + ", t=" + t + '}';
	}

}
