package knn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import knn.get_nearest_k.TokenizerMapper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class get_result {
	public static class TokenizerMapper 
	extends Mapper<Object, Text, Text, Text>
	{
	private Text word = new Text();
	private Text properties = new Text();
	
	//signal the site
	private int[] state_num=new int[3];

    
	public void map(Object key, Text value, Context context)
	throws IOException, InterruptedException
	{ 
			
			//origanite the distance and site
			for(int i = 0 ; i<3 ; i++)
			{
				state_num[i] = 0;
			}
			
			String sentence = value.toString();
			String[] gettitle = sentence.split("\t");
			
			String[] get_state = gettitle[2].split(",");
			for(int i = 0 ; i < 20 ; i++)
			{
				//positive
				if(get_state[i].equals("positive"))
					state_num[0]++;
				
				//neutral
				if(get_state[i].equals("neutral"))
					state_num[1]++;
				
				//negative
				if(get_state[i].equals("negative"))
					state_num[2]++;
			}
			
			int biggest = 0;
			if(state_num[0] <= state_num[1])
				biggest = 1;
			if(state_num[biggest] <= state_num[2])
				biggest = 2;
			
			word.set(gettitle[0] + "\t" + gettitle[1]);
			//context.write
			//positive
			if(biggest == 0)
			{
				properties.set("positive");
				context.write(word, properties);
			}
			else if(biggest == 1)
			{
				properties.set("neutral");
				context.write(word, properties);
			}
			else
			{
				properties.set("negative");
				context.write(word, properties);
			}
			
	}
	}

	public static void main(String[] args) throws Exception
	{ //涓轰换鍔¤瀹氶厤缃枃浠�
		//TokenizerMapper map1 = new TokenizerMapper();
		//map1.map();
	Configuration conf = new Configuration();
	//鍛戒护琛屽弬鏁�
	String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
	if (otherArgs.length != 2)
	{ System.err.println("Usage: wordcount <in> <out>");
	System.exit(2);
	}
	Job job = new Job(conf, "word count");
	job.setJarByClass(get_nearest_k.class);
	job.setMapperClass(TokenizerMapper.class); 
	job.setOutputKeyClass(Text.class); 
	job.setOutputValueClass(Text.class);
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
