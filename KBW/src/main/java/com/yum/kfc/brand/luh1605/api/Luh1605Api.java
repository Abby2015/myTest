package com.yum.kfc.brand.luh1605.api;

import javax.jws.WebMethod;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.luh1605.api.impl.Luh1605Parameter;


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Luh1605Api {
	/**
	 * A01:打开活动首页
	 */
	@WebMethod
	@POST
	@Path("/open")
	public Result open(Luh1605Parameter parameter, @Context HttpServletRequest request);
	
	
	/**
	 * A02: 抽奖
	 */
	@WebMethod
	@POST
	@Path("/draw")
	public Result draw(Luh1605Parameter parameter, @Context HttpServletRequest request);
}
