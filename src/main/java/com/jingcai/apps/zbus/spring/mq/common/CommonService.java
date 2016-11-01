package com.jingcai.apps.zbus.spring.mq.common;

import com.jingcai.apps.common.lang.reflect.Reflections;
import com.jingcai.apps.common.lang.string.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by lejing on 15/1/28.
 */
public abstract class CommonService<T> implements BeanNameAware {
	protected static Logger logger = LoggerFactory.getLogger(CommonService.class);
	private static Map<String, String> beanNameMap = new HashMap<String, String>();
	private static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private static Lock r = rwl.readLock();
	private static Lock w = rwl.writeLock();


	/**
	 * 获取交易代码
	 *
	 * @return
	 */
	public abstract String getTransCode();

	public Class getRequestBeanClass() {
		return Reflections.getClassGenricType(this.getClass(), 0);
	}

	/**
	 * 检查请求参数是否正确
	 *
	 * @param req
	 */
	public abstract void checkRequest(T req);

	/**
	 * 执行业务逻辑
	 *
	 * @param req
	 */
	public abstract boolean action(T req);

	public void setBeanName(String name) {
		if (StringUtils.isEmpty(getTransCode())) {
			return;
		}
		logger.debug("完成加载业务bean[{}]", name);
		w.lock();
		try {
			beanNameMap.put(getTransCode(), name);
		} finally {
			w.unlock();
		}
	}

	public static String getServiceBeanName(String transCode) {
		r.lock();
		try {
			String beanName = beanNameMap.get(transCode);
			if (null == beanName) {
				logger.error("交易代码[{}]错误", transCode);
			}
			return beanName;
		} finally {
			r.unlock();
		}
	}
}
