package com.yum.kfc.brand.gkslz1606.api;

import javax.jws.WebMethod;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.gkslz1606.api.impl.Gkslz1606Parameter;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Gkslz1606Api {
	/**
	 * A01:打开活动页面
	 */
	@WebMethod
	@POST
	@Path("/open")
	public Result open(Gkslz1606Parameter parameter, @Context HttpServletRequest request);
	
	/**
	 * A02:活动抽奖
	 */
	@WebMethod
	@POST
	@Path("/draw")
	public Result draw(Gkslz1606Parameter parameter, @Context HttpServletRequest request);
	
	/**
	 * A03:活动分享
	 */
	@WebMethod
	@POST
	@Path("/share")
	public Result share(Gkslz1606Parameter parameter, @Context HttpServletRequest request);
}
