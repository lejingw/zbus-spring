package com.jingcai.apps.zbus.spring;

import com.jingcai.apps.zbus.spring.pubsub.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by lejing on 15/9/23.
 */
public class PublishBeanDefinitionParser implements BeanDefinitionParser {
	private static final Logger logger = LoggerFactory.getLogger(PublishBeanDefinitionParser.class);

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(Publisher.class);
		rootBeanDefinition.setLazyInit(false);

		String serviceRouterName = element.getAttribute("id");
		if (StringUtils.hasText(serviceRouterName)) {
			parserContext.getRegistry().registerBeanDefinition(serviceRouterName, rootBeanDefinition);
		} else {
			serviceRouterName = parserContext.getReaderContext().registerWithGeneratedName(rootBeanDefinition);
		}
		parserContext.registerComponent(new BeanComponentDefinition(rootBeanDefinition, serviceRouterName));

		String broker = element.getAttribute("broker");
		String mq = element.getAttribute("mq");
		if (!StringUtils.hasText(broker)) {
			logger.error("init scrible error, 'broker' should be set");
			return null;
		}
		if (!StringUtils.hasText(mq)) {
			logger.error("init scrible error, 'mq'(message queue name) should be set");
			return null;
		}
		//broker, PubSubMQ, org.zbus.mq.Protocol.MqMode.PubSub
		ConstructorArgumentValues constructorArgumentValues = rootBeanDefinition.getConstructorArgumentValues();
		constructorArgumentValues.addIndexedArgumentValue(0, new RuntimeBeanReference(broker));
		constructorArgumentValues.addIndexedArgumentValue(1, mq);
		constructorArgumentValues.addIndexedArgumentValue(2, org.zbus.mq.Protocol.MqMode.PubSub);


		rootBeanDefinition.setInitMethodName("createMQ");

		return rootBeanDefinition;
	}
}