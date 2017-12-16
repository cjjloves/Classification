package knn;

import java.io.IOException;
import java.util.List;

import knn.DocFre.IntSumReducer;
import knn.DocFre.TokenizerMapper;

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

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

public class Word_GR {
	public static class TokenizerMapper //瀹氫箟Map绫诲疄鐜板瓧绗︿覆鍒嗚В
	extends Mapper<Object, Text, Text, DoubleWritable>
	{
	private DoubleWritable word_GR = new DoubleWritable();
	private Text word = new Text();
	public void map(Object key, Text value, Context context)
	throws IOException, InterruptedException
	{ 
		String sentence = value.toString();
		//System.out.println(sentence);
		String[] gettitle = sentence.split("\t");
		int num = gettitle.length;
		
		double positive_have=0;
		double positive_nohave=517;
		double neutral_have=0;
		double neutral_nohave=512;
		double negative_have=0;
		double negative_nohave=470;
		
		//the word only appears in one class
		if(num==2)
		{
			String[] get_class = gettitle[1].split("#");
			
			//only in positive class
			if(get_class[0].equals("positive"))
			{
				positive_have=Double.parseDouble(get_class[1]);
				positive_nohave=positive_nohave-positive_have;
			}
			
			//only in neutral class
			if(get_class[0].equals("neutral"))
			{
				neutral_have=Double.parseDouble(get_class[1]);
				neutral_nohave=neutral_nohave-neutral_have;
			}
			
			//only in negative class
			if(get_class[0].equals("negative"))
			{
				negative_have=Double.parseDouble(get_class[1]);
				negative_nohave=negative_nohave-negative_have;
			}
		}
		
		//the word appears in two class
		if(num==3)
		{
			String[] get_class1 = gettitle[1].split("#");
			String[] get_class2 = gettitle[2].split("#");
			
			//in positive and neutral class
			if((get_class1[0].equals("positive"))&&get_class2[0].equals("neutral"))
			{
				positive_have=Double.parseDouble(get_class1[1]);
				positive_nohave=positive_nohave-positive_have;
				neutral_have=Double.parseDouble(get_class2[1]);
				neutral_nohave=neutral_nohave-neutral_have;
			}
			if((get_class2[0].equals("positive"))&&get_class1[0].equals("neutral"))
			{
				positive_have=Double.parseDouble(get_class2[1]);
				positive_nohave=positive_nohave-positive_have;
				neutral_have=Double.parseDouble(get_class1[1]);
				neutral_nohave=neutral_nohave-neutral_have;
			}
			
			//in positive and negative class
			if((get_class1[0].equals("positive"))&&get_class2[0].equals("negative"))
			{
				positive_have=Double.parseDouble(get_class1[1]);
				positive_nohave=positive_nohave-positive_have;
				negative_have=Double.parseDouble(get_class2[1]);
				negative_nohave=negative_nohave-negative_have;
			}
			if((get_class2[0].equals("positive"))&&get_class1[0].equals("negative"))
			{
				positive_have=Double.parseDouble(get_class2[1]);
				positive_nohave=positive_nohave-positive_have;
				negative_have=Double.parseDouble(get_class1[1]);
				negative_nohave=negative_nohave-negative_have;
			}
			
			//in neutral and negative class
			if((get_class1[0].equals("neutral"))&&get_class2[0].equals("negative"))
			{
				neutral_have=Double.parseDouble(get_class1[1]);
				neutral_nohave=neutral_nohave-neutral_have;
				negative_have=Double.parseDouble(get_class2[1]);
				negative_nohave=negative_nohave-negative_have;
			}
			if((get_class2[0].equals("neutral"))&&get_class1[0].equals("negative"))
			{
				neutral_have=Double.parseDouble(get_class2[1]);
				neutral_nohave=neutral_nohave-neutral_have;
				negative_have=Double.parseDouble(get_class1[1]);
				negative_nohave=negative_nohave-negative_have;
			}
		}
		
		//in three classes
		if(num==4)
		{
			String[] get_class1 = gettitle[1].split("#");
			String[] get_class2 = gettitle[2].split("#");
			String[] get_class3 = gettitle[2].split("#");
			
			//positive, neutral and negative
			if((get_class1[0].equals("positive"))&&get_class2[0].equals("neutral")&&get_class3[0].equals("negative"))
			{
				positive_have=Double.parseDouble(get_class1[1]);
				positive_nohave=positive_nohave-positive_have;
				neutral_have=Double.parseDouble(get_class2[1]);
				neutral_nohave=neutral_nohave-neutral_have;
				negative_have=Double.parseDouble(get_class3[1]);
				negative_nohave=negative_nohave-negative_have;
			}
			
			//positive, negative and neutral
			if((get_class1[0].equals("positive"))&&get_class3[0].equals("neutral")&&get_class2[0].equals("negative"))
			{
				positive_have=Double.parseDouble(get_class1[1]);
				positive_nohave=positive_nohave-positive_have;
				neutral_have=Double.parseDouble(get_class3[1]);
				neutral_nohave=neutral_nohave-neutral_have;
				negative_have=Double.parseDouble(get_class2[1]);
				negative_nohave=negative_nohave-negative_have;
			}
			
			//negative, positive and neutral
			if((get_class2[0].equals("positive"))&&get_class3[0].equals("neutral")&&get_class1[0].equals("negative"))
			{
				positive_have=Double.parseDouble(get_class2[1]);
				positive_nohave=positive_nohave-positive_have;
				neutral_have=Double.parseDouble(get_class3[1]);
				neutral_nohave=neutral_nohave-neutral_have;
				negative_have=Double.parseDouble(get_class1[1]);
				negative_nohave=negative_nohave-negative_have;
			}
			
			//negative, neutral and positive
			if((get_class3[0].equals("positive"))&&get_class2[0].equals("neutral")&&get_class1[0].equals("negative"))
			{
				positive_have=Double.parseDouble(get_class3[1]);
				positive_nohave=positive_nohave-positive_have;
				neutral_have=Double.parseDouble(get_class2[1]);
				neutral_nohave=neutral_nohave-neutral_have;
				negative_have=Double.parseDouble(get_class1[1]);
				negative_nohave=negative_nohave-negative_have;
			}
			
			//neutral, negative and positive
			if((get_class3[0].equals("positive"))&&get_class1[0].equals("neutral")&&get_class2[0].equals("negative"))
			{
				positive_have=Double.parseDouble(get_class3[1]);
				positive_nohave=positive_nohave-positive_have;
				neutral_have=Double.parseDouble(get_class1[1]);
				neutral_nohave=neutral_nohave-neutral_have;
				negative_have=Double.parseDouble(get_class2[1]);
				negative_nohave=negative_nohave-negative_have;
			}
			
			//neutral, positive and negative
			if((get_class2[0].equals("positive"))&&get_class1[0].equals("neutral")&&get_class3[0].equals("negative"))
			{
				positive_have=Double.parseDouble(get_class2[1]);
				positive_nohave=positive_nohave-positive_have;
				neutral_have=Double.parseDouble(get_class1[1]);
				neutral_nohave=neutral_nohave-neutral_have;
				negative_have=Double.parseDouble(get_class3[1]);
				negative_nohave=negative_nohave-negative_have;
			}
		}
		double have = positive_have + neutral_have + negative_have;
		double nohave = positive_nohave + neutral_nohave + negative_nohave;
		double allnum = have + nohave;
		if(allnum!=(470+512+517))
			System.out.println(allnum);
		double word_gr=get_i(470.0,512.0,517.0)-((have/allnum)*get_i(positive_have,neutral_have,negative_have)+(nohave/allnum)*get_i(positive_nohave,neutral_nohave,negative_nohave));
		//System.out.println(word_gr);
		word_GR.set(word_gr);
		word.set(gettitle[0]);
		context.write(word,word_GR);
		
	
	}
	private static double get_i(double a,double b,double c ) {
		
		//no 0
		if((a!=0.0)&&(b!=0.0)&&(c!=0.0))
		{	
			double d=a+b+c;
			return -(a/d)*(Math.log(a/d)/Math.log((double)2))-b/d*(Math.log(b/d)/Math.log((double)2))-(c/d)*(Math.log(c/d)/Math.log((double)2));
		}
		else 
			return 0.0;
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
	job.setJarByClass(Word_GR.class);
	job.setMapperClass(TokenizerMapper.class); 
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(DoubleWritable.class);
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
