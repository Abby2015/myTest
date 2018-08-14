package com.yum.kfc.brand.luh1604.api.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.danga.MemCached.MemCachedClient;
import com.hp.jdf.ssm.api.ApiException;
import com.hp.jdf.ssm.api.BaseApiServlet;
import com.hp.jdf.ssm.rmq.FailPublishHandler;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.util.NetUtil;
import com.hp.jdf.ssm.util.StringUtil;
import com.hp.jdf.ssm.web.util.WebUtil;
import com.yum.kfc.brand.common.constant.ApiErrorCode;
import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.common.utils.AzureClient;
import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.common.utils.RestClientUtil;
import com.yum.kfc.brand.luh1604.api.Luh1604Api;
import com.yum.kfc.brand.luh1604.pojo.Luh1604Open;
import com.yum.kfc.brand.luh1604.pojo.Luh1604User;
import com.yum.kfc.brand.luh1604.service.Luh1604Service;

@Component
@Path("/luh1604")
@Scope("singleton")
public class Luh1604ApiImpl extends BaseApiServlet implements Luh1604Api {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(Luh1604ApiImpl.class);
	private final String CACHE_KEY_PREFIX = "KFC.LUH.LUH1604.";
	private final String CACHE_KEY_USERATTEND = CACHE_KEY_PREFIX+"ATTENDUSER-"; 
	private final String CACHE_KEY_USERID = CACHE_KEY_PREFIX+"LOGIN_USERID-";
	private final String CACHE_KEY_USERPHOTO = CACHE_KEY_PREFIX+"USER_PHOTO-";
	private final String IMAGE_SUFFIX = ".png";
//	private final String CAMPAIGN_PREFIX = "luh1604_";
	private static final FileNameMap FILE_NAME_MAP = URLConnection.getFileNameMap();
	private static String LOCAL_IP;
	
	@Value("${campaign.svc.kbs.user}")	private String KBS_GET_USER_URL;
	@Value("${file.root.path}")			private String rootDir;
	@Value("${ws.azure.storage.image.root}") private String AZURE_IR;
//	@Value("${campaign.radix.number}")	private int RADIX_NUMBER;
	
	@Autowired 
	private MemCachedClient memcachedClient;
	
	@Autowired
	private Luh1604Service service;
	
	
	//用户渠道(0: Brand App; 1: 微信; 2:浏览器)
		public static final int CHANNEL_TYPE_APP = 0;
		public static final int CHANNEL_TYPE_WECHAT = 1;
		public static final int CHANNEL_TYPE_BROWSER = 2;
		
		//设备类型(0: android; 1:ios; 2:browser)
		public static final int DEVICE_TYPE_ANDRIOD	 = 0;
		public static final int DEVICE_TYPE_IOS	 = 1;
		public static final int DEVICE_TYPE_BROWSER	 = 2;
		
		public static List<Integer> CHANNEL_TYPE_LIST = new ArrayList<Integer>();
		public static List<String> MEDIA_TYPE_LIST = new ArrayList<String>();
		public static List<Integer> DEVICE_TYPE_LIST = new ArrayList<Integer>();
		public static List<Integer> CHOICE_LIST = new ArrayList<Integer>();
		public static List<Integer> SHARE_RESULT_LIST = new ArrayList<Integer>();
		public static List<Integer> MENU_TYPE_LIST = new ArrayList<Integer>();
		public static List<Integer> ASK_TYPE_LIST = new ArrayList<Integer>();
		
		static{
			//用户渠道(0: Brand App; 1: 微信; 2:浏览器)
			CHANNEL_TYPE_LIST.add(CHANNEL_TYPE_APP);
			CHANNEL_TYPE_LIST.add(CHANNEL_TYPE_WECHAT);
			CHANNEL_TYPE_LIST.add(CHANNEL_TYPE_BROWSER);
			//分享媒介类型
			MEDIA_TYPE_LIST.add("QQ");
			MEDIA_TYPE_LIST.add("WX");
			MEDIA_TYPE_LIST.add("TWB");
			MEDIA_TYPE_LIST.add("WB");
			MEDIA_TYPE_LIST.add("RR");
			MEDIA_TYPE_LIST.add("DB");
			MEDIA_TYPE_LIST.add("TB");
			MEDIA_TYPE_LIST.add("ZFB");
			//设备类型(0: android; 1:ios; 2:browser)
			DEVICE_TYPE_LIST.add(DEVICE_TYPE_ANDRIOD);
			DEVICE_TYPE_LIST.add(DEVICE_TYPE_IOS);
			DEVICE_TYPE_LIST.add(DEVICE_TYPE_BROWSER);
			//问题选择范围(1靠谱，0不靠谱，-1未选择)
			CHOICE_LIST.add(1);
			CHOICE_LIST.add(0);
			CHOICE_LIST.add(-1);
			//分享结果
			SHARE_RESULT_LIST.add(0);
			SHARE_RESULT_LIST.add(1);
			//菜单类型
			MENU_TYPE_LIST.add(0);
			MENU_TYPE_LIST.add(1);
			//求友类型
			ASK_TYPE_LIST.add(0);
			ASK_TYPE_LIST.add(1);
		}
	
