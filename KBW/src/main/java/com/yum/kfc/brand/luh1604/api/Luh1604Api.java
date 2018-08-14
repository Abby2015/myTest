package com.yum.kfc.brand.luh1604.api;

import javax.jws.WebMethod;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.luh1604.pojo.Luh1604Open;
import com.yum.kfc.brand.luh1604.pojo.Luh1604User;

/**
 * 鹿晗1604活动 API
 * @author Yi Dequan
 *
 */

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Luh1604Api {
	
	/**
	 * 保存打开记录
	 * @param open
	 * @param request
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/open")
	public Result open(Luh1604Open open, @Context HttpServletRequest request);
	
	/**
	 * 参加活动并且上传头像
	 * @param user
	 * @param request
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/attendAndUpload")
	public Result attend(Luh1604User user, @Context HttpServletRequest request);
	
	/**
	 * 总共参加人数
	 * @return
	 */
	@WebMethod
	@GET
	@Path("/totalNumber")
	public Result getNumber();
	
	/**
	 * 检查参与者状态
	 * @param user
	 * @param request
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/checkUserStatus")
	public Result checkUserStatus(Luh1604User user, @Context HttpServletRequest request);
	
	@WebMethod
	@POST
	@Path("/uploadFileToAzure")
	public Result uploadFileToAzure(@Context HttpServletRequest request);
	
}
