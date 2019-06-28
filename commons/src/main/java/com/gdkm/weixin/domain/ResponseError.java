package com.gdkm.weixin.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

// 返回响应的是错误信息
public class ResponseError extends ResponseMessage {

	@JsonProperty("errcode")
	private int errorCode;
	@JsonProperty("errmsg")
	private String errorMessage;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
