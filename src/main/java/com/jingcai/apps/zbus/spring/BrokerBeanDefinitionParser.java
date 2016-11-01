package com.jingcai.apps.zbus.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.zbus.broker.BrokerConfig;
import org.zbus.broker.SingleBroker;

/**
 * Created by lejing on 15/9/23.
 */
public class BrokerBeanDefinitionParser implements BeanDefinitionParser {
	private static final Logger logger = LoggerFactory.getLogger(BrokerBeanDefinitionParser.class);

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(SingleBroker.class);
		rootBeanDefinition.setLazyInit(false);

		String serviceRouterName = element.getAttribute("id");
		if (StringUtils.hasText(serviceRouterName)) {
			parserContext.getRegistry().registerBeanDefinition(serviceRouterName, rootBeanDefinition);
		} else {
			serviceRouterName = parserContext.getReaderContext().registerWithGeneratedName(rootBeanDefinition);
		}
		parserContext.registerComponent(new BeanComponentDefinition(rootBeanDefinition, serviceRouterName));

		String serverAddr = element.getAttribute("serverAddr");
		if (!StringUtils.hasText(serverAddr)) {
			logger.error("init Broker error, 'serverAddr' should be set");
			return null;
		}

		RootBeanDefinition configBeanDefinition = new RootBeanDefinition(BrokerConfig.class);
		MutablePropertyValues conifigPropertyValues = configBeanDefinition.getPropertyValues();
		conifigPropertyValues.add("serverAddress", serverAddr);
		String maxTotal = element.getAttribute("maxTotal");
		if (StringUtils.hasText(maxTotal)) {
			conifigPropertyValues.add("maxTotal", maxTotal);
		}
		String configBeanName = parserContext.getReaderContext().registerWithGeneratedName(configBeanDefinition);
		parserContext.registerComponent(new BeanComponentDefinition(configBeanDefinition, configBeanName));

//		BrokerConfig brokerConfig = new BrokerConfig();
//		brokerConfig.setServerAddress(serverAddr);


		ConstructorArgumentValues constructorArgumentValues = rootBeanDefinition.getConstructorArgumentValues();
		constructorArgumentValues.addGenericArgumentValue(new RuntimeBeanReference(configBeanName));

		rootBeanDefinition.setDestroyMethodName("close");
		return rootBeanDefinition;
	}
}