	static {
		//try {
			LOCAL_IP = NetUtil.getLocalIP();//InetAddress.getLocalHost().getHostAddress();
		//} catch (UnknownHostException e) {
		//	LOCAL_IP = "";
		//	logger.error("Failed to get local IP", e);
		//}
	}
	
	private static enum CacheOp{
		GET, GETS, ADD, SET, INCR, ADDORINCR
	}
	
	private static interface DataGenerator{
		Object generate();
	}
	
	private Object getCache(String key, Date expiry, CacheOp copSave, DataGenerator dg){
		Object o = memcachedClient.get(key);
		if (o==null && dg!=null){
			o = dg.generate();
			if (o!=null){
				switch (copSave){
				case ADD:
					memcachedClient.add(key, String.valueOf(o), expiry);
					break;
				case SET:
					memcachedClient.set(key,  String.valueOf(o), expiry);
					break;
				default:
					throw new RuntimeException(copSave+" not support in this method");
				}
			}
		}
		return o;
	}
	
	private String getSortNO(final String userId) {
		Object retValue = getCache(CACHE_KEY_USERATTEND+userId, null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				Long id = service.getIdByUserId(userId);
				return id ==null ? null:id;
			}
		});
		return null == retValue ? "" : retValue.toString();
	}
	
	private String getPhotoUrl(final String userId) {
		Object retValue = getCache(CACHE_KEY_USERPHOTO+userId, null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				String photoUrl = service.getUserPhoto(userId);
				return photoUrl ==null ? null:photoUrl;
			}
		});
		return null == retValue ? "" : retValue.toString();
	}
	
	private String getUserId(final String token) {
		Object retValue = getCache(CACHE_KEY_USERID+token, null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				String userId = getUserIdByToken(token);
				return userId;
			}
		});
		return null == retValue ? "" : retValue.toString();
	}
	
	@Override
	public Result open(Luh1604Open open, HttpServletRequest request) {
		open.setIpAddr(WebUtil.getRealRemoteAddr(request));
		//检查渠道类型是否正确
		if(null == open.getChannelType()){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}else if(!CHANNEL_TYPE_LIST.contains(open.getChannelType())){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "Channel Type is incorrect");
		}
		//检查设备类型是否正确
		if(null == open.getDeviceType()){
			return new Result(ApiErrorCode.DEVICE_TYPE_ISNULL, "Device Type is empty");
		}else if(!DEVICE_TYPE_LIST.contains(open.getDeviceType())){
			return new Result(ApiErrorCode.DEVICE_TYPE_INCORRECT, "Device Type is incorrect");
		}
		//检查是否存在设备号
		if(null == open.getDeviceId()){
			return new Result(ApiErrorCode.DEVICE_UUID_ISNULL, "DeviceId is empty");
		}
		//检查token
		if(null == open.getToken() || open.getToken().trim().isEmpty()){
			return new Result(ApiErrorCode.MISSING_TOKEN, "no access token");
		}
		//检查userId
		String userId = getUserId(open.getToken());
		if(userId.isEmpty()){
			return new Result(ApiErrorCode.INVALID_TOKEN, "token is invalid");
		}
		open.setUserId(userId);
		open.setId(newUUID());
		open.setOpenTime(new Date());
		final Luh1604Open luhOpen = open;
		Result result = null;
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, luhOpen);
			RabbitMQHelper.publish(Luh1604Open.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						service.saveOpen(luhOpen);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			service.saveOpen(open);
		}

		result =  new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, "", null);
		logger.info("====luhan1604 open ====\n"+JSON.toJSONString(result));
		return result;
	}

	@Override
	public Result attend(Luh1604User user, HttpServletRequest request) {
		//检查token
		if(null == user.getToken() || user.getToken().trim().isEmpty()){
			return new Result(ApiErrorCode.MISSING_TOKEN, "no access token");
		}
		
		String userId = getUserId(user.getToken());
		if(userId.isEmpty()){
			return new Result(ApiErrorCode.INVALID_TOKEN, "token is invalid");
		}
		
		//检查用户是否已经参与过
		String sortNO = this.getSortNO(userId);
		if(!sortNO.trim().isEmpty()){
			return new Result(ApiErrorCode.USER_HAVE_ATTEND, "User have participated!");
		}
		Date date = new Date();
		String absolutePath = null;
		boolean deleteFile = false;
		String filename = DateUtil.formatDate(date, "yyyyMMddHHmmssSSS") + IMAGE_SUFFIX;
		if(user.getPhotoUrl() == null || user.getPhotoUrl().trim().isEmpty()){
			if(user.getBase64Code() == null || user.getBase64Code().trim().isEmpty()){
				return new Result(ApiErrorCode.MISSING_BASE64CODE, "PhotoUrl and Base64Code are missing.");
			}
			logger.info("====================================upload photo to local from base64");
			try {
				
				String serialDate = DateUtil.formatDate(date, "yyyy-MM-dd HH");
    			String path = File.separator + "photos" + File.separator + serialDate + File.separator + filename;
				absolutePath = rootDir + path;
				logger.info("================================From base64, userId:{}, filePath:{}", userId, absolutePath);
				File pf = new File(absolutePath);
    			pf.getParentFile().mkdirs();
				byte[] buffer = new BASE64Decoder().decodeBuffer(user.getBase64Code());
				FileOutputStream out = new FileOutputStream(absolutePath);
				out.write(buffer);
				out.close();
				if(!pf.isFile()){
					String failedFilePath = rootDir + File.separator + "failedBase64" + File.separator + filename + ".txt";
					FileOutputStream  fos= new FileOutputStream(failedFilePath);
					fos.write(buffer);
					fos.close();
				}
			} catch (IOException e) {
				logger.info("save photo from base64 to local occurs exception.{}", e);
				return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "please try again.");
			}
			 
		}else {
			String pu = user.getPhotoUrl().toLowerCase();
			if(pu.startsWith("http://") || pu.startsWith("https://")){
				logger.info("====================================download azure photo to local");
				if(pu.startsWith("https://")){
					pu = pu.replace("https://", "http://");
				}
				try {
					URL httpUrl = new URL(pu);
//	    			String path = File.separator + filename;
	    			String serialDate = DateUtil.formatDate(date, "yyyy-MM-dd HH");
	    			String path = File.separator + "photos" + File.separator + serialDate + File.separator + filename;
	    			File file = new File(rootDir+path);
	    			FileUtils.copyURLToFile(httpUrl, file);	    			
	    			absolutePath = file.getAbsolutePath();
	    			logger.info("================================from photoUrl, userId:{}, filePath:{}", userId, absolutePath);
				} catch (Exception e) {
					logger.info("download photo from url failed:{}", e);
					return new Result(ApiErrorCode.INVALID_URL, "invalid photo url");
				}
			}else {
				logger.info("====================================get photo from local, userId:{}, filePath:{}", userId, absolutePath);
				String filePath = rootDir+ File.separator + pu + ".png";
				File file = new File(filePath);
				absolutePath = file.getAbsolutePath();
//				deleteFile = false;
			}
		}
		
		String containerName = AZURE_IR;
		String blobName = filename;
		try{
			File file = new File(absolutePath);
			String fn = file.getName();
			uploadToAzureViaMQ(absolutePath, fn==null?null:FILE_NAME_MAP.getContentTypeFor(fn), containerName, blobName, deleteFile);
		}catch(Exception e){
			logger.error("request@{}, {}: upload uhi to azure failed [{}]", request.hashCode(), DateUtil.formatDate(new Date()), StringUtil.getMsgOrClzName(e, true), e);
		}
		
		//save user information
		final Luh1604User LuhUser = user;
		LuhUser.setUserId(userId);
		LuhUser.setHeadImgPath(blobName);
		LuhUser.setIpAddr(WebUtil.getRealRemoteAddr(request));
		LuhUser.setAttendTime(date);
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, LuhUser);
			RabbitMQHelper.publish(Luh1604User.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						service.saveAttendUser(LuhUser);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			service.saveAttendUser(LuhUser);
		}
		
		//delete photos on local
