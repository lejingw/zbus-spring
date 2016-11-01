package com.jingcai.apps.zbus.spring.mq;

import org.zbus.broker.Broker;
import org.zbus.mq.Protocol;
import org.zbus.net.core.Session;
import org.zbus.net.http.Message;

import java.io.IOException;

/**
 * Created by lejing on 15/10/10.
 */
public class Consumer extends org.zbus.mq.Consumer {
	private boolean enable = false;
	public Consumer(Broker broker, String mq, Protocol.MqMode... mode) {
		super(broker, mq, mode);
	}

	public void setHandler(final Handler handler) throws IOException {
		Message.MessageHandler h = new Message.MessageHandler() {
			public void handle(Message msg, Session sess) throws IOException {
				handler.handle(msg.getBodyString());
			}
		};
		onMessage(h);
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public void startup(){
		if(enable) {
			start();
		}
	}
	public void shutdown(){
		stop();
	}
}
