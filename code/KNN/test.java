package knn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class test {
    public static void main(String[] args) {
    	
    	int[] state_num=new int[3];
    	String[] top_ig_words = new String[1000];  
        Map<String, Integer> map = new HashMap<String, Integer>();
        
        for(int i = 0 ; i<3 ; i++)
		{
			state_num[i] = 0;
		}
        
        /* read the data */
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0])), "UTF-8"));
            String lineTxt = null;
            int get_line=0;
            while ((lineTxt = br.readLine()) != null) {
            	
            	String[] getword = lineTxt.toString().split("\t");

            	if(getword[2].equals("positive"))
					state_num[0]++;
				
				//neutral
				if(getword[2].equals("neutral"))
					state_num[1]++;
				
				//negative
				if(getword[2].equals("negative"))
					state_num[2]++;
            	
            	
            }
            br.close();
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }
        
        System.out.println(state_num[0]);
        System.out.println(state_num[1]);
        System.out.println(state_num[2]);
        
    }
}
