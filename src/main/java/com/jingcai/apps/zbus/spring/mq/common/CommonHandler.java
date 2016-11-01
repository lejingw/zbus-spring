package com.jingcai.apps.zbus.spring.mq.common;

import com.jingcai.apps.common.lang.format.JsonMapper;
import com.jingcai.apps.zbus.spring.mq.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

/**
 * Created by lejing on 15/10/12.
 */
public class CommonHandler implements Handler {
	private final Logger logger = LoggerFactory.getLogger(CommonHandler.class);
	private final JsonMapper jsonMapper = JsonMapper.getInstance();
	@Autowired
	private ApplicationContext applicationContext;

	public void handle(String json) {
		logger.info("MQ接收消息:{}", json);
		RequestData requestData = null;
		try {
			requestData = jsonMapper.fromJson(json, RequestData.class);
		} catch (Exception e) {
			logger.error("传入数据格式错误", e);
			return;
		}
		if (null == requestData || StringUtils.isEmpty(requestData.getTradecode())) {
			logger.error("交易编码错误为空");
			return;
		}
		String beanName = CommonService.getServiceBeanName(requestData.getTradecode());
		if (null == beanName || !applicationContext.containsBean(beanName)) {
			logger.error("获取业务处理bean出错{}", requestData.getTradecode());
			return;
		}

		CommonService commonService = null;
		try {
			commonService = (CommonService) applicationContext.getBean(beanName);
		} catch (Exception e) {
			logger.error("获取业务处理bean出错", e);
			return;
		}

		Object request = null;
		try {
			Class requestBodyCls = commonService.getRequestBeanClass();
			if(String.class == requestBodyCls)
				request = requestData.getContent();
			else
				request = jsonMapper.fromJson(requestData.getContent(), requestBodyCls);
		} catch (Exception e) {
			logger.error("解析业务请求数据出错", e);
			return;
		}
		boolean result = false;
		try {
			commonService.checkRequest(request);
			result = commonService.action(request);
			if (result) {
				logger.info("MQ消息消费成功");
			} else {
				logger.error("MQ消息消费失败");
			}
		} catch (Exception e) {
			logger.error("执行业务逻辑出错", e);
			result = false;
		} finally {
			finalClean(requestData, result);
		}
	}

	protected void finalClean(RequestData requestData, boolean result){

	}
}
