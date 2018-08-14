package com.yum.kfc.brand.luh1604.mqc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.api.ApiException;
import com.hp.jdf.ssm.rmq.BaseRabbitMQConsumer;
import com.hp.jdf.ssm.rmq.ConsumeException;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.MessageConsumer;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.util.BaseHttpClient;
import com.hp.jdf.ssm.util.BaseHttpClient.ResultHandler;
import com.hp.jdf.ssm.util.LogUtil;
import com.hp.jdf.ssm.util.NetUtil;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.yum.kfc.brand.common.utils.AzureClient;
import com.yum.kfc.brand.luh1604.api.impl.Luh1604ApiImpl;

public class AzureRabbitMQConsumer extends BaseRabbitMQConsumer{
private static final Logger log = LoggerFactory.getLogger(AzureRabbitMQConsumer.class);
	
	private static final String QUEUE_NAME = AzureClient.class.getCanonicalName();
	
	private static final Type TYPE_MAP_STR_STR = new TypeReference<Map<String, String>>(){}.getType();
	
//	private static String ROOTDIR_IMG = ApplicationConfig.getProperty("img.rootdir");
	
//	private static ExecutorService esTraceRequest = LogUtil.newTraceLogExecSvc();//Executors.newFixedThreadPool(100);//.newCachedThreadPool();
	
	private static String LOCAL_IP;
	
	private static final String SIGN_KEY = "";
	private static final String SIGN_SEC = "";
	
	static {
		//try {
			LOCAL_IP = NetUtil.getLocalIP();//InetAddress.getLocalHost().getHostAddress();
		//} catch (UnknownHostException e) {
		//	LOCAL_IP = "";
		//	log.error("Failed to get local IP", e);
		//}
	}
	
	public AzureRabbitMQConsumer() {
		CONSUMERS = Integer.parseInt(ApplicationConfig.getProperty("rmq.queue.consumers.azclient", "1"));
	}
	
	@Override
	protected void consume() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException {
		RabbitMQHelper.consume(QUEUE_NAME, new MessageConsumer(){
			@Override
			public void consume(final Message msg) throws ConsumeException{
				try{
					log.info("============== Azure consume");
					Map<String, String> params = JSON.parseObject(String.valueOf(msg.getPayload()), TYPE_MAP_STR_STR);
					String fileAbsolutePath = params.get("filePath");
					String containerName = params.get("containerName");
					String blobName = params.get("blobName");
					String contentType = params.get("contentType");
					
					if (fileAbsolutePath==null || fileAbsolutePath.trim().isEmpty()
							|| containerName==null || containerName.trim().isEmpty()
							|| blobName==null || blobName.trim().isEmpty()
						){
						log.warn("invalid azure message, discard, [{}]", msg);
						return;
					}
					
//					if (!fileAbsolutePath.startsWith(ROOTDIR_IMG)||fileAbsolutePath.indexOf("..")>=0) {
//						log.error("SUSPECIOUS REQUEST, discard: [{}]", msg.getPayload());
//						return;
//					}
					
//					try{
//						AzureClient.checkContainerName(containerName);
//						AzureClient.checkBlobName(blobName);
//					}catch(IllegalArgumentException e){
//						log.warn("invalid azure message, discard, [{}]", msg, e);
//						return;
//					}
					
					if (new File(fileAbsolutePath).exists()){
						Boolean deleteAfterUpload = Boolean.valueOf(params.get("deleteAfterUpload"));
						Luh1604ApiImpl.uploadToAzure(fileAbsolutePath, contentType, containerName, blobName, deleteAfterUpload);
					} else {
						String origin = params.get("origin");
						log.info("============== Azure consume origin:{}", origin);
						String urlStr = null;
						String bodyStr = JSON.toJSONString(params);
						Map<String, String> headers = new HashMap<String, String>();
						if (origin==null||(origin=origin.trim()).isEmpty()){
//							throw new FileNotFoundException("file not found at ["+LOCAL_IP+"], try again late for ["+msg.getPayload()+"]");
							for(int i=0; i<4; i++){
								if(i==0){
									log.info("============== upload from CapmaginServer:172.31.77.85");
									urlStr = "http://172.31.77.85:18080/brandkfc_luh1604/api/luh1604/uploadFileToAzure";
								}
								if(i==1){
									log.info("============== upload from CapmaginServer:172.31.77.86");
									urlStr = "http://172.31.77.86:18080/brandkfc_luh1604/api/luh1604/uploadFileToAzure";
								}
								if(i==2){
									log.info("============== upload from CapmaginServer:172.31.77.89");
									urlStr = "http://172.31.77.89:18080/brandkfc_luh1604/api/luh1604/uploadFileToAzure";
								}
								if(i==3){
									log.info("============== upload from CapmaginServer:172.31.77.90");
									urlStr = "http://172.31.77.90:18080/brandkfc_luh1604/api/luh1604/uploadFileToAzure";
								}
								
								tryOtherServer(urlStr, headers, bodyStr);
							}
						}else {
							log.info("============== upload from CapmaginServer:{}", origin);
							urlStr = "http://"+origin+":18080/brandkfc_luh1604/api/luh1604/uploadFileToAzure";
						}
						
						final String url = urlStr;//TODO configure the port and path
						final String body = bodyStr;
						
						
						new BaseHttpClient(){}.callHttp(BaseHttpClient.HttpMethod.POST, url, headers, body, ContentType.APPLICATION_JSON, new ResultHandler(){
							@Override
							public void handle(String resultFromHttp, HttpResponse rawResp) {
								JSONObject jo = JSONObject.parseObject(resultFromHttp);
								if (jo!=null && "0".equals(jo.getString("errCode"))){
									if (log.isTraceEnabled()) {//TODO why log.isTraceEnabled() return false always???
//										esTraceRequest.submit(new Runnable(){@Override public void run(){
											log.trace("successfully upload file to azure thru [{}] for [{}]", url, body);
//										}});
									}
								} else {
									throw new ApiException("failed to upload file to azure").setContext("thru ["+url+"], reason ["+resultFromHttp+"], msg ["+body+"]");
								}
							}
						});
					}
				}catch(JSONException e){
					log.info("===============msg.getPayload():{}", msg.getPayload());
					log.info("===============String.valueOf(msg.getPayload)):{}", String.valueOf(msg.getPayload()));
					log.info("===============JSON exception:{}", e);
					
				} catch(Exception e){
					throw new ConsumeException(e);
				}
			}
		});
	}
	
	public void tryOtherServer(String urlStr, Map<String, String> headers, String bodyStr){
		final String url = urlStr;//TODO configure the port and path
		final String body = bodyStr;
		try {
			new BaseHttpClient(){}.callHttp(BaseHttpClient.HttpMethod.POST, url, headers, body, ContentType.APPLICATION_JSON, new ResultHandler(){
				@Override
				public void handle(String resultFromHttp, HttpResponse rawResp) {
					JSONObject jo = JSONObject.parseObject(resultFromHttp);
					if (jo!=null && "0".equals(jo.getString("errCode"))){
						if (log.isTraceEnabled()) {//TODO why log.isTraceEnabled() return false always???
//							esTraceRequest.submit(new Runnable(){@Override public void run(){
								log.trace("successfully upload file to azure thru [{}] for [{}]", url, body);
//							}});
						}
					} else {
						throw new ApiException("failed to upload file to azure").setContext("thru ["+url+"], reason ["+resultFromHttp+"], msg ["+body+"]");
					}
				}
			});
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
