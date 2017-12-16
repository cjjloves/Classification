package knn;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class merge_test_title {
	public static class TokenizerMapper //定义Map类实现字符串分解
	extends Mapper<Object, Text, Text, Text>
	{
	private Text word = new Text();
	private Text merge_title = new Text();
	//实现map()函数
	public void map(Object key, Text value, Context context)
	//public void map()
	throws IOException, InterruptedException
	{ //将字符串拆解成单词
	String[] gettitle = value.toString().split("\t");
	gettitle[4] = gettitle[4].replaceAll("[^\u4e00-\u9fa5]", "");
	merge_title.set(gettitle[4]);
	word.set(gettitle[0]+"\t"+gettitle[1]);
	context.write(word,merge_title);  
	
	}
	}
	
	//定义Reduce类规约同一key的value
	public static class IntSumReducer extends Reducer<Text,Text,Text,Text>
	{
	private Text result = new Text();
	//实现reduce()函数
	public void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context)
	throws IOException, InterruptedException
	{
		String fileList = new String();
		for (Text value : values) {
			fileList += value.toString() + "。";
		}
		fileList = fileList.substring(0,fileList.length()-1);
		result.set(fileList);
		context.write(key, result);	
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
	job.setJobName("url");
	job.setJarByClass(merge_test_title.class);//设置执行任务的jar
	job.setMapperClass(TokenizerMapper.class); 
	job.setCombinerClass(IntSumReducer.class); //设置Combine类
	job.setReducerClass(IntSumReducer.class); //设置Reducer类
	job.setOutputKeyClass(Text.class); //设置job输出的key
	//设置job输出的value
	job.setOutputValueClass(Text.class);
	//设置输入文件的路径
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
