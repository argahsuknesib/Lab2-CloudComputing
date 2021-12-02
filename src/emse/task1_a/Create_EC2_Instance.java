package emse.task1_a;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;


public class Create_EC2_Instance {
	
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
      
      // EC2 instance
      String instanceId = createEC2Instance(ec2,name, amiId) ;
           
  }
  
  
  public static String createEC2Instance(Ec2Client ec2,String name, String amiId ) {
	  	
	    //run instance request
	    RunInstancesRequest runRequest = RunInstancesRequest.builder()
	            .imageId(amiId)
	            .instanceType(InstanceType.T1_MICRO)
	            .maxCount(1)
	            .minCount(1)
	            .build();
	    
	  //run instance response
	    RunInstancesResponse response = ec2.runInstances(runRequest);
	    String instanceId = response.instances().get(0).instanceId();
	
	    Tag tag = Tag.builder()
	            .key("Name")
	            .value(name)
	            .build();
	    
	    //create tag request
	    CreateTagsRequest tagRequest = CreateTagsRequest.builder()
	            .resources(instanceId)
	            .tags(tag)
	            .build();
	
	    try {
	        ec2.createTags(tagRequest);
	        System.out.printf(
	                "EC2 Instance has been created successfully. \nInstance ID: %s  \nami ID:      %s",
	                instanceId, amiId);
	
	      return instanceId;
	
	    } catch (Ec2Exception e) {
	        System.err.println(e.awsErrorDetails().errorMessage());
	        System.exit(1);
	    }
	
	    return "";
	}
  	
}

