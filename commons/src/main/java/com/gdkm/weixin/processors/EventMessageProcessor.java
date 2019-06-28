package com.gdkm.weixin.processors;

import com.gdkm.weixin.domain.event.EventInMessage;

public interface EventMessageProcessor {

	void onMessage(EventInMessage msg);
}