//		String photoFilePath = rootDir + File.separator + "photos";
//		deleteLocalPhotos(photoFilePath);
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("userId", userId);
		data.put("message", "谢谢参与！");
		data.put("photoUrl", "http://yumsuperapp.blob.core.chinacloudapi.cn/campaign/" + blobName);
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("=========luhan1604 attend Success =========\n"+ JSON.toJSONString(result));
		return result;
	}

	public static void deleteLocalPhotos(final String photoFilePath) {
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Map<String, String> params = new HashMap<String, String>();
			params.put("photoFilePath", photoFilePath);
			Message msg = new Message(Message.Type.CREATE, params);
			RabbitMQHelper.publish("DeleteLuhPic", msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to delete photo directly",  reason.getMessage(), reason);
					try{
						deleteFileByTime(photoFilePath);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
			});
		} else {
			deleteFileByTime(photoFilePath);
		}
	}
	
	public static void deleteFileByTime(String photoFilePath) {
		logger.info("=======================deleteFileByTime");
		try {
			File file = new File(photoFilePath);
			File[] files = file.listFiles();
			for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件
		       String createTime = files[i].getName() + ":00:00";
		       Date date = DateUtil.parseDate(createTime);
		       Date currentDate = new Date();
		       long diff = currentDate.getTime() - date.getTime();
		       long day = diff / (24 * 60 * 60 * 1000);
		       long hour = (diff / (60 * 60 * 1000) - day * 24);
		       
		       if(hour >=20){
		    	   deleteFile(files[i]);
		       }
		    }
		} catch (Exception e) {
			logger.info("=====================deleteFileByTime error:{}", e);
		}
		
	}

	@Override
	public Result getNumber() {
		Long totalNumber;
		try {
			totalNumber = service.getNumber();
		} catch (Exception e) {
			return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "general server error, please try it later");
		}
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("totalNumber", totalNumber);
		
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("====get totalNumber====\n"+ JSON.toJSONString(result));
		return result;
	}

	@Override
	public Result checkUserStatus(Luh1604User user, HttpServletRequest request) {
		//检查token
		if(null == user.getToken() || (user.getToken()).isEmpty()){
			return new Result(ApiErrorCode.MISSING_TOKEN, "no access token");
		}
		
		//根据token获取用户信息
		String userId = getUserId(user.getToken());
		if(userId.isEmpty()){
			return new Result(ApiErrorCode.INVALID_TOKEN, "token is invalid");
		}
		
		boolean attend = false;
		String sortNO = this.getSortNO(userId);
		if(!sortNO.trim().isEmpty()){
			attend = true;
		}
		
		String photoUrl = getPhotoUrl(userId);
			
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("userId", userId);
		data.put("attend", attend);
		data.put("sortNO", sortNO);
		if(attend){
			data.put("photoUrl", "http://yumsuperapp.blob.core.chinacloudapi.cn/campaign/" + photoUrl);
		}else {
			data.put("photoUrl", "");
		}
		
		
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("====user status====\n"+ JSON.toJSONString(result));
		return result;
	}
	
	private String getUserIdByToken(String token) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("token", token);
		String str = RestClientUtil.callPostService(KBS_GET_USER_URL, p, String.class);
		logger.info("request award from kbs, param[{}], result[{}]", p, str);
		JSONObject object = JSONObject.parseObject(str).getJSONObject("data");
		
		String userId = null;
		if(object != null){
			userId = object.getString("userId");
		}
