package com.jingcai.apps.zbus.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.zbus.rpc.RpcProcessor;
import org.zbus.rpc.mq.Service;
import org.zbus.rpc.mq.ServiceConfig;

import java.util.List;

/**
 * Created by lejing on 15/9/23.
 */
public class RpcServiceBeanDefinitionParser implements BeanDefinitionParser {
	private static final Logger logger = LoggerFactory.getLogger(RpcServiceBeanDefinitionParser.class);

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String mq = element.getAttribute("mq");
		String broker = element.getAttribute("broker");
		String consumerCount = element.getAttribute("consumerCount");
		if (!StringUtils.hasText(mq)) {
			logger.error("init interfaces error, 'mq' should be set");
			return null;
		}
		if (!StringUtils.hasText(broker)) {
			logger.error("init interfaces error, 'broker' should be set");
			return null;
		}
		if (!StringUtils.hasText(consumerCount)) {
			logger.error("init interfaces error, 'consumerCount' should be set");
			return null;
		}
		Object source = parserContext.extractSource(element);

		RootBeanDefinition rpcProcessorBeanDefinition = new RootBeanDefinition(RpcProcessor.class);
		rpcProcessorBeanDefinition.setSource(source);
		rpcProcessorBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		List<Element> listeners = DomUtils.getChildElementsByTagName(element, new String[]{"bean", "ref"});
		int i = 0;
		for (Element interceptor : listeners) {
			Object listenerBean = parserContext.getDelegate().parsePropertySubElement(interceptor, null);
			rpcProcessorBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(i++, listenerBean);
		}
		String rpcBeanName = parserContext.getReaderContext().registerWithGeneratedName(rpcProcessorBeanDefinition);
		parserContext.registerComponent(new BeanComponentDefinition(rpcProcessorBeanDefinition, rpcBeanName));


		RootBeanDefinition configBeanDefinition = new RootBeanDefinition(ServiceConfig.class);
		configBeanDefinition.setSource(source);
		configBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		configBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference(broker));
		configBeanDefinition.getPropertyValues().add("mq", mq);
		configBeanDefinition.getPropertyValues().add("consumerCount", consumerCount);
		configBeanDefinition.getPropertyValues().add("messageProcessor", new RuntimeBeanReference(rpcBeanName));
		String configBeanName = parserContext.getReaderContext().registerWithGeneratedName(configBeanDefinition);
		parserContext.registerComponent(new BeanComponentDefinition(configBeanDefinition, configBeanName));


		RootBeanDefinition serviceBeanDefinition = new RootBeanDefinition(Service.class);
		serviceBeanDefinition.setSource(source);
		serviceBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		serviceBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference(configBeanName));
		String beanName = parserContext.getReaderContext().registerWithGeneratedName(serviceBeanDefinition);
		parserContext.registerComponent(new BeanComponentDefinition(serviceBeanDefinition, beanName));

		serviceBeanDefinition.setInitMethodName("start");
		serviceBeanDefinition.setDestroyMethodName("close");
		return serviceBeanDefinition;
	}
}