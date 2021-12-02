package emse.task2_a;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;


public class D_SendMessages {

  public static void main(String[] args) {
	  
	  //initializing queue name
      String queueName = "queue1";
      
      //initializing message content
      String message = "bucket name: " + "bucket.emse.cloud.project\n" + "file name: " + "values.csv";
      
      //initializing sqs client & region
      SqsClient sqsClient = SqsClient.builder()
              .region(Region.US_EAST_1)
              .build();
      		  sendMessage(sqsClient, queueName, message);
      		  sqsClient.close();
      
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
          
          //get queue url
          String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
          
          // send message request
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
