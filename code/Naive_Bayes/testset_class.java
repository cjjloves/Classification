import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class testset_class {

	public static class TokenizerMapper 
	extends Mapper<Object, Text, Text, Text>
	{
	private Text word = new Text();
	private Text properties = new Text();
	
	//get positive probabilities
	String[] positive_probabilities = new String[1000];
	
	//get neutral probabilities
	String[] neutral_probabilities = new String[1000];
	
	//get negative probabilities
	String[] negative_probabilities = new String[1000];
	
	//get the TrainingSet information
    protected void setup(Context context) throws IOException,InterruptedException{
    	
    	//get positive probabilities
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/u2/hadoop_installs/hadoop-2.7.4/Bayes/TrainingData_positive_probabilities/TrainingData_positive_probabilities.txt")), "UTF-8"));
            String lineTxt = null;
            int get_line=0;
            while ((lineTxt = br.readLine()) != null) {
            	positive_probabilities[get_line] = new String(lineTxt);
            	get_line++;
            }
            br.close();
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }
        
      //get neutral probabilities
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/u2/hadoop_installs/hadoop-2.7.4/Bayes/TrainingData_neutral_probabilities/TrainingData_neutral_probabilities.txt")), "UTF-8"));
            String lineTxt = null;
            int get_line=0;
            while ((lineTxt = br.readLine()) != null) {
            	neutral_probabilities[get_line] = new String(lineTxt);
            	get_line++;
            }
            br.close();
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }
    	
      //get negative probabilities
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/u2/hadoop_installs/hadoop-2.7.4/Bayes/TrainingData_negative_probabilities/TrainingData_negative_probabilities.txt")), "UTF-8"));
            String lineTxt = null;
            int get_line=0;
            while ((lineTxt = br.readLine()) != null) {
            	negative_probabilities[get_line] = new String(lineTxt);
            	get_line++;
            }
            br.close();
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }
        
    }
    
    	
    
	public void map(Object key, Text value, Context context)
	throws IOException, InterruptedException
	{ 
			String sentence = value.toString();
			String[] get_dimention_value = sentence.split("\t");
			String[] get_value = get_dimention_value[2].split(",");
			
			//p(y_positive)
			double p_positive = 466.0/1371.0;
			
			//p(y_neutral)
			double p_neutral = 458.0/1371.0;
			
			//p(y_negative)
			double p_negative = 447.0/1371.0;
			
			
			for(int i = 0 ; i < 1000 ; i++)
			{
				double dimention_value = Double.parseDouble(get_value[i]);
				
				int positive_site = 0;
				int neutral_site = 0;
				int negative_site = 0;
				
				//traverse positive
				for(int positive1 = 0 ; positive1 < 1000 ; positive1++)
				{
					String[] split_positive = positive_probabilities[positive1].split("\t");
					String[] split_positive1 = split_positive[0].split("_");
					if(Integer.parseInt(split_positive1[1]) == i)
					positive_site = positive1;
				}
				
				//traverse neutral
				for(int neutral1 = 0 ; neutral1 < 1000 ; neutral1++)
				{
					String[] split_neutral = neutral_probabilities[neutral1].split("\t");
					String[] split_neutral1 = split_neutral[0].split("_");
					if(Integer.parseInt(split_neutral1[1]) == i)
					neutral_site = neutral1;
				}
				
				//traverse negative
				for(int negative1 = 0 ; negative1 < 1000 ; negative1++)
				{
					String[] split_negative = negative_probabilities[negative1].split("\t");
					String[] split_negative1 = split_negative[0].split("_");
					if(Integer.parseInt(split_negative1[1]) == i)
					negative_site = negative1;
				}
				
				if(dimention_value==0)
				{
					String[] split_positive2 = positive_probabilities[positive_site].split("\t");
					String[] split_neutral2 = positive_probabilities[neutral_site].split("\t");
					String[] split_negative2 = positive_probabilities[negative_site].split("\t");
					double positive_probabilities = Double.parseDouble(split_positive2[2]);
					p_positive = p_positive*positive_probabilities*2;
					
					double neutral_probabilities = Double.parseDouble(split_neutral2[2]);
					p_neutral = p_neutral*neutral_probabilities*2;
					
					double negative_probabilities = Double.parseDouble(split_negative2[2]);
					p_negative = p_negative*negative_probabilities*2;
					
				}
				else
				{
					String[] split_positive2 = positive_probabilities[positive_site].split("\t");
					String[] split_neutral2 = positive_probabilities[neutral_site].split("\t");
					String[] split_negative2 = positive_probabilities[negative_site].split("\t");
					double positive_probabilities = Double.parseDouble(split_positive2[1]);
					p_positive = p_positive*positive_probabilities*2;
					
					double neutral_probabilities = Double.parseDouble(split_neutral2[1]);
					p_neutral = p_neutral*neutral_probabilities*2;
					
					double negative_probabilities = Double.parseDouble(split_negative2[1]);
					p_negative = p_negative*negative_probabilities*2;
				}
					
			}
			
			int biggest = 0;
			//get the biggest
			if(p_positive <= p_neutral)
			{
				biggest = 1;
			}
			
			if(biggest == 1)
			{
				if(p_neutral <= p_negative)
					biggest = 2;
			}
			
			if(biggest == 0)
			{
				if(p_positive <= p_negative)
					biggest = 2;
			}
			
			if(biggest == 0)
				properties.set("positive");
			else if(biggest == 1)
				properties.set("neutral");
			else
				properties.set("negative");
			
			word.set(get_dimention_value[0] + "\t" + get_dimention_value[1]);
			System.err.println(properties);
			context.write(word, properties);

			
	}
	}

	public static void main(String[] args) throws Exception
	{ 
	Configuration conf = new Configuration();

	String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
	if (otherArgs.length != 2)
	{ System.err.println("Usage: wordcount <in> <out>");
	System.exit(2);
	}
	Job job = new Job(conf, "word count");
	job.setJarByClass(testset_class.class);
	job.setMapperClass(TokenizerMapper.class); 
	job.setOutputKeyClass(Text.class); 
	job.setOutputValueClass(Text.class);
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
	
}
