package com.yum.kfc.brand.szz1604.api;

import javax.jws.WebMethod;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.szz1604.api.impl.Szz1604Parameter;


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Szz1604Api {
	/**
	 * A01:打开活动首页
	 */
	@WebMethod
	@POST
	@Path("/open")
	public Result open(Szz1604Parameter parameter, @Context HttpServletRequest request);
	
	
	/**
	 * A02: 抽奖
	 */
	@WebMethod
	@POST
	@Path("/draw")
	public Result draw(Szz1604Parameter parameter, @Context HttpServletRequest request);
}
