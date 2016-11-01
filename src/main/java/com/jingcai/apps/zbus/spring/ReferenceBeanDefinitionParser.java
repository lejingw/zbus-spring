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
import org.zbus.rpc.RpcFactory;
import org.zbus.rpc.RpcProcessor;
import org.zbus.rpc.mq.MqInvoker;
import org.zbus.rpc.mq.Service;
import org.zbus.rpc.mq.ServiceConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lejing on 15/9/23.
 */
public class ReferenceBeanDefinitionParser implements BeanDefinitionParser {
	private static final Logger logger = LoggerFactory.getLogger(ReferenceBeanDefinitionParser.class);
	private final Map<String, String> registedRpcFactoryMap = new HashMap<String, String>();

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String mq = element.getAttribute("mq");
		String broker = element.getAttribute("broker");
		String interfacename = element.getAttribute("interface");
		if (!StringUtils.hasText(mq)) {
			logger.error("init reference error, 'mq' should be set");
			return null;
		}
		if (!StringUtils.hasText(broker)) {
			logger.error("init reference error, 'broker' should be set");
			return null;
		}
		if (!StringUtils.hasText(interfacename)) {
			logger.error("init reference error, 'interface' should be set");
			return null;
		}
		Class<?> inter = null;
		try {
			inter = Class.forName(interfacename);
		} catch (ClassNotFoundException e) {
			logger.error("init reference error, interface[interfacename] can't be found");
			return null;
		}
//		Object source = parserContext.extractSource(element);

		String rpcFactoryName = registedRpcFactoryMap.get(getKey(broker, mq));
		if(null == rpcFactoryName) {
			RootBeanDefinition mqInvokerBean = new RootBeanDefinition(MqInvoker.class);
			mqInvokerBean.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference(broker));
			mqInvokerBean.getConstructorArgumentValues().addIndexedArgumentValue(1, mq);
			String mqInvokerName = parserContext.getReaderContext().registerWithGeneratedName(mqInvokerBean);
			parserContext.registerComponent(new BeanComponentDefinition(mqInvokerBean, mqInvokerName));

			RootBeanDefinition rpcFactoryBean = new RootBeanDefinition(RpcFactory.class);
			rpcFactoryBean.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference(mqInvokerName));
			rpcFactoryName = parserContext.getReaderContext().registerWithGeneratedName(rpcFactoryBean);
			parserContext.registerComponent(new BeanComponentDefinition(rpcFactoryBean, rpcFactoryName));

			registedRpcFactoryMap.put(getKey(broker, mq), rpcFactoryName);
		}

		RootBeanDefinition referenceBean = new RootBeanDefinition();
		referenceBean.setFactoryBeanName(rpcFactoryName);
		referenceBean.setFactoryMethodName("getService");
		referenceBean.getConstructorArgumentValues().addGenericArgumentValue(inter);
		String referenceName = element.getAttribute("id");
		if (StringUtils.hasText(referenceName)) {
			parserContext.getRegistry().registerBeanDefinition(referenceName, referenceBean);
		} else {
			referenceName = parserContext.getReaderContext().registerWithGeneratedName(referenceBean);
		}
		parserContext.registerComponent(new BeanComponentDefinition(referenceBean, referenceName));

		return referenceBean;

//		RootBeanDefinition rpcProcessorBeanDefinition = new RootBeanDefinition(RpcProcessor.class);
//		rpcProcessorBeanDefinition.setSource(source);
//		rpcProcessorBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
//		List<Element> listeners = DomUtils.getChildElementsByTagName(element, new String[]{"bean", "ref"});
//		int i = 0;
//		for (Element interceptor : listeners) {
//			Object listenerBean = parserContext.getDelegate().parsePropertySubElement(interceptor, null);
//			rpcProcessorBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(i++, listenerBean);
//		}
//		String rpcBeanName = parserContext.getReaderContext().registerWithGeneratedName(rpcProcessorBeanDefinition);
//		parserContext.registerComponent(new BeanComponentDefinition(rpcProcessorBeanDefinition, rpcBeanName));
//
//
//		RootBeanDefinition configBeanDefinition = new RootBeanDefinition(ServiceConfig.class);
//		configBeanDefinition.setSource(source);
//		configBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
//		configBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference(broker));
//		configBeanDefinition.getPropertyValues().add("mq", mq);
//		configBeanDefinition.getPropertyValues().add("consumerCount", consumerCount);
//		configBeanDefinition.getPropertyValues().add("messageProcessor", new RuntimeBeanReference(rpcBeanName));
//		String configBeanName = parserContext.getReaderContext().registerWithGeneratedName(configBeanDefinition);
//		parserContext.registerComponent(new BeanComponentDefinition(configBeanDefinition, configBeanName));
//
//
//		RootBeanDefinition serviceBeanDefinition = new RootBeanDefinition(Service.class);
//		serviceBeanDefinition.setSource(source);
//		serviceBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
//		serviceBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference(configBeanName));
//		String beanName = parserContext.getReaderContext().registerWithGeneratedName(serviceBeanDefinition);
//		parserContext.registerComponent(new BeanComponentDefinition(serviceBeanDefinition, beanName));
//
//		serviceBeanDefinition.setInitMethodName("start");
//		serviceBeanDefinition.setDestroyMethodName("close");
//		return serviceBeanDefinition;
	}

	private String getKey(String broker, String mq){
		return broker + "$" + mq;
	}
}