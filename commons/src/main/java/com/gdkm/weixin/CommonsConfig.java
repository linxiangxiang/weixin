package com.gdkm.weixin;

import com.gdkm.weixin.domain.InMessage;
import com.gdkm.weixin.json.JsonRedisSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public interface CommonsConfig extends
// 命令行运行器，表示此程序是一个命令行程序，需要重新run方法来实现程序的初始化。
// 使用一个线程等待程序的停止通知。
		CommandLineRunner, //
// 此接口实现以后，Spring会在销毁的时候自动调用此接口的方法
// 用于发送程序的停止通知
		DisposableBean {

	public static final Logger LOG = LoggerFactory.getLogger(CommonsConfig.class);

	// @Bean注解相当于在XML文件中写<bean>元素
	@Bean
	public default XmlMapper xmlMapper() {
		XmlMapper mapper = new XmlMapper();
		return mapper;
	}

	@Bean
	public default RedisTemplate<String, ? extends InMessage> inMessageTemplate(//
			@Autowired RedisConnectionFactory connectionFactory) {

		RedisTemplate<String, ? extends InMessage> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		// 使用序列化程序完成对象的序列化和反序列化，可以自定义
		template.setValueSerializer(new JsonRedisSerializer());

		return template;
	}

	// 停止监视器
	public final Object stopMonitor = new Object();

	// 程序初始化是被执行
	@Override
	public default void run(String... args) throws Exception {
		new Thread(() -> {
			// 等待停止通知
			synchronized (stopMonitor) {
				try {
					stopMonitor.wait();// 无限期等待
				} catch (InterruptedException e) {
					LOG.error("无法等待停止通知：" + e.getLocalizedMessage(), e);
				}
			}
		}).start();
	}

	// 程序最后执行的
	@Override
	public default void destroy() throws Exception {
		// 发送停止通知
		synchronized (stopMonitor) {
			stopMonitor.notify();
		}
	}
}
