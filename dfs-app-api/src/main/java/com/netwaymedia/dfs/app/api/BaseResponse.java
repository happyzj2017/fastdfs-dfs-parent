package com.netwaymedia.dfs.app.api;

public class BaseResponse<T> {
	private int result; // 响应结果

	private T body;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}

}
