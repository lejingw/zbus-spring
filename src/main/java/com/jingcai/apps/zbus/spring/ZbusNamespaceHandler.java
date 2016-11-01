package com.jingcai.apps.zbus.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by lejing on 15/9/23.
 */
public class ZbusNamespaceHandler extends NamespaceHandlerSupport {
	public void init() {
		registerBeanDefinitionParser("broker", new BrokerBeanDefinitionParser());
		registerBeanDefinitionParser("subscribe", new SubscribeBeanDefinitionParser());
		registerBeanDefinitionParser("publish", new PublishBeanDefinitionParser());
		registerBeanDefinitionParser("produce", new ProduceBeanDefinitionParser());
		registerBeanDefinitionParser("consume", new ConsumeBeanDefinitionParser());
		registerBeanDefinitionParser("interfaces", new RpcServiceBeanDefinitionParser());
		registerBeanDefinitionParser("reference", new ReferenceBeanDefinitionParser());
	}
}
