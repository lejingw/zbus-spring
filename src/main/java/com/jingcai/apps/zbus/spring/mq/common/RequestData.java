package com.jingcai.apps.zbus.spring.mq.common;

public class RequestData {
	private String id;
	private String tradecode;
	private String content;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTradecode() {
		return tradecode;
	}

	public void setTradecode(String tradecode) {
		this.tradecode = tradecode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}