package emse.task2_b;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.io.BufferedReader;  
import java.io.FileReader;  


public class C_and_D_RetrieveFile_Calculate_Ave_Min_Max {

	 public static void main(String[] args) {
		 
		 //bucket name
	     String bucketName = "bucket.emse.cloud.project.final";
	     
	     //file name
	     String keyName = "values.csv";
	     
	     //path
	     String path = "final-values.txt";
	     
	     //region
	     Region region = Region.US_EAST_1;
	     
	     //s3 client
	     S3Client s3 = S3Client.builder()
	             .region(region)
	             .build();
	     
	     //get object
	     getObjectBytes(s3,bucketName,keyName, path);
	     
	     //read from csv
	     String line = "";  
	     String splitBy = " "; 
	     
	     String[] values = new String[1000]; // Creating a new Array of Size 999
	     int[] intArray = new int[1000];
	     int i = 0;
	     int x =0;
	     int sum=0;
	     int average = 0;
	     
	     //parsing a CSV file into BufferedReader class constructor 
	     try{  
	    	  
		     BufferedReader br = new BufferedReader(new FileReader(path));  
		     while ((line = br.readLine()) != null){
		    	 
		       	 // usinsg space as separator
			     String[] index = line.split(splitBy);      
			     values[i]=index[0];
			     i++;
			 }  
		     
	     }catch (IOException e){  
	    	 e.printStackTrace();  
	     }  

	     //reading all lines
	     for(x=0;x<1000;x++) {
	    	 
	    	 intArray[x] = Integer.parseInt(values[x]);
	    	 sum = sum + Integer.parseInt(values[x]);
	    	
	     } 
	     
	     //###AVERAGE####
	     average = sum/1000;
	     
	     Arrays.sort(intArray);
	     System. out. println("Average = " + average);
	     System. out. println("Minimum = " + intArray[0]);
	     System. out. println("Maximum = " + intArray[intArray. length-1]);
	     s3.close();
	     
	 }
	
	 public static void getObjectBytes (S3Client s3, String bucketName, String keyName, String path ) {
	
	     try {
	         GetObjectRequest objectRequest = GetObjectRequest
	                 .builder()
	                 .key(keyName)
	                 .bucket(bucketName)
	                 .build();
	
	         ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
	         byte[] data = objectBytes.asByteArray();
	
	         // Write the data to a local file
	         File myFile = new File(path );
	         OutputStream os = new FileOutputStream(myFile);
	         os.write(data);
	         System.out.println("Successfully obtained bytes from an S3 object");
	         os.close();
	
	     } catch (IOException ex) {
	         ex.printStackTrace();
	     } catch (S3Exception e) {
	       System.err.println(e.awsErrorDetails().errorMessage());
	        System.exit(1);
	     }
	 
	 }

}