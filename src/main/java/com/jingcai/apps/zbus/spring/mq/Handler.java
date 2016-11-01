package com.jingcai.apps.zbus.spring.mq;

/**
 * Created by lejing on 15/10/10.
 */
public interface Handler {
	void handle(String message);
}
