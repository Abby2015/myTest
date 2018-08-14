package com.yum.kfc.brand.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.util.ProxyHelper;
import com.hp.jdf.ssm.util.StringUtil;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

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
 * @author Zheng, Wenhai (Heven, ES-Apps-GD-China-WH) <wenhai.zheng@hpe.com> Tuesday, October 13, 2015 3:43 PM
 * @author DING Weimin (wei-min.ding@hp.com) Oct 19, 2015 10:32:49 AM
 *
 */
public class AzureClient {
	
	private static final Logger log = LoggerFactory.getLogger(AzureClient.class);

	private static final String ACCOUNT_NAME = ApplicationConfig.getProperty("ws.azure.storage.account.name",
												"mediasvcq1nsl4gr5263r");
	private static final String BLOB_ENDPOINT = ApplicationConfig.getProperty("ws.azure.storage.blob.endpoint",
												"http://mediasvcq1nsl4gr5263r.blob.core.chinacloudapi.cn/");
	private static final String ACCOUNT_KEY = ApplicationConfig.getProperty("ws.azure.storage.account.key",
												"ljHdBVQJvj6hj0v7WDVYPgWpyfkVmcB3xBA2w6RffPzqZYx3uZ802o5ZC0K0Ykim1WRj0MP7LfPmoe2zzTlfUA==");
	
	private static final String storageConnectionString = "DefaultEndpointsProtocol=http;"
														+ "BlobEndpoint="+BLOB_ENDPOINT+";"
														+ "AccountName="+ACCOUNT_NAME+";"
														+ "AccountKey="+ACCOUNT_KEY;
	
	private static CloudBlobClient blobClient;

	static {
		ProxyHelper.setProxy("azure client");
		blobClient = createBlobClient();
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
			throw new IllegalArgumentException("failed to create azure client", e);
		}
		
		return storageAccount.createCloudBlobClient();
	}
	
	public static String getBlobEndPoint(){
		return BLOB_ENDPOINT;
	}

	public static void createBlobContainer(String containerName) throws IllegalArgumentException, IOException {
		
		checkContainerName(containerName);
		
		try {
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
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
			log.info("==============================uploadBlob finished");
		}
	}

	public static void uploadBlob(InputStream srcStream, String contentType, String containerName, String blobName, boolean autoCreateContainer) throws IllegalArgumentException, IOException {

		checkContainerName(containerName);

		checkBlobName(blobName);

		try {
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
			
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
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
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
			for (CloudBlobContainer cc: blobClient.listContainers()){
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
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
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
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
			CloudBlockBlob blob = container.getBlockBlobReference(blobName);
			blob.deleteIfExists();
		} catch (Exception e) {
			throw new IOException("Call Azure service failed: " + StringUtil.getMsgOrClzName(e), e);
		}
	}

	public static void deleteContainer(String containerName) throws IOException {
		try {
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
			container.deleteIfExists();
		} catch (Exception e) {
			throw new IOException("call Azure service failed: " + StringUtil.getMsgOrClzName(e), e);
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
