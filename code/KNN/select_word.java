package knn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class select_word {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        
    
       
        
        /* 读取数据 */
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0])), "UTF-8"));
            String lineTxt = null;
            int get_line=0;
            while (((lineTxt = br.readLine()) != null)&&(get_line<1000)) {
            	
                FileWriter fw;
        		try {
        			fw = new FileWriter(args[1], true);
        			BufferedWriter bw = new BufferedWriter(fw);
        			bw.write(lineTxt+"\n");
        			bw.close();
                    fw.close();
        		} catch (IOException e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
                
            	
            	
            	
            	
            	System.out.println(lineTxt);
            	get_line++;
            }
            br.close();
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }
    }
}
