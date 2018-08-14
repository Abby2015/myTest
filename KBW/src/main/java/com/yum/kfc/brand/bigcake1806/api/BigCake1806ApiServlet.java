package com.yum.kfc.brand.bigcake1806.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.alibaba.fastjson.JSON;
import com.hp.jdf.ssm.api.ApiException;
import com.hp.jdf.ssm.util.SpringUtil;
import com.hp.jdf.ssm.util.StringUtil;
import com.yum.kfc.brand.ErrCode;
import com.yum.kfc.brand.bigcake1806.pojo.BcUser1806;
import com.yum.kfc.brand.bigcake1806.service.BigCake1806Service;
import com.yum.kfc.brand.common.api.BaseCampApiServlet;

/**
 * 
 * @author yidequan@cloudwalk.cn 2018年5月28日上午11:39:45
 *
 */
@WebServlet(urlPatterns = "/api/bigcake/*", asyncSupported = true)
public class BigCake1806ApiServlet extends BaseCampApiServlet {
	private static final long serialVersionUID = -2005854502582749527L;
	
	private static final BigCake1806Service bcService = SpringUtil.getBean(BigCake1806Service.class);
	
	@GET @Path("isAttend")
	private void userIsAttend(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String token = super.getStrParam(req, "token", null);
		
		if(StringUtil.isEmptyWithTrim(token)){
			throw new ApiException("Bad request", ErrCode.INVALID_TOKEN).setContext("invalid token");
		}

		//根据token获取用户信息
		UserInfo ui = super.getUserInfoByToken(token, req.getHeader("kbck"));
		if(ui == null){
			throw new ApiException("Bad request", ErrCode.INVALID_TOKEN).setContext("failed to get userInfo from KBS, token["+token+"]");
		}	
		
		final String crmUserCode = ui.getCrmUserCode();
		
		BcUser1806 bcUser = getBigCakeUserInfo(crmUserCode);
		boolean isAttend = bcUser == null ? false : true;
		String tasteStoreName = bcUser == null ? null: getTasteStoreName(bcUser.getCityName());
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("isAttend", isAttend);
		result.put("user", bcUser);
		result.put("tasteStoreName", tasteStoreName);

		req.setAttribute(RESULT, result);
	}

	private BcUser1806 getBigCakeUserInfo(final String crmUserCode) {
		Object o = super.getOrGenCacheableData("BIGCAKE1806_"+crmUserCode, null, Integer.MAX_VALUE, new CacheableDataGenerator(){

			@Override
			public CacheableData generate() {
				return new CacheableData(bcService.getUserInfo(crmUserCode));
			}
			
		});
		
		String so = String.valueOf(o);
		if (o==null||"null".equals(so)||so.isEmpty()) return null;
		if (o instanceof BcUser1806) return (BcUser1806)o; 
		return JSON.parseObject(so, BcUser1806.class);
	}
	
	private String getTasteStoreName(final String cityName) {
		Object o = super.getOrGenCacheableData("BIGCAKE1806_TASTESTORENAME"+cityName, null, Integer.MAX_VALUE, new CacheableDataGenerator(){

			@Override
			public CacheableData generate() {
				return new CacheableData(bcService.getTasteStoreName(cityName));
			}
			
		});
		
		return String.valueOf(o);
	}
}
