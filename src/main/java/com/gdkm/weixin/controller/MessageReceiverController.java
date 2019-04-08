package com.gdkm.weixin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kemao_2/message/receiver")
public class MessageReceiverController {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageReceiverController.class);
	@GetMapping
	public String echo(
			String signature,
			String timestamp,
			String nonce,
			String echostr
			) {
		return echostr;
	}
	
	@PostMapping
	public String onMessage(
			String signature,
			String timestamp,
			String nonce,
			@RequestBody String xml) {
		
		//收到消息
		LOG.trace("收到的消息原文：\n{}\n----------------------------------",xml);
		
		return "success";
	}
			
	
}
