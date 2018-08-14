package com.yum.kfc.brand.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.ErrorCodeRuntimeException;
import com.hp.jdf.ssm.util.ProxyHelper;
import com.hp.jdf.ssm.util.StringUtil;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.Constants;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.microsoft.azure.storage.blob.SharedAccessBlobPolicy;
import com.microsoft.azure.storage.core.SharedAccessSignatureHelper;
import com.microsoft.azure.storage.core.StorageCredentialsHelper;
import com.microsoft.azure.storage.core.UriQueryBuilder;
import com.microsoft.azure.storage.core.Utility;
import com.yum.kfc.brand.ErrCode;

/**
 * 
 * <b>please use create and upload only, the download and delete has problem now</b><br>
 * 
 * http://www.windowsazure.cn/zh-cn/develop/java/<br>
 * https://azure.microsoft.com/en-us/documentation/articles/storage-use-azcopy/<br>
 * https://msdn.microsoft.com/en-us/library/azure/dd135715.aspx<br>
 * https://azure.microsoft.com/en-us/documentation/articles/storage-java-how-to-use-blob-storage/<br>
 * https://azure.microsoft.com/en-us/documentation/articles/storage-scalability-targets/<br>
 * https://msdn.microsoft.com/library/azure/gg433040.aspx<br>
 * http://azurestorageexplorer.codeplex.com/releases<br>
 * https://github.com/Azure/azure-storage-java<br>
 * https://social.msdn.microsoft.com/forums/azure/en-US/home?forum=windowsazuredata<br>
 * 
 *  @author yidequan@cloudwalk.cn 2018年5月24日下午5:54:07
 *
 */
public class CspAzureClient {
	
	private static final Logger log = LoggerFactory.getLogger(CspAzureClient.class);

	//CSP AZURE ACCOUNT
	private static final String ACCOUNT_NAME = ApplicationConfig.getProperty("ws.azure.storage.csp.account.name",
			"csp");
	private static final String BLOB_ENDPOINT = ApplicationConfig.getProperty("ws.azure.storage.csp.blob.endpoint",
			"http://csp.blob.core.chinacloudapi.cn/");
	private static final String ACCOUNT_KEY = ApplicationConfig.getProperty("ws.azure.storage.csp.account.key",
			"h6coOloE97xp0I6KxPrnj8B5tkJWx9biaxfr04M62OmoR9OLWgkb/AJXVZzI4hNxGn3Af/daFY3o5a//HZ8rdQ==");
	private static final StorageCredentialsAccountAndKey creds = new StorageCredentialsAccountAndKey(ACCOUNT_NAME, ACCOUNT_KEY);
	
	public static final long SAS_TTL_MS = 60L*1000*Integer.parseInt(ApplicationConfig.getProperty("ws.azure.storage.blob.sas.ttl.minutes.csp", "1440"));
	
	private static final String storageConnectionString = "DefaultEndpointsProtocol=http;"
														+ "BlobEndpoint="+BLOB_ENDPOINT+";"
														+ "AccountName="+ACCOUNT_NAME+";"
														+ "AccountKey="+ACCOUNT_KEY;
	
	private static Object lckBcc = new Object();
	private static volatile long lastBCCreateTime=-1;
	private static CloudBlobClient blobClient;

	static {
		ProxyHelper.setProxy("csp azure client");
		try{
			lastBCCreateTime = new Date().getTime();
			blobClient = createBlobClient();
		}catch(Throwable t){
			log.error("failed to create blob client [{}]", StringUtil.getMsgOrClzName(t, true));
			if (log.isTraceEnabled()) log.trace("failed to create blob client [{}]", StringUtil.getMsgOrClzName(t, true), t);
		}
	}
	
