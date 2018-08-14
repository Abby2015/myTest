package com.yum.kfc.brand.common.mqc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
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
import com.hp.jdf.ssm.util.StringUtil;
import com.hp.jdf.ssm.util.BaseHttpClient.ResultHandler;
import com.hp.jdf.ssm.util.NetUtil;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.yum.kfc.brand.common.utils.CspAzureClient;
import com.yum.kfc.brand.crm.pojo.ClientChannel;
import com.yum.kfc.brand.kbm.api.CSPApiServlet;

/**
 * 
 * @author yidequan@cloudwalk.cn 2018年5月24日下午5:54:07
 *
 */
public class CspAzureRabbitMQConsumer extends BaseRabbitMQConsumer {
	
	private static final Logger log = LoggerFactory.getLogger(CspAzureRabbitMQConsumer.class);
	
	private static final String QUEUE_NAME = CspAzureClient.class.getCanonicalName();
	
	private static final Type TYPE_MAP_STR_STR = new TypeReference<Map<String, String>>(){}.getType();
	
	private static String ROOTDIR_IMG = ApplicationConfig.getProperty("img.rootdir");
		
	private static final String SIGN_KEY;
	private static final String SIGN_SEC;
	
	private static String LOCAL_IP;
	
	static {
		//try {
			LOCAL_IP = NetUtil.getLocalIP();//InetAddress.getLocalHost().getHostAddress();
		//} catch (UnknownHostException e) {
		//	LOCAL_IP = "";
		//	log.error("Failed to get local IP", e);
		//}
		
		Map<String, String> m = JSON.parseObject(StringUtil.stripEmbeddedComment(ApplicationConfig.getProperty("sec.client.keysec")), new TypeReference<TreeMap<String, String>>(){}.getType());
		String k=null, v=null;
		for(Entry<String, String> e: m.entrySet()){
			if (e.getKey().startsWith(ClientChannel.ksys.name())){
				k = e.getKey();
				v = e.getValue();
			}
		}
		SIGN_KEY = k;
		SIGN_SEC = v;
	}
	
	public CspAzureRabbitMQConsumer() {
		CONSUMERS = Integer.parseInt(ApplicationConfig.getProperty("rmq.queue.consumers.azclient", "1"));
	}
	
	@Override
	protected void consume() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException {
		RabbitMQHelper.consume(QUEUE_NAME, new MessageConsumer(){
			@Override
			public void consume(final Message msg) throws ConsumeException{
				try{
					Map<String, String> params = JSON.parseObject(String.valueOf(msg.getPayload()), TYPE_MAP_STR_STR);
					String fileAbsolutePath = params.get("filePath");
					String containerName = params.get("containerName");
					String blobName = params.get("blobName");
					String contentType = params.get("contentType");
					Boolean sameDiskAccrossNodes = Boolean.parseBoolean(params.get("sameDiskAccrossNodes"));
					
					if (fileAbsolutePath==null || fileAbsolutePath.trim().isEmpty()
							|| containerName==null || containerName.trim().isEmpty()
							|| blobName==null || blobName.trim().isEmpty()
						){
						log.warn("invalid azure message, discard, [{}]", msg);
						return;
					}
					
					if (!fileAbsolutePath.startsWith(ROOTDIR_IMG)||fileAbsolutePath.indexOf("..")>=0) {
						log.error("SUSPECIOUS REQUEST, discard: [{}]", msg.getPayload());
						return;
					}
					
					try{
						CspAzureClient.checkContainerName(containerName);
						CspAzureClient.checkBlobName(blobName);
					}catch(IllegalArgumentException e){
						log.warn("invalid azure message, discard, [{}]", msg, e);
						return;
					}
					
					if (!new File(fileAbsolutePath).exists()){
						Boolean deleteAfterUpload = Boolean.valueOf(params.get("deleteAfterUpload"));
						CSPApiServlet.uploadCspImgToAzure(fileAbsolutePath, contentType, containerName, blobName, deleteAfterUpload);
					} else {
						if (sameDiskAccrossNodes){//all nodes share same disk but not found, maybe system error, try again late
							throw new FileNotFoundException("file not found on mounted path ["+ROOTDIR_IMG+"] at ["+LOCAL_IP+"], try again late for ["+msg.getPayload()+"]");
						}
						
						//nodes do not share disk, try it on the origin node
						String origin = params.get("origin");
						if (origin==null||(origin=origin.trim()).isEmpty()){
							throw new FileNotFoundException("file not found at ["+LOCAL_IP+"], try again late for ["+msg.getPayload()+"]");
						}
						
						final String url = "http://"+origin+":18080/KBM/api/csp/__clusterNodeCall512_upld_file_to_azure_";//TODO configure the port and path
						final String body = JSON.toJSONString(params);
						Map<String, String> headers = new HashMap<String, String>();
						headers.put("kbcts", ""+new Date().getTime());
						headers.put("kbck", SIGN_KEY);
						String forSign = SIGN_KEY+"\t"+SIGN_SEC+"\t"+headers.get("kbcts")+"\t/csp/__clusterNodeCall512_upld_file_to_azure_\t\t"+body;
						headers.put("kbsv", DigestUtils.md5Hex(forSign));
						new BaseHttpClient(){}.callHttp(BaseHttpClient.HttpMethod.POST, url, headers, body, ContentType.APPLICATION_JSON, new ResultHandler(){
							@Override
							public void handle(String resultFromHttp, HttpResponse rawResp) {
								JSONObject jo = JSONObject.parseObject(resultFromHttp);
								if (jo!=null && "0".equals(jo.getString("errCode"))){
									if (log.isTraceEnabled()) {//TODO why log.isTraceEnabled() return false always???
										log.trace("successfully upload file to azure thru [{}] for [{}]", url, body);
									}
								} else {
									throw new ApiException("failed to upload file to azure").setContext("thru ["+url+"], reason ["+resultFromHttp+"], msg ["+body+"]");
								}
							}
						});
					}
					
				} catch(Exception e){
					throw new ConsumeException(e);
				}
			}
		});
	}
	
}