//		memcachedClient.add(CACHE_KEY_USERID + token, userId);	
		return userId;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> upload(HttpServletRequest request) {
		List<String> targetPaths = new ArrayList<String>();
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if(!isMultipart) {
        	logger.info(" request didn't contain any file ");
            return null;
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(2048);
        ServletFileUpload fileUpload = new ServletFileUpload(factory);
        try {
            List<FileItem> items = fileUpload.parseRequest(request);
            for (FileItem item : items) {
                if (item == null || item.isFormField()) {
                    continue;
                }
                String filename = UUID.randomUUID().toString().replace("-", "");
//                String serialDate = DateUtil.getCurrentDate().replace("-", "");
    			String path = File.separator + filename;
    			File pf = new File(rootDir+path);
    			pf.getParentFile().mkdirs();
    			item.write(pf);
    			targetPaths.add(File.separatorChar=='\\'?path.replace(File.separatorChar, '/'):path);
            }
        } catch (Exception e) {
        	logger.error("====================update file error: {}", e);
        }
		return targetPaths;
	}
	
	public void uploadToAzureViaMQ(final String fileAbsolutePath, final String contentType, final String containerName, final String blobName, final boolean deleteAfterUpload){
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Map<String, String> params = new HashMap<String, String>();
			params.put("filePath", fileAbsolutePath);
			params.put("containerName", containerName);
			params.put("blobName", blobName);
			params.put("origin", LOCAL_IP);
			params.put("contentType", contentType);
			params.put("deleteAfterUpload", ""+deleteAfterUpload);
			Message msg = new Message(Message.Type.CREATE, params);
			RabbitMQHelper.publish(AzureClient.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to upload to Azure directly",  reason.getMessage(), reason);
					try{
						uploadToAzure(fileAbsolutePath, contentType, containerName, blobName, deleteAfterUpload);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
			});
		} else {
			uploadToAzure(fileAbsolutePath, contentType, containerName, blobName, deleteAfterUpload);
		}
	}
	
	public static void uploadToAzure(String fileAbsolutePath, String contentType, String containerName, String blobName, boolean deleteAfterUpload){
		File f = new File(fileAbsolutePath);
		
		try {
			if(f.isFile()){
				if(f.canRead()){
					AzureClient.uploadBlob(f, contentType, containerName, blobName, false);//container inited in static block already
				}else {
					logger.info("================file can not read:{}", fileAbsolutePath);
					Thread.sleep(1000);
					AzureClient.uploadBlob(f, contentType, containerName, blobName, false);
				}
				
			}else{
				logger.info("================is not a file:{}", fileAbsolutePath);
				Thread.sleep(1000);
				AzureClient.uploadBlob(f, contentType, containerName, blobName, false);
			}
//			if(f.exists()){
//				AzureClient.uploadBlob(f, contentType, containerName, blobName, false);//container inited in static block already
//			}else{
//				logger.info("================file not exist:{}", fileAbsolutePath);
//			}
		} catch (Throwable e) {
			throw new ApiException("Remote Azure Server Error", ApiErrorCode.GENERAL_SERVER_ERROR_AZS, e).setContext(StringUtil.getMsgOrClzName(e, true));
		}
		
//		if (deleteAfterUpload){
//			try{
//				f.delete();
//			}catch(Throwable e){
//				logger.warn("failed to delete file uploaded to azure storage already {}", StringUtil.getMsgOrClzName(e, true), e);
//			}
//		}
	}
	@Override
	public Result uploadFileToAzure(HttpServletRequest request){
		JSONObject jo;
		try {
			jo = (JSONObject) super.getBodyAsJson(request);
			final String fileAbsolutePath = jo.getString("filePath");
			final String containerName = jo.getString("containerName");
			final String blobName = jo.getString("blobName");
			final String contentType = jo.getString("contentType");
			final boolean deleteAfterUpload = jo.getBooleanValue("deleteAfterUpload");
			
			uploadToAzure(fileAbsolutePath, contentType, containerName, blobName, deleteAfterUpload);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, "", null);
		logger.info("====get totalNumber====\n"+ JSON.toJSONString(result));
		return result;
	}

	