	private static CloudBlobClient getBlobClient(){
		if (blobClient==null){
			synchronized (lckBcc){
				if (blobClient==null){
					long now = new Date().getTime();
					if (lastBCCreateTime==-1 || now-lastBCCreateTime>=5*60*1000L){
						lastBCCreateTime = new Date().getTime();
						blobClient = createBlobClient();
					}
				}
			}
		}
		return blobClient;
	}
	
	public static interface ContainerHandler{
		void handle(CloudBlobContainer cloudBlobContainer);
	}
	
	public static interface BlobItemHandler{
		void handle(ListBlobItem blobItem, String blobName);
	}
	
	private static CloudBlobClient createBlobClient() throws IllegalArgumentException{
		CloudStorageAccount storageAccount;
		
		try {
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
		} catch (Exception e) {
			throw new IllegalArgumentException("failed to create azure client. "+e.getMessage(), e);
		}
		
		return storageAccount.createCloudBlobClient();
	}
	
	public static String getBlobEndPoint(){
		return BLOB_ENDPOINT;
	}

	public static void createBlobContainer(String containerName) throws IllegalArgumentException, IOException {
		
		checkContainerName(containerName);
		
		try {
			CloudBlobContainer container = getBlobClient().getContainerReference(containerName);
			container.createIfNotExists();
			BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
			containerPermissions.setPublicAccess(BlobContainerPublicAccessType.BLOB);
			container.uploadPermissions(containerPermissions);
		} catch (Throwable e) {
			throw new IOException("call Azure service failed: " + StringUtil.getMsgOrClzName(e), e);
		}
	}

