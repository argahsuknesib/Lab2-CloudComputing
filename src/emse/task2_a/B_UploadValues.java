package emse.task2_a;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class B_UploadValues {

    public static void main(String[] args) {
        
    	//initializing bucket name
        String bucketName = "bucket.emse.cloud.project.final";
        
        //initializing key
        String objectKey = "values.csv";
        
        //initializing file path
        String objectPath = "values.csv";
        
        System.out.println("Putting object " + objectKey +" into bucket "+bucketName);
        System.out.println("  in bucket: " + bucketName);
        
        //initializing sqs client &  region
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        String result = putS3Object(s3, bucketName, objectKey, objectPath);
        System.out.println("Tag information: "+result);
        s3.close();
    }

    public static String putS3Object(S3Client s3,
                                     String bucketName,
                                     String objectKey,
                                     String objectPath) {

        try {

            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            
            // put object request
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .metadata(metadata)
                    .build();
            
            // put object response
            PutObjectResponse response = s3.putObject(putOb,
                    RequestBody.fromBytes(getObjectFile(objectPath)));

           return response.eTag();

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    
    // Return a byte array
    private static byte[] getObjectFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }
}