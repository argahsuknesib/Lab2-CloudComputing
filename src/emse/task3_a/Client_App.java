package emse.task3_a;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

public class Client_App {
    public static void main(String[] args) throws URISyntaxException {
        String bucketName = "bucket.architecture";
        System.out.format("bucket name: %s \n", bucketName);

        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder().region(region).build();
        // createBucket(s3, bucketName);

        String objectKey = "sales.csv";
        String objectPath = "sales.csv";

        System.out.println("Putting object " + objectKey + " into bucket " + bucketName);
        System.out.println(" in bucket: " + bucketName);

        String result = putS3Object(s3, bucketName, objectKey, objectPath);
        System.out.println("Tag information: " + result);

        String queueNameOne = "Inbox";
        String messageToInbox = "bucket name: " + "bucket.architecture \n " + "file name: " + "sales.csv";
        SqsClient sqsClient = SqsClient.builder().region(Region.US_EAST_1).build();

        sendMessage(sqsClient, queueNameOne, messageToInbox);

        s3.close();
        sqsClient.close();
    
    }

    public static void createBucket( S3Client s3Client, String bucketName) {

        try {
            S3Waiter s3Waiter = s3Client.waiter();
            //create bucket request
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            //head bucket request
            s3Client.createBucket(bucketRequest);
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            // wait until the bucket is created and print out the response.
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println(bucketName +" has been created");

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
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

    public static void sendMessage(SqsClient sqsClient, String queueName, String message) {

        try {
            CreateQueueRequest request = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();
            CreateQueueResponse createResult = sqsClient.createQueue(request);
  
            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();
            
            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
            
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .delaySeconds(5)
                .build();
  
            sqsClient.sendMessage(sendMsgRequest);
  
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }


    
}
