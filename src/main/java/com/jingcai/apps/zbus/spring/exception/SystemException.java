package com.jingcai.apps.zbus.spring.exception;


import com.jingcai.apps.zbus.spring.mq.common.ResultMessage;

public class SystemException extends RuntimeException {
	private String code;
	private String message;

	public SystemException(ResultMessage businessMessage) {
		this.code = businessMessage.getCode();
		this.message = businessMessage.getMessage();
	}

	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}