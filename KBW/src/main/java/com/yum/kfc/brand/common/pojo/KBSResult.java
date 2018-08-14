package com.yum.kfc.brand.common.pojo;

public class KBSResult {
	private String errCode;
	private String errMsg;
	private String errData;

	public boolean isSuccess() {
		return "0".equals(errCode);
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getErrData() {
		return errData;
	}

	public void setErrData(String errData) {
		this.errData = errData;
	}
}
