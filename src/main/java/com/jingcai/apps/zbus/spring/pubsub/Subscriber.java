package com.jingcai.apps.zbus.spring.pubsub;

import com.jingcai.apps.zbus.spring.mq.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zbus.broker.Broker;
import org.zbus.mq.Consumer;
import org.zbus.mq.Protocol;
import org.zbus.net.core.Session;
import org.zbus.net.http.Message;

/**
 * Created by lejing on 15/10/10.
 */
public class Subscriber extends Consumer {
	private static final Logger logger = LoggerFactory.getLogger(Subscriber.class);

	public Subscriber(Broker broker, String mq, Protocol.MqMode... mode) {
		super(broker, mq, mode);
		setConsumeTimeout(30*1000);
	}

	public void setHandler(final Handler handler) {
		Message.MessageHandler h = new Message.MessageHandler() {
			public void handle(Message msg, Session sess) {
				handler.handle(msg.getBodyString());
			}
		};
		try {
			onMessage(h);
		} catch (Exception e) {
			logger.error("setHandler error", e);
		}
	}
}
