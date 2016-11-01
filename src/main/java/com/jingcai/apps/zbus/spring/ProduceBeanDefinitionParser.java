package com.jingcai.apps.zbus.spring;

import com.jingcai.apps.zbus.spring.mq.Producer;
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
import org.zbus.mq.Protocol;

/**
 * Created by lejing on 15/9/23.
 */
public class ProduceBeanDefinitionParser implements BeanDefinitionParser {
	private static final Logger logger = LoggerFactory.getLogger(ProduceBeanDefinitionParser.class);

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(Producer.class);
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
			logger.error("init Producer error, 'broker' should be set");
			return null;
		}
		if (!StringUtils.hasText(mq)) {
			logger.error("init Producer error, 'mq'(message queue name) should be set");
			return null;
		}
		//broker, PubSubMQ, org.zbus.mq.Protocol.MqMode.PubSub
		ConstructorArgumentValues constructorArgumentValues = rootBeanDefinition.getConstructorArgumentValues();
		constructorArgumentValues.addIndexedArgumentValue(0, new RuntimeBeanReference(broker));
		constructorArgumentValues.addIndexedArgumentValue(1, mq);
		constructorArgumentValues.addIndexedArgumentValue(2, Protocol.MqMode.MQ);


		rootBeanDefinition.setInitMethodName("createMQ");

		return rootBeanDefinition;
	}
}