//	public String getBlobName(int number){
//		int a, b, c;
//		a = number/RADIX_NUMBER;
//		b = number%RADIX_NUMBER;
//		c = a + 1;
//		
//		String path;	
//		if(number == 0 || b>0){
//			path = CAMPAIGN_PREFIX + c + File.separator + number + IMAGE_SUFFIX;
//		}else {
//			path = CAMPAIGN_PREFIX + a + File.separator + number + IMAGE_SUFFIX;
//		}
//		
//		String blobName = File.separatorChar=='\\'?path.replace(File.separatorChar, '/'):path;
//		return blobName;
//	}
	
	// 递归删除文件夹
	public static void deleteFile(File file) {
		if (file.exists()) {// 判断文件是否存在
			if (file.isFile()) {// 判断是否是文件
				file.delete();// 删除文件
			} else if (file.isDirectory()) {// 否则如果它是一个目录
				File[] files = file.listFiles();// 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) {// 遍历目录下所有的文件
					deleteFile(files[i]);// 把每个文件用这个方法进行迭代
				}
				file.delete();// 删除文件夹
			}
		} else {
			logger.info("所删除的文件不存在");
		}
	}
	
	public static String newUUID(){
		return String.format("%X%S", new Date().getTime(), UUID.randomUUID().toString().replace("-", ""));
	}
}
