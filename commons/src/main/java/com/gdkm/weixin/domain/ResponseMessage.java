package com.gdkm.weixin.domain;

public abstract class ResponseMessage {

	// 自定义响应的状态值，1表示成功，其他值都表示失败
	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
