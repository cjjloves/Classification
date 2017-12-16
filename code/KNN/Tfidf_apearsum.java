package knn;

import java.io.IOException;

import knn.merge_word.IntSumReducer;
import knn.merge_word.TokenizerMapper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Tfidf_apearsum {
	public static class TokenizerMapper //瀹氫箟Map绫诲疄鐜板瓧绗︿覆鍒嗚В
	extends Mapper<Object, Text, Text, IntWritable>
	{
	private Text word = new Text();
	private final static IntWritable one = new IntWritable();
	public void map(Object key, Text value, Context context)
	throws IOException, InterruptedException
	{ 
			String sentence = value.toString();
			//System.out.println(sentence);
			String[] gettitle = sentence.split("\t");
		    word.set(gettitle[0].toString());
		    one.set(Integer.parseInt(gettitle[1]));
			context.write(word,one);
					
	}
	}  
	public static class IntSumReducer extends Reducer<Text,IntWritable,Text,IntWritable>
	{
	private IntWritable result = new IntWritable();
	//瀹炵幇reduce()鍑芥暟
	public void reduce(Text key, Iterable<IntWritable> values, Context context )
	throws IOException, InterruptedException
	{
		//System.out.println("1");
	int sum = 0;
		//遍历迭代器values，得到同一key的所有value
	for (IntWritable val : values) { sum += val.get(); }
	result.set(sum);
	context.write(key, result);
	}
	}
	
	public static void main(String[] args) throws Exception
	{ 
	Configuration conf = new Configuration();
	//鍛戒护琛屽弬鏁�
	String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
	if (otherArgs.length != 2)
	{ System.err.println("Usage: wordcount <in> <out>");
	System.exit(2);
	}
	Job job = new Job(conf, "word count"); 
	job.setJarByClass(DocFre.class);
	job.setMapperClass(TokenizerMapper.class);
	job.setReducerClass(IntSumReducer.class); 
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(IntWritable.class);
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
