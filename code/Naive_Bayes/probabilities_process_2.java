import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class probabilities_process_2 {
	public static void main(String[] args) throws Exception
	{
		int[] get_idf_words_information = new int[1000];
		
		for(int i = 0 ; i < 1000 ; i++)
			get_idf_words_information[i] = 0;
		
	      try {
	            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/u2/hadoop_installs/hadoop-2.7.4/Bayes/TrainingData_positive_probabilities/TrainingData_positive_probabilities.txt")), "UTF-8"));
	            String lineTxt = null;
	            while ((lineTxt = br.readLine()) != null) {
	            	String[] get_num = lineTxt.split("\t");
	            	String[] get_site = get_num[0].split("_");
	            	int get_the_site = Integer.parseInt(get_site[1]);
	            	get_idf_words_information[get_the_site]++;
	            }
	            br.close();
	        } catch (Exception e) {
	            System.err.println("read errors :" + e);
	        }
	      
	      
	      File dest = new File("/home/u2/hadoop_installs/hadoop-2.7.4/Bayes/TrainingData_positive_probabilities/TrainingData_positive_probabilities.txt");  
	      try {  
	          BufferedWriter writer  = new BufferedWriter(new FileWriter(dest,true));  
	          
	          
	          for(int i = 0 ; i < 1000 ; i++){
	        	  if(get_idf_words_information[i]==0)
	        	  {
	        		  String line = "positive_" + i + "\t" + "0.5000000000000000" + "\t" + "0.5000000000000000" + "\n";  
	          
	              writer.write(line);   
	        	  }
	          
	          }
	          writer.flush();    
	          writer.close();  
	      } catch (FileNotFoundException e) {  
	          e.printStackTrace();  
	      } catch (IOException e) {  
	          e.printStackTrace();  
	      }
	      
	      
	      
	}
}
