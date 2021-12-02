package emse.task2_b;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

public class E_DeleteFile_and_Bucket {

    public static void main(String[] args) throws Exception {
    	
    	//bucket name
        String bucket = "bucket.emse.cloud.project";
        
        //region
        Region region = Region.US_EAST_1;
        
        //s3 client
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        listAllObjects(s3,bucket) ;
        s3.close();
    }

    public static void listAllObjects(S3Client s3, String bucket) {
    	
    	//deleting list of objects
        try {

            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucket).build();
            ListObjectsV2Response listObjectsV2Response;

            do {
                listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);
                for (S3Object s3Object : listObjectsV2Response.contents()) {
                    s3.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(s3Object.key())
                            .build());
                }

                listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucket)
                        .continuationToken(listObjectsV2Response.nextContinuationToken())
                        .build();
                
            //deleting bucket    
            } while(listObjectsV2Response.isTruncated());
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucket).build();
            s3.deleteBucket(deleteBucketRequest);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}