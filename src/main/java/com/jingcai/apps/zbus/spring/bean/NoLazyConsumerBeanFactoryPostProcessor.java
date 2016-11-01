package com.jingcai.apps.zbus.spring.bean;

import com.jingcai.apps.common.lang.string.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.List;

/**
 * Created by lejing on 15/12/29.
 */
public class NoLazyConsumerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static final Logger log = LoggerFactory.getLogger(NoLazyConsumerBeanFactoryPostProcessor.class);
	private List<String> packageList;

	public void setPackageList(List<String> packageList) {
		this.packageList = packageList;
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (!(beanFactory instanceof DefaultListableBeanFactory)) {
			log.error("if speed up spring, bean factory must be type of DefaultListableBeanFactory");
			return;
		}

		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
			if (isConsumer(beanDef.getBeanClassName())) {
				beanDef.setLazyInit(false);
			}
		}
	}

	private boolean isConsumer(String classname) {
		if (StringUtils.isEmpty(classname)) {
			return false;
		}
		for (String pk : packageList) {
			if (classname.startsWith(pk))
				return true;
		}
		return false;
	}
}
