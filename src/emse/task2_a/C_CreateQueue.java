package emse.task2_a;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public class C_CreateQueue{


  public static void main(String[] args) {
	  
	  //initializing queue name
      String queueName = "queue1";
      
      //initializing sqs client & region
      SqsClient sqsClient = SqsClient.builder()
              .region(Region.US_EAST_1)
              .build();

      // Perform various tasks on the Amazon SQS queue
      String queueUrl= createQueue(sqsClient, queueName );
      listQueues(sqsClient);
      // listQueuesFilter(sqsClient, queueUrl);
      //List<Message> messages = receiveMessages(sqsClient, queueUrl);
      //sendBatchMessages(sqsClient, queueUrl);
      //changeMessages(sqsClient, queueUrl, messages);
      //deleteMessages(sqsClient, queueUrl,  messages) ;
      sqsClient.close();
  }


  public static String createQueue(SqsClient sqsClient,String queueName ) {

      try {
          System.out.println("\nCreate Queue");

          //create queue request 
          CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
              .queueName(queueName)
              .build();

          sqsClient.createQueue(createQueueRequest);
          
          System.out.println("\nGet queue url");

        //get queue url response
          GetQueueUrlResponse getQueueUrlResponse =
              sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
          String queueUrl = getQueueUrlResponse.queueUrl();
          return queueUrl;

      } catch (SqsException e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
      return "";

  }

  public static void listQueues(SqsClient sqsClient) {
	  
	  //list of queues
      System.out.println("\nList Queues");
      String prefix = "que";

      try {
          ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder().queueNamePrefix(prefix).build();
          ListQueuesResponse listQueuesResponse = sqsClient.listQueues(listQueuesRequest);

          for (String url : listQueuesResponse.queueUrls()) {
              System.out.println(url);
          }

      } catch (SqsException e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
  }

 
}
