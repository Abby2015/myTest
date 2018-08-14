package com.yum.kfc.brand.common.pojo;

public class Result {

	private int errCode = 0; // 结果代码
	private String errMsg = ""; // 返回错误消息
	private Object data; // 放回的数据对象

	public Result() {
	}

	public Result(int errCode) {
		this.errCode = errCode;
	}

	public Result(int errCode, String errMsg) {
		this.errCode = errCode;
		this.errMsg = null == errMsg ? "" : errMsg;
	}

	public Result(int errCode, String errMsg, Object data) {
		this.errCode = errCode;
		this.errMsg = null == errMsg ? "" : errMsg;
		this.data = data;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
