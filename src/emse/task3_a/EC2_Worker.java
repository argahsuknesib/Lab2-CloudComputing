package emse.task3_a;

import java.util.List;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueNameExistsException;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

public class EC2_Worker {
    public static void main(String[] args){
        String queueNameOne = "Inbox";
        String queueNameTwo = "Outbox";

        SqsClient sqsClient = SqsClient.builder().region(Region.US_EAST_1).build();
        // String queueUrlOne = createQueue(sqsClient, queueNameOne);
        // String queueUrlTwo = createQueue(sqsClient, queueNameTwo);
        listQueues(sqsClient);

        String UrlOne = "https://sqs.us-east-1.amazonaws.com/915818437072/Inbox";
        String UrlTwo = "https://sqs.us-east-1.amazonaws.com/915818437072/Outbox";

        try{
            ReceiveMessageRequest receiveRequestOne = ReceiveMessageRequest.builder().queueUrl(UrlOne).build();
            List<Message> messagesOne = sqsClient.receiveMessage(receiveRequestOne).messages();

            ReceiveMessageRequest receiveRequestTwo = ReceiveMessageRequest.builder().queueUrl(UrlTwo).build();
            List<Message> messagesTwo = sqsClient.receiveMessage(receiveRequestTwo).messages();

            for(Message m : messagesOne){
                System.out.println("Inbox");
                System.out.println("\n" + m.body());
            }

            for(Message m : messagesTwo){
                System.out.println("Outbox");
                System.out.println("\n" + m.body());
            }

        deleteMessages(sqsClient, UrlOne, messagesOne);
        deleteMessages(sqsClient, UrlTwo, messagesTwo);

        } catch (QueueNameExistsException e){
            throw e;
        }
        
        // deleteSQSQueue(sqsClient, queueNameOne);
        // deleteSQSQueue(sqsClient, queueNameTwo);
        

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

}
