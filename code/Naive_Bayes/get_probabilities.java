import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;


public class get_probabilities {

	public static class TokenizerMapper //定义Map类实现字符串分解
	extends Mapper<Object, Text, Text, IntWritable>
	{
		private Text word = new Text();
		private final static IntWritable one = new IntWritable(1);
		
		//judge if word exists
		private int[] judge_word_exists=new int[1000];

		//get the selected word information
		String[] get_idf_words_information = new String[1000];
		
	    protected void setup(Context context) throws IOException,InterruptedException{
	        try {
	            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/u2/hadoop_installs/hadoop-2.7.4/TrainingData_TFIDF_IDF_select/TrainingData_TFIDF_IDF_select.txt")), "UTF-8"));
	            String lineTxt = null;
	            int get_line=0;
	            while ((lineTxt = br.readLine()) != null) {
	            	get_idf_words_information[get_line] = new String(lineTxt);
	            	get_line++;
	            }
	            br.close();
	        } catch (Exception e) {
	            System.err.println("read errors :" + e);
	        }
	    }
	    
	    public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException
		{  
				
				//origanite the distance and site
				for(int i = 0 ; i < 1000 ; i++)
				{
					judge_word_exists[i] = 0;
				}
				
				byte[] valueBytes = value.getBytes();
				String sentence = new String(valueBytes, "GBK");
				//sentence=sentence.replaceAll(" +","");
				sentence=sentence.replaceAll("[^\u4e00-\u9fa5]", "");
				//System.out.println(sentence);
				Segment segment = HanLP.newSegment().enablePartOfSpeechTagging(false); 
				List<Term> segWords = segment.seg(sentence); 
				CoreStopWordDictionary.apply(segWords); 	
				for (int i1 = 0; i1 < segWords.size(); i1++) 
				{ 
					for(int i = 0 ; i < 1000 ; i++)
					{
						String[] get_idf_word = get_idf_words_information[i].split("\t");
						if(get_idf_word[0].equals(segWords.get(i1).toString()))
						{
								judge_word_exists[i]++;
						}
				}
				}  

				for(int i = 0 ; i < 1000 ; i++)
				{
					if(judge_word_exists[i]!=0)
					{
						word.set("negative_"+i);
						context.write(word,one);
					}
				}
	
	}
	} 
	public static class IntSumReducer_work extends Reducer<Text,IntWritable,Text,IntWritable>
	{
		
		private IntWritable result = new IntWritable();
		private Text word = new Text();
		public void reduce(Text key, Iterable<IntWritable> values, Context context )
		throws IOException, InterruptedException
		{
		//System.out.println("why no reducer?");
		int sum = 0;
		//遍历迭代器values，得到同一key的所有value
		for (IntWritable val : values) { sum += val.get(); }
		result.set(sum);
		word.set(key.toString());
		context.write(word, result);
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
	job.setJarByClass(get_probabilities.class);//设置执行任务的jar
	job.setMapperClass(TokenizerMapper.class); //设置Mapper类
	//job.setCombinerClass(IntSumReducer.class); //设置Combine类
	job.setReducerClass(IntSumReducer_work.class); //设置Reducer类
	job.setOutputKeyClass(Text.class); //设置job输出的key
	//设置job输出的value
	job.setOutputValueClass(IntWritable.class);
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
