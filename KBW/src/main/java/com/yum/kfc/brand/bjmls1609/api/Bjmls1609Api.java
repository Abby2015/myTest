package com.yum.kfc.brand.bjmls1609.api;

import javax.jws.WebMethod;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.yum.kfc.brand.bjmls1609.pojo.Bjmls1609Attend;
import com.yum.kfc.brand.bjmls1609.pojo.Bjmls1609Open;
import com.yum.kfc.brand.common.pojo.Result;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Bjmls1609Api {
	/**
	 * A01:打开活动页面
	 */
	@WebMethod
	@POST
	@Path("/open")
	public Result open(Bjmls1609Open open,  @Context HttpServletRequest request);
	
	/**
	 * A02:参加活动
	 */
	@WebMethod
	@POST
	@Path("/attend")
	public Result attend(Bjmls1609Attend attend, @Context HttpServletRequest request);
	
	@WebMethod
	@POST
	@Path("/isAttend")
	public Result isAttend(Bjmls1609Attend attend, @Context HttpServletRequest request);
}
