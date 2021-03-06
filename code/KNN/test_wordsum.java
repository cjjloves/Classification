package knn;

import java.io.IOException;
import java.util.List;

import knn.txt_wordsum.TokenizerMapper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

public class test_wordsum {
	public static class TokenizerMapper //瀹氫箟Map绫诲疄鐜板瓧绗︿覆鍒嗚В
	extends Mapper<Object, Text, Text, IntWritable>
	{
	private final static IntWritable one = new IntWritable();
	private Text word = new Text();
	public void map(Object key, Text value, Context context)
	throws IOException, InterruptedException
	{ 
			String sentence=value.toString();
			String[] gettitle = value.toString().split("\t");
			if(gettitle.length!=3)
				System.out.println(gettitle[0]);
			sentence=gettitle[2].replaceAll("[^\u4e00-\u9fa5]", "");
			//System.out.println(sentence);
			Segment segment = HanLP.newSegment().enablePartOfSpeechTagging(false); 
			List<Term> segWords = segment.seg(sentence); 
			CoreStopWordDictionary.apply(segWords); 	
			int txt_wordsum = segWords.size();
			
			//get the name of txt
		     FileSplit fileSplit = (FileSplit) context.getInputSplit();
		     word.set(gettitle[0]+"\t"+gettitle[1]);
		     one.set(txt_wordsum);
		     context.write(word, one);
		//}
	
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
	job.setJarByClass(test_wordsum.class);
	job.setMapperClass(TokenizerMapper.class); 
	job.setOutputKeyClass(Text.class); 
	job.setOutputValueClass(IntWritable.class);
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
