package com.jingcai.apps.zbus.spring.pubsub;

import com.jingcai.apps.common.lang.exception.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zbus.broker.Broker;
import org.zbus.mq.Producer;
import org.zbus.mq.Protocol;
import org.zbus.net.http.Message;

/**
 * Created by lejing on 15/10/10.
 */
public class Publisher extends Producer {
	private static final Logger logger = LoggerFactory.getLogger(Publisher.class);

	public Publisher(Broker broker, String mq, Protocol.MqMode... mode) {
		super(broker, mq, mode);
	}

	public void sendSync(String topic, String message) {
		Message msg = new Message();
		msg.setTopic(topic);
		msg.setBody(message);
		try {
			sendSync(msg);
		} catch (Exception e) {
			logger.error("sendSync error", e);
			throw Exceptions.unchecked(e);
		}
	}

	public void sendAsync(String topic, String message) {
		Message msg = new Message();
		msg.setTopic(topic);
		msg.setBody(message);
		try {
			sendAsync(msg);
		} catch (Exception e) {
			logger.error("sendSync error", e);
			throw Exceptions.unchecked(e);
		}
	}
}
