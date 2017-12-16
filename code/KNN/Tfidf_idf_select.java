package knn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

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


public class Tfidf_idf_select {
	public static class TokenizerMapper //定义Map类实现字符串分解
	extends Mapper<Object, Text, Text, Text>
	{
		
    	//get the top 1000 Information Gain words
    	String[] top_ig_words = new String[1000];  
        Map<String, Integer> map = new HashMap<String, Integer>();
        
        /* read the data */
        protected void setup(Context context) throws IOException,InterruptedException{
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/u2/hadoop_installs/hadoop-2.7.4/TrainingData_word_GR_sort/TrainingData_word_GR_sort.txt")), "UTF-8"));
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
		
	private Text word = new Text();
	private Text idf = new Text();
	//实现map()函数
	public void map(Object key, Text value, Context context)
	//public void map()
	throws IOException, InterruptedException
	{ //将字符串拆解成单词
	String line = value.toString();
	for(int i=0;i<1000;i++)
	{
		String[] getword = line.split("\t");
		if(getword[0].equals(top_ig_words[i]))
		{
			word.set(getword[0]);
			idf.set(getword[1]);
			context.write(word, idf);
			break;
		}
		}
	}
	} 
	public static void main(String[] args) throws Exception
	{ //为任务设定配置文件
		//TokenizerMapper map1 = new TokenizerMapper();
		//map1.map();
	Configuration conf = new Configuration();
	//命令行参数
	String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
	if (otherArgs.length != 2)
	{ System.err.println("Usage: wordcount <in> <out>");
	System.exit(2);
	}
	Job job = new Job(conf, "word count"); //新建一个用户定义的Job
	job.setJarByClass(Tfidf_idf_select.class);//设置执行任务的jar
	job.setMapperClass(TokenizerMapper.class); //设置Mapper类
	job.setOutputKeyClass(Text.class); //设置job输出的key
	//设置job输出的value
	job.setOutputValueClass(Text.class);
	//设置输入文件的路径
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	//设置输出文件的路径
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	//提交任务并等待任务完成
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
