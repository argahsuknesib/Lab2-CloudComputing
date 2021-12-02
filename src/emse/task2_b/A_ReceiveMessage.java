package emse.task2_b;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueNameExistsException;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import java.util.List;


public class A_ReceiveMessage {
	
	
  public static void main(String[] args) {
	  
	  //queue url
      String Url="https://sqs.us-east-1.amazonaws.com/915818437072/queue1";
      
      SqsClient sqsClient = SqsClient.builder()
              .region(Region.US_EAST_1)
              .build();

      try {

          // Receive messages from the queue
          ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
              .queueUrl(Url)
              .build();
          List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

          // Print out the messages
           for (Message m : messages) {
              System.out.println("\n" +m.body());
          }
      } catch (QueueNameExistsException e) {
          throw e;
      }
  }
}
