package emse.task2_b;



import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import java.util.List;

public class B_Delete_Messages_and_Queue{


  public static void main(String[] args) {
	  
	  //initializing queue name
      String queueName = "queue1";
      
	  //queue url
      String Url="https://sqs.us-east-1.amazonaws.com/915818437072/queue1";
      
      //initializing sqs client & region
      SqsClient sqsClient = SqsClient.builder()
              .region(Region.US_EAST_1)
              .build();
      
      //initializing list of messages
      List<Message> messages = receiveMessages(sqsClient, Url);
      
      //delete messages
      deleteMessages(sqsClient, Url,  messages) ;
      
      //delete queue
      deleteSQSQueue(sqsClient, queueName); 
      sqsClient.close();
  }

  public static void deleteMessages(SqsClient sqsClient, String queueUrl,  List<Message> messages) {
      System.out.println("\nAll messages has been deleted");

      try {
          for (Message message : messages) {
              DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                  .queueUrl(queueUrl)
                  .receiptHandle(message.receiptHandle())
                  .build();
              sqsClient.deleteMessage(deleteMessageRequest);
          }


      } catch (SqsException e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
 }
  
  public static void deleteSQSQueue(SqsClient sqsClient, String queueName) {

      try {

          GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                  .queueName(queueName)
                  .build();

          String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
          
          System.out.println("\nQueue has been deleted");
          DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder()
                  .queueUrl(queueUrl)
                  .build();

          sqsClient.deleteQueue(deleteQueueRequest);

      } catch (SqsException e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
  }
  
  public static  List<Message> receiveMessages(SqsClient sqsClient, String queueUrl) {


      try {
          ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
              .queueUrl(queueUrl)
              .maxNumberOfMessages(5)
              .build();
          List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
          return messages;
      } catch (SqsException e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
      return null;

  } 

 
}
