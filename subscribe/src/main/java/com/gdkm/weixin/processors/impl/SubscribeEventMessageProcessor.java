package com.gdkm.weixin.processors.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gdkm.weixin.domain.User;
import com.gdkm.weixin.domain.event.EventInMessage;
import com.gdkm.weixin.processors.EventMessageProcessor;
import com.gdkm.weixin.repository.UserRepository;
import com.gdkm.weixin.service.WeiXinProxy;

// 把Bean加入容器管理，默认类名首字母小写作为ID
// 如果@Service注解有值则表示自定义ID
@Service("subscribeMessageProcessor")
public class SubscribeEventMessageProcessor implements EventMessageProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(SubscribeEventMessageProcessor.class);
//	@Autowired
//	private AccessTokenManager accessTokenManager;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private WeiXinProxy weiXinProxy;

	@Override
	public void onMessage(EventInMessage msg) {
		LOG.trace("关注消息处理器: " + msg);
		String account = msg.getToUserName();
		String openId = msg.getFromUserName();
		// 1.检查用户是否有关注，如果以关注不需要处理
		User user = this.userRepository.findByOpenId(openId);
		// 2.如果用户未关注，则需要调用远程接口获取用户的资料，即使之前有关注，现在也重新获取用户信息
		if (user == null || user.getStatus() != User.Status.IS_SUBSCRIBE) {
			// 2.1.先要获取访问令牌，重点、难点
			// 2.2.调用远程接口
			User wxUser = weiXinProxy.getUser(account, openId);
			if (wxUser == null) {
				return;
			}
			// 3.把用户信息保存到数据库
			if (user != null) {
				wxUser.setId(user.getId());
				wxUser.setSubTime(user.getSubTime());
			} else {
				wxUser.setSubTime(new Date());
			}
			wxUser.setStatus(User.Status.IS_SUBSCRIBE);
			this.userRepository.save(wxUser);

			this.weiXinProxy.sendText(account, openId, "欢迎关注我的公众号，回复菜单可以获得人工智能菜单");
		}
//		String token = accessTokenManager.getToken("null");
//		LOG.trace("访问令牌: " + token);
	}
}
