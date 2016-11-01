package com.jingcai.apps.zbus.spring.mq;

import com.jingcai.apps.common.lang.exception.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zbus.broker.Broker;
import org.zbus.mq.Protocol;
import org.zbus.net.http.Message;

/**
 * Created by lejing on 15/10/10.
 */
public class Producer extends org.zbus.mq.Producer {
	private static final Logger logger = LoggerFactory.getLogger(Producer.class);

	public Producer(Broker broker, String mq, Protocol.MqMode... mode) {
		super(broker, mq, mode);
	}

	public void sendSync(String message) {
		Message msg = new Message();
		msg.setBody(message);
		try {
			logger.info("MQ同步发送:{}", message);
			sendSync(msg);
		} catch (Exception e) {
			logger.error("MQ同步发送异常", e);
			throw Exceptions.unchecked(e);
		}
	}

	public void sendAsync(String message) {
		Message msg = new Message();
		msg.setBody(message);
		try {
			logger.info("MQ异步发送:{}", message);
			sendAsync(msg);
		} catch (Exception e) {
			logger.error("MQ异步发送异常", e);
			throw Exceptions.unchecked(e);
		}
	}
}
