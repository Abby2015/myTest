package com.yum.kfc.brand.common.pojo;

/**
 * @author luolix
 *
 */
public class FeiRuiResult {

	private int retcode = 0; // 结果代码
	private String msg = ""; // 返回消息
	private Object data; // 放回的数据对象
	
	public int getRetcode() {
		return retcode;
	}
	public void setRetcode(int retcode) {
		this.retcode = retcode;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	
}
