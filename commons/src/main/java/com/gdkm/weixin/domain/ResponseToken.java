package com.gdkm.weixin.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseToken extends ResponseMessage {

	@JsonProperty("access_token")
	private String token;
	@JsonProperty("expires_in")
	private long expiresIn;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
}
