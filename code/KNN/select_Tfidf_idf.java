package knn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class select_Tfidf_idf {
	    public static void main(String[] args) {
	    	
	    	//get the top 1000 Information Gain words
	    	String[] top_ig_words = new String[1000];  
	        Map<String, Integer> map = new HashMap<String, Integer>();
	        /* read the data */
	        try {
	            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0])), "UTF-8"));
	            String lineTxt = null;
	            int get_line=0;
	            while (((lineTxt = br.readLine()) != null)&&(get_line<1000)) {
	            	
	            	String[] getword = lineTxt.toString().split("\t");
	            	top_ig_words[get_line] = new String(getword[1]);
	            	get_line++;
	            }
	            br.close();
	        } catch (Exception e) {
	            System.err.println("read errors :" + e);
	        }
	        
	        
	        
	    }
}
