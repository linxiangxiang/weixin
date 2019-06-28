package com.gdkm.weixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.gdkm.weixin.domain.InMessage;
import com.gdkm.weixin.domain.event.EventInMessage;
import com.gdkm.weixin.processors.EventMessageProcessor;

@SpringBootApplication
@ComponentScan("com.gdkm")
public class UnsubscribeApplication implements //
		// 命令行运行器，表示此程序是一个命令行程序，需要重新run方法来实现程序的初始化。
		// 使用一个线程等待程序的停止通知。
		CommandLineRunner, //
		// 此接口实现以后，Spring会在销毁的时候自动调用此接口的方法
		// 用于发送程序的停止通知
		DisposableBean, //
		ApplicationContextAware, //
		CommonsConfig {

	private static final Logger LOG = LoggerFactory.getLogger(UnsubscribeApplication.class);

	private ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// 每当Spring容器初始化好以后，在初始化Bean的时候，把容器自身传入给Bean
		ctx = applicationContext;
	}

	@Bean
	public MessageListener messageListener(//
			@Autowired //
			@Qualifier("inMessageTemplate") //
			RedisTemplate<String, ? extends InMessage> inMessageTemplate) {
		// 适配器是把任意对象、任意方法代理成能够处理消息的方法
		MessageListenerAdapter adapter = new MessageListenerAdapter(this, "handle");
		// 使用跟模板相同的序列化程序。
		adapter.setSerializer(inMessageTemplate.getValueSerializer());
		return adapter;
	};

	// 此方法就可以正常处理信息！
	public void handle(EventInMessage msg) {
		LOG.trace("处理信息： {}", msg);
		// 1.获取事件的类型，并且在后面加上MessageProcessor组成Bean的id
		String id = msg.getEvent().toLowerCase() + "MessageProcessor";
		try {
			// 2.根据ID到容器里面获取Bean，强制转换为EventMessageProcessor对象
			EventMessageProcessor mp = (EventMessageProcessor) ctx.getBean(id);
			// 3.调用onMessage方法
			if (mp != null) {
				mp.onMessage(msg);
			} else {
				LOG.error("利用Bean的ID {} 不能找到一个事件消息处理器!", id);
			}
		} catch (NoSuchBeanDefinitionException e) {
			LOG.trace("当前模块不适合处理 {} 消息，没有对应的处理器实现", msg.getEvent());
		} catch (Exception e) {
			LOG.error("无法处理事件：" + e.getLocalizedMessage(), e);
		}
	}

	@Bean
	public RedisMessageListenerContainer messageListenerContainer(//
			@Autowired RedisConnectionFactory connectionFactory, //
			@Autowired MessageListener messageListener) {
		RedisMessageListenerContainer c = new RedisMessageListenerContainer();
		c.setConnectionFactory(connectionFactory);
		// 监听指定通道的消息
		Topic topic = new ChannelTopic("kemao_2_event");
		// 监听所有kemao_2_开头的通道里面的消息
//		Topic topic = new PatternTopic("kemao_2_*");
		// 添加消息的监听器
		c.addMessageListener(messageListener, topic);

		return c;
	}

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(UnsubscribeApplication.class, args);
	}
}
