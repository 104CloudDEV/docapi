package com.e104.util;

import java.util.List;

import scala.reflect.internal.Trees.This;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.e104.Errorhandling.DocApplicationException;

public class S3Service {
	public AmazonS3 s3Client(){
		return new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
	}

	/**
	 * This method first deletes all the files in given folder and than the
	 * folder itself
	 * @throws DocApplicationException
	 */
	public int deleteFolder(String bucketName, String folderName) throws DocApplicationException {
		AmazonS3 client = s3Client();
		DeleteObjectRequest deleteRequest = new DeleteObjectsRequest(bucketName);
		List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<DeleteObjectsRequest.KeyVersion>;
		
		// Note: clifflu: 考慮將下列包在 retry 迴圈內
		try{
			//Note: clifflu: 檢查 folderName，以避免用戶傳入 / 造成 document API 刪除全部檔案
			fileList = client.listObjects(bucketName, folderName).getObjectSummaries();
			for (S3ObjectSummary file : fileList) {
				keys.add(new DeleteObjectsRequest.KeyVersion(file.getKey()));
			}
			client.deleteObjects(deleteRequest.withKeys(keys));
		}catch(Exception e){
			throw new DocApplicationException("S3 delete fail", 15);
		}
		return fileList.size();
	}
}
