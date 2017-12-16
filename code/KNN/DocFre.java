package knn;

import java.io.IOException;
import java.util.List;
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

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

public class DocFre {
	public static class TokenizerMapper //瀹氫箟Map绫诲疄鐜板瓧绗︿覆鍒嗚В
	extends Mapper<Object, Text, Text, IntWritable>
	{
	private final static IntWritable one = new IntWritable(1);
	private Text word = new Text();
	public void map(Object key, Text value, Context context)
	throws IOException, InterruptedException
	{ 
			byte[] valueBytes = value.getBytes();
			String sentence = new String(valueBytes, "GBK");
			//sentence=sentence.replaceAll(" +","");
			sentence=sentence.replaceAll("[^\u4e00-\u9fa5]", "");
			System.out.println(sentence);
			Segment segment = HanLP.newSegment().enablePartOfSpeechTagging(false); 
			List<Term> segWords = segment.seg(sentence); 
			CoreStopWordDictionary.apply(segWords); 	
			for (int i1 = 0; i1 < segWords.size(); i1++) { 
				int jud=0;
				//System.out.println(segWords.get(i1).toString());
				if(i1==0){
					word.set(segWords.get(i1).toString());
					context.write(word,one);
				}
				else{
					for(int i2=0;i2<i1;i2++){
						if(segWords.get(i1).toString().equals(segWords.get(i2).toString()))
						{
							jud=1;
							break;
						}
					}
					if(jud==0){
						word.set(segWords.get(i1).toString());
						context.write(word,one);
					}
				}
			}  
		//}
	
	}
	}
	
	//瀹氫箟Reduce绫昏绾﹀悓涓�key鐨剉alue
	public static class IntSumReducer extends Reducer<Text,IntWritable,Text,IntWritable>
	{
	private IntWritable result = new IntWritable();
	//瀹炵幇reduce()鍑芥暟
	public void reduce(Text key, Iterable<IntWritable> values, Context context )
	throws IOException, InterruptedException
	{
	int sum = 0;
	//閬嶅巻杩唬鍣╲alues锛屽緱鍒板悓涓�key鐨勬墍鏈塿alue
	for (IntWritable val : values) { sum += val.get(); }
	result.set(sum);
	//浜х敓杈撳嚭瀵�<key, value>
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
	job.setOutputValueClass(IntWritable.class);
	//璁剧疆杈撳叆鏂囦欢鐨勮矾寰�
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	//璁剧疆杈撳嚭鏂囦欢鐨勮矾寰�
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	//鎻愪氦浠诲姟骞剁瓑寰呬换鍔″畬鎴�
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
