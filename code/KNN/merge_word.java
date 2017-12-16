package knn;

import java.io.IOException;
import java.util.List;

import knn.DocFre.IntSumReducer;
import knn.DocFre.TokenizerMapper;

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

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

public class merge_word {
	public static class TokenizerMapper //瀹氫箟Map绫诲疄鐜板瓧绗︿覆鍒嗚В
	extends Mapper<Object, Text, Text, Text>
	{
	private Text word = new Text();
	private Text addition = new Text();
	public void map(Object key, Text value, Context context)
	throws IOException, InterruptedException
	{ 
			String sentence = value.toString();
			System.out.println(sentence);
			String[] gettitle = sentence.split("\t");
			FileSplit fileSplit = (FileSplit) context.getInputSplit();
		    String fileName = fileSplit.getPath().getName();
		    word.set(gettitle[0].toString());
		    addition.set(fileName.toString()+"#"+gettitle[1].toString());
			context.write(word,addition);
					
	}
	}  
	public static class IntSumReducer extends Reducer<Text,Text,Text,Text>
	{
		private Text result = new Text();
	//瀹炵幇reduce()鍑芥暟
	public void reduce(Text key, Iterable<Text> values, Context context )
	throws IOException, InterruptedException
	{
	String fileList = new String();
	for (Text value : values) {
		fileList += value.toString() + "\t";
		}
	fileList = fileList.substring(0,fileList.length()-1);
	result.set(fileList);
	context.write(key, result);
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
	job.setJarByClass(DocFre.class);//璁剧疆鎵ц浠诲姟鐨刯ar
	job.setMapperClass(TokenizerMapper.class); //璁剧疆Mapper绫�
	job.setCombinerClass(IntSumReducer.class); //璁剧疆Combine绫�
	job.setReducerClass(IntSumReducer.class); //璁剧疆Reducer绫�
	job.setOutputKeyClass(Text.class); //璁剧疆job杈撳嚭鐨刱ey
	//璁剧疆job杈撳嚭鐨剉alue
	job.setOutputValueClass(Text.class);
	//璁剧疆杈撳叆鏂囦欢鐨勮矾寰�
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	//璁剧疆杈撳嚭鏂囦欢鐨勮矾寰�
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	//鎻愪氦浠诲姟骞剁瓑寰呬换鍔″畬鎴�
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
