

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;


public class probabilities_process {


	public static class TokenizerMapper //定义Map类实现字符串分解
	extends Mapper<Object, Text, Text, Text>
	{
		private Text word = new Text();
		private Text two_probabilities = new Text();
	    
	    public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException
		{  
				String sentence = value.toString();
				String[] get_num = sentence.split("\t");
				word.set(get_num[0]);
				
				double have_probabilities = Double.parseDouble(get_num[1]);
				have_probabilities = 0.5 + have_probabilities/447;
				System.out.println(have_probabilities);
				double nohave_probabilities = 1- have_probabilities;
				
				two_probabilities.set(Double.toString(have_probabilities) + "\t" + Double.toString(nohave_probabilities));
				context.write(word, two_probabilities);
	
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
	Job job = Job.getInstance();
	job.setJobName("get_probabilities");
	job.setJarByClass(probabilities_process.class);//设置执行任务的jar
	job.setMapperClass(TokenizerMapper.class); //设置Mapper类
	job.setOutputKeyClass(Text.class); //设置job输出的key
	//设置job输出的value
	job.setOutputValueClass(Text.class);
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
	
}
