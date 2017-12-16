package knn;

import java.io.IOException;

import knn.merge_word.IntSumReducer;
import knn.merge_word.TokenizerMapper;

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
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Sort_word {

	public static class TokenizerMapper
	extends Mapper<Object, Text, DoubleWritable, Text>
	{
	private Text word = new Text();
	private DoubleWritable word_GR = new DoubleWritable();
	public void map(Object key, Text value, Context context)
	throws IOException, InterruptedException
	{ 
			String sentence = value.toString();
			String[] gettitle = sentence.split("\t");
			double word_gr=Double.parseDouble(gettitle[1]);
			if(word_gr!=0.0)
			{	
				word.set(gettitle[0].toString());
				word_GR.set(word_gr);
				context.write(word_GR,word);
			}		
	}
	}  
	 private static class IntWritableDecreasingComparator extends IntWritable.Comparator {

	     public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
	                return -super.compare(b1, s1, l1, b2, s2, l2);
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
	Job job = new Job(conf, "word count"); //鏂板缓涓�涓敤鎴峰畾涔夌殑Job
	job.setJarByClass(Sort_word.class);//璁剧疆鎵ц浠诲姟鐨刯ar
	job.setMapperClass(TokenizerMapper.class); //璁剧疆Mapper绫�
	job.setOutputKeyClass( DoubleWritable.class); //璁剧疆job杈撳嚭鐨刱ey
	job.setOutputValueClass(Text.class);
	job.setSortComparatorClass(IntWritableDecreasingComparator.class);
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	//璁剧疆杈撳嚭鏂囦欢鐨勮矾寰�
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	//鎻愪氦浠诲姟骞剁瓑寰呬换鍔″畬鎴�
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
