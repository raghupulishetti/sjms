package com.sjms.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sjms.manager.ScheduleManager;

/**
 * ServiceBeanFactory holds the spring bean objects for future use in Job
 * execution time.
 * 
 * @author Raghu
 *
 */
@Component
public class ServiceBeanFactory {
	private final Logger LOGGER = LoggerFactory.getLogger(ServiceBeanFactory.class);
	public static Map<String, Object> BEAN_CONTAINER = null;

	@Autowired
	private JobService jobService;

	@Autowired
	private ScheduleManager scheduleManager;

	private void registerBeans() {
		LOGGER.info("JOb runtime register beans");
		BEAN_CONTAINER = new HashMap<>();
		BEAN_CONTAINER.put(ServiceBeanFactory.class.getName(), this);
		BEAN_CONTAINER.put(JobService.class.getName(), jobService);
		BEAN_CONTAINER.put(ScheduleManager.class.getName(), scheduleManager);
	}

	@PostConstruct
	private void initAll() {
		LOGGER.info("runtime initializing beans...");
		this.registerBeans();
		LOGGER.info("runtime beans initialized.");
	}

	public static <T> T getBean(Class<T> beanClass) {
		if (beanClass == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		T bean = (T) BEAN_CONTAINER.get(beanClass.getName());
		return bean;
	}
}
