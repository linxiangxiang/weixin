package com.gdkm.weixin.service;

import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXB;

import com.gdkm.weixin.domain.InMessage;
import com.gdkm.weixin.domain.image.ImageInMessage;
import com.gdkm.weixin.domain.text.TextInMessage;

public class MessageConvertHelper {
	//1.使用一个Map来记录消息类型和Java类型的关系
	private static final Map<String,Class<? extends InMessage>> typeMap = new ConcurrentHashMap<>();
	static {
		typeMap.put("text", TextInMessage.class);
		typeMap.put("image", ImageInMessage.class);
		
		
		//其余的类型也是使用相同的方式关联起来
		typeMap.put("vioce", TextInMessage.class);
		typeMap.put("video", TextInMessage.class);
		typeMap.put("location", TextInMessage.class);
		typeMap.put("event", TextInMessage.class);
		typeMap.put("link", TextInMessage.class);
		typeMap.put("shortvideo", TextInMessage.class);
	}
	//2.搜索一个静态的方法，可以传入XML，把XML转化为JAVA对象
	
	public static <T extends InMessage> T convert(String xml) {
		//获取类型
		String type = xml.substring(xml.indexOf("<MsgType><![CDATA[")+18);
		type = type.substring(0,type.indexOf("]"));
		
		//获取JAVA类
		Class<? extends InMessage> c = typeMap.get(type);
		
		//使用JAXB转换
		@SuppressWarnings("unchecked")
		T msg = (T) JAXB.unmarshal(new StringReader(xml), c);
		
		return msg;
	}

}