	public static void uploadBlob(File localFile, String contentType, String containerName, String blobName, boolean autoCreateContainer) throws IllegalArgumentException, IOException {
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(localFile);
			uploadBlob(fis, contentType, containerName, blobName, autoCreateContainer);
		}finally{
			if (fis!=null){
				try{
					fis.close();
				}catch(Exception e){
					log.warn("uploadBlob failed to close input stream", e);
				}
			}
		}
	}

	public static void uploadBlob(InputStream srcStream, String contentType, String containerName, String blobName, boolean autoCreateContainer) throws IllegalArgumentException, IOException {

		checkContainerName(containerName);

		checkBlobName(blobName);

		try {
			CloudBlobContainer container = getBlobClient().getContainerReference(containerName);
			
			if (!container.exists() && autoCreateContainer) {
				createBlobContainer(containerName);
			}
			
			CloudBlockBlob blob = container.getBlockBlobReference(blobName);
			if (contentType!=null && !contentType.trim().isEmpty()){
				blob.getProperties().setContentType(contentType.trim());
			}
			blob.upload(srcStream, -1);// 不确定长度时为-1
		} catch (Exception e) {
			throw new IOException("call Azure service failed: " + StringUtil.getMsgOrClzName(e), e);
		}
	}

	/**
	 * length between 3 and 63, all lower case, start with a letter or number, and can contain only letters, numbers, and the dash (-) character, consecutive dashes are not permitted <br>
	 * https://msdn.microsoft.com/en-us/library/azure/dd135715.aspx
	 * 
	 * @param containerName
	 * @return
	 * @throws IOException
	 */
	public static void checkContainerName(String containerName) throws IllegalArgumentException {
		if (containerName == null || containerName.trim().isEmpty()) {
			throw new IllegalArgumentException("Container name can not be null");
		}
		if (containerName.length() < 3 || containerName.length() > 63) {
			throw new IllegalArgumentException("Container names must be from 3 through 63 characters long");
		}
		Pattern pattern = Pattern.compile("^[a-z0-9]+(-?[a-z0-9]+)+$");
		Matcher matcher = pattern.matcher(containerName);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(
					"A container name must be all lowercase, start with a letter or number, and can contain only letters, numbers, and the dash (-) character, consecutive dashes are not permitted.");
		}
	}

	/**
	 * length between 1 and 1024, segments not exceed 254<br>
	 * https://msdn.microsoft.com/en-us/library/azure/dd135715.aspx
	 * 
	 * @param blobName
	 * @return
	 * @throws IOException
	 */
	public static void checkBlobName(String blobName) throws IllegalArgumentException {
		if (blobName == null || blobName.trim().isEmpty()) {
			throw new IllegalArgumentException("Blob name can not be null");
		}

		if (blobName.length() < 1 || blobName.length() > 1024) {
			throw new IllegalArgumentException("A blob name must be at least one character long and cannot be more than 1,024 characters long");
		}

		String[] strArr = blobName.split("/");
		if (strArr.length > 254) {
			throw new IllegalArgumentException("The number of path segments comprising the blob name cannot exceed 254");
		}
	}
	
	public static void downloadBlob(String containerName, String blobName, OutputStream destStream) throws IOException {
		try {
			CloudBlobContainer container = getBlobClient().getContainerReference(containerName);
			CloudBlockBlob blob = container.getBlockBlobReference(blobName);
			blob.download(destStream);
		} catch (Exception e) {
			throw new IOException("call Azure service failed: " + StringUtil.getMsgOrClzName(e) + ":"+containerName+":"+blobName+":", e);
		}
	}
	
	public static List<String> listContainer() throws IOException {
		return listContainer(null);
	}
	
	public static List<String> listContainer(ContainerHandler ch) throws IOException {
		List<String> containerList = new ArrayList<String>();
		try {
			for (CloudBlobContainer cc: getBlobClient().listContainers()){
				containerList.add(cc.getName());
				if (ch!=null){
					ch.handle(cc);
				}
			}
		} catch (Exception e) {
			throw new IOException("call Azure service failed: " + StringUtil.getMsgOrClzName(e), e);
		}
		return containerList;
	}
	
	public static List<String> listBlob(String containerName) throws IOException {
		return listBlob(containerName, null);
	}
	
	public static List<String> listBlob(String containerName, BlobItemHandler blobItemHandler) throws IOException {
		List<String> blobList = new ArrayList<String>();
		try {
			CloudBlobContainer container = getBlobClient().getContainerReference(containerName);
			Iterable<ListBlobItem> lbis = container.listBlobs(null, true);
			String containerEndpoint = BLOB_ENDPOINT+containerName;
			int containerEndpointLen = containerEndpoint.length();
			for (ListBlobItem blobItem : lbis) {
				String uri = blobItem.getUri().toString();
				String bname = uri.substring(containerEndpointLen);
				blobList.add(bname);
				if (blobItemHandler!=null) blobItemHandler.handle(blobItem, bname);
			}
		} catch (Exception e) {
			throw new IOException("call Azure service failed: " + StringUtil.getMsgOrClzName(e), e);
		}
		return blobList;
	}

	public static void deleteBlob(String containerName, String blobName) throws IOException {
		try {
			CloudBlobContainer container = getBlobClient().getContainerReference(containerName);
			CloudBlockBlob blob = container.getBlockBlobReference(blobName);
			blob.deleteIfExists();
		} catch (Exception e) {
			throw new IOException("Call Azure service failed: " + StringUtil.getMsgOrClzName(e), e);
		}
	}

	public static void deleteContainer(String containerName) throws IOException {
		try {
			CloudBlobContainer container = getBlobClient().getContainerReference(containerName);
			container.deleteIfExists();
		} catch (Exception e) {
			throw new IOException("call Azure service failed: " + StringUtil.getMsgOrClzName(e), e);
		}
	}
	
	public static String createSAS4BlobContainer(String blobContainerName) {
		//https://docs.microsoft.com/zh-cn/rest/api/storageservices/Constructing-a-Service-SAS?redirectedfrom=MSDN
		try{
			Date st = new Date(System.currentTimeMillis()-5L*60*1000);
			Date se = new Date(System.currentTimeMillis()+SAS_TTL_MS+5L*60*1000);
			final String resourceName = String.format("/blob/%s/%s", ACCOUNT_NAME, blobContainerName);
			
			//com.microsoft.azure.storage.blob.CloudBlobContainer.generateSharedAccessSignature(SharedAccessBlobPolicy, String)
			SharedAccessBlobPolicy policy = new SharedAccessBlobPolicy();
			policy.setSharedAccessStartTime(st);
			policy.setSharedAccessExpiryTime(se);
			policy.setPermissionsFromString("r");
			
			//signedpermissions + "\n" +  signedstart + "\n" + signedexpiry + "\n" + canonicalizedresource + "\n" + signedidentifier + "\n" +  signedIP + "\n" +  signedProtocol + "\n" +  signedversion + "\n"
			//+  rscc + "\n" + rscd + "\n" +  rsce + "\n" + rscl + "\n" + rsct
			String stringToSign = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s",
								                "r",
								                Utility.getUTCTimeOrEmpty(st),
								                Utility.getUTCTimeOrEmpty(se),
								                resourceName,
								                Constants.EMPTY_STRING,
								                Constants.EMPTY_STRING,
								                Constants.EMPTY_STRING,
								                Constants.HeaderConstants.TARGET_STORAGE_VERSION);
			stringToSign = String.format("%s\n%s\n%s\n%s\n%s\n%s",
										stringToSign,
						                Constants.EMPTY_STRING,
						                Constants.EMPTY_STRING,
						                Constants.EMPTY_STRING,
						                Constants.EMPTY_STRING,
						                Constants.EMPTY_STRING);
			
			stringToSign = Utility.safeDecode(stringToSign);
			
	        String signature = StorageCredentialsHelper.computeHmac256(creds, stringToSign);
	
	        final UriQueryBuilder builder = SharedAccessSignatureHelper.generateSharedAccessSignatureForBlobAndFile(
	        		policy, null, null, "c", null, null, signature);
	
	        String sign = builder.toString();
	        return sign;
		} catch(StorageException e){
			throw new ErrorCodeRuntimeException("Internal Server Error", ""+ErrCode.GENERAL_SERVER_ERROR, e);
		} catch(InvalidKeyException e){
			throw new ErrorCodeRuntimeException("Internal Server Error", ""+ErrCode.GENERAL_SERVER_ERROR, e);
		}
	}

	public static void main(String[] args){
		try {
			List<String> containers = listContainer();
			System.out.println(containers);
			
			//String fp = "d:/tmp/hp.jpg";
			//String contentType = java.net.URLConnection.getFileNameMap().getContentTypeFor(fp);
			//uploadBlob(new File(fp), contentType, "cc1", "/hp/hp"+new java.util.Date().getTime()+".jpg", true);
			//deleteBlob("cc1", "hphp1445241624126.jpg");
			//deleteContainer("cc1");
			
//			FileOutputStream fos = new FileOutputStream("d:\\tmp\\123.jpg.aaa.jpg");
//			downloadBlob("test-container", "123.jpg", fos);
//			fos.close();
//			FileOutputStream fos2 = new FileOutputStream("d:\\tmp\\fis.jpg.aaa.jpg");
//			downloadBlob("test-container", "fis.jpg", fos2);
//			fos2.close();

			for(final String c: containers){
				List<String> blobs = listBlob(c, new BlobItemHandler(){
					@Override
					public void handle(ListBlobItem blobItem, String blobName) {
						try {
//							if (blobItem instanceof CloudBlob) {//If the item is a blob, not a virtual directory.
//								CloudBlob blob = (CloudBlob) blobItem;
//								blob.downloadToFile("d:\\tmp\\"+blobName);
//							}
//							
//							FileOutputStream fos2 = new FileOutputStream("d:\\tmp\\"+blobName+".jpg");
//							downloadBlob(blobItem.getContainer().getName(), blobName, fos2);
//							fos2.close();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.err.println(":"+c+":"+blobName+":");
							e.printStackTrace();
						}
					}
				});
				System.out.println(blobs);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
