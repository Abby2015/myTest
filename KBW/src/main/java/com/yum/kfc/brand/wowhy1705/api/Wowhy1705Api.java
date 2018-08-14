package com.yum.kfc.brand.wowhy1705.api;

import javax.jws.WebMethod;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.wowhy1705.api.impl.Wowhy1705Parameter;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Wowhy1705Api {

	/**
	 * 抽奖
	 * @param parameter
	 * @param request
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/draw") 
	public Result draw(Wowhy1705Parameter parameter, @Context HttpServletRequest request);
	
	/**
	 * 用户抽奖记录
	 * @param parameter
	 * @param request
	 * @return
	 */
	@WebMethod
	@GET
	@Path("/record") 
	public Result getDrawRecord(@QueryParam("token") String token, @Context HttpServletRequest request);
}
