package knn;

import java.io.IOException;

import knn.Tfidf_apearsum.IntSumReducer;
import knn.Tfidf_apearsum.TokenizerMapper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Tfidf_idf {
	public static class TokenizerMapper
	extends Mapper<Object, Text, Text, DoubleWritable>
	{
	private Text word = new Text();
	private final static DoubleWritable one = new DoubleWritable();
	public void map(Object key, Text value, Context context)
	throws IOException, InterruptedException
	{ 
			String sentence = value.toString();
			//System.out.println(sentence);
			String[] gettitle = sentence.split("\t");
		    word.set(gettitle[0].toString());
		    double appear_sum = Double.parseDouble(gettitle[1]);
		    double idf = Math.log((1499.0/appear_sum)+0.01);
		    if(gettitle[0].toString().equals("一一"))
		    {	
		    System.out.println(appear_sum);
		    System.out.println((1499.0/appear_sum)+0.01);
		    System.out.println(idf);}
		    
		    one.set(idf);
			context.write(word,one);
					
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
	job.setJarByClass(DocFre.class);
	job.setMapperClass(TokenizerMapper.class); 
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(DoubleWritable.class);
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
