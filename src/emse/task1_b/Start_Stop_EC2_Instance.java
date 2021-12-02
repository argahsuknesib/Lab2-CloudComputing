package emse.task1_b;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceStateName;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.model.Tag;


public class Start_Stop_EC2_Instance {
	
	  public static void main(String[] args) {
		  
		  //instance name
	      String name = "i-098df66d8d1a94e29";
	      
	      //instance ami ID
	      String amiId = "ami-04902260ca3d33422" ;  
	      
	      //region
	      Region region = Region.US_EAST_1;
	      
	      //EC2 client
	      Ec2Client ec2 = Ec2Client.builder()
	              .region(region)
	              .build();
	      
	      //instance ID
	      String instanceId = createEC2Instance(ec2,name, amiId) ;
	      
	      //instance state
	      String state = state_EC2_Instances(ec2,instanceId);
	                

          
	      /*
	      
          if(state=="running") {
          	  System.out.println("\nInstance state: "+  state+ " ==> ") ;
          	  stopInstance(ec2, instanceId);
          }
           */
          if(state=="stopped") {
          	  System.out.println("\nInstance state: "+  state+ " ==> ") ;
          	  startInstance(ec2, instanceId);
          }
         
          
	  }
	  
   	  public static void startInstance(Ec2Client ec2, String instanceId) {
	
		    StartInstancesRequest request = StartInstancesRequest.builder()
		            .instanceIds(instanceId)
		            .build();
		
		    ec2.startInstances(request);
		    System.out.printf("Successfully started instance %s\n", instanceId);
	  }
	
	
	  public static void stopInstance(Ec2Client ec2, String instanceId) {
	
		    StopInstancesRequest request = StopInstancesRequest.builder()
		            .instanceIds(instanceId)
		            .build();
		
		    ec2.stopInstances(request);
		    System.out.printf("Successfully stopped instance %s\n", instanceId);
	  }
	
	
	  public static String state_EC2_Instances( Ec2Client ec2, String instanceId){
	
		    boolean done = false;
		    String nextToken = null;
		    InstanceStateName state1;
		    String state="";
		
		    try {
		
		        do {
		            DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(6).nextToken(nextToken).build();
		            DescribeInstancesResponse response = ec2.describeInstances(request);
		            
		            for (Reservation reservation : response.reservations()) {
		                for (Instance instance : reservation.instances()) {
		                	 
		                   
		                    state = instance.state().name().toString();
		                    
       	                    /*
		                    System.out.println("Instance Id is " + instance.instanceId());
		                    System.out.println("Image id is "+  instance.imageId());
		                    System.out.println("Instance type is "+  instance.instanceType());
		                    System.out.println("monitoring information is "+  instance.monitoring().state());
		                    */
		            }
		               
	                   		                		                
		        }
		            nextToken = response.nextToken();
		        } while (nextToken != null);
		
		    } catch (Ec2Exception e) {
		        System.err.println(e.awsErrorDetails().errorMessage());
		        System.exit(1);
		    }
		    
		    return(state);
	  }
	
	  public static String createEC2Instance(Ec2Client ec2,String name, String amiId ) {

		    RunInstancesRequest runRequest = RunInstancesRequest.builder()
		            .imageId(amiId)
		            .instanceType(InstanceType.T1_MICRO)
		            .maxCount(1)
		            .minCount(1)
		            .build();
		
		    RunInstancesResponse response = ec2.runInstances(runRequest);
		    String instanceId = response.instances().get(0).instanceId();
		
		    Tag tag = Tag.builder()
		            .key("Name")
		            .value(name)
		            .build();
		
		    CreateTagsRequest tagRequest = CreateTagsRequest.builder()
		            .resources(instanceId)
		            .tags(tag)
		            .build();
		
		    try {
		        ec2.createTags(tagRequest);
		        System.out.printf(
		        		"EC2 Instance has been created successfully. \nInstance ID: %s  \nami ID:      %s\n\n",
		                instanceId, amiId);
		        System.out.printf(
		        		"Please wait a little bit...\n\n");
		
		      return instanceId;
		
		    } catch (Ec2Exception e) {
		        System.err.println(e.awsErrorDetails().errorMessage());
		        System.exit(1);
		    }
		
		    return "";
		}	

}

