package knn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import knn.txt_wordappearance_time.TokenizerMapper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
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

public class test_wordappearance {
	public static class TokenizerMapper //瀹氫箟Map绫诲疄鐜板瓧绗︿覆鍒嗚В
	extends Mapper<Object, Text, Text, Text>
	{
	private Text word = new Text();
	private Text peoperties_1000 = new Text();
	String[] top_ig_words = new String[1000];
	String[] txt_wordsum = new String[3266];
	private double[] word_appearance=new double[1000];	
    protected void setup(Context context) throws IOException,InterruptedException{
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/u2/hadoop_installs/hadoop-2.7.4/TrainingData_TFIDF_IDF_select/TrainingData_TFIDF_IDF_select.txt")), "UTF-8"));
            String lineTxt = null;
            int get_line=0;
            while (((lineTxt = br.readLine()) != null)&&(get_line<1000)) {
            	top_ig_words[get_line] = new String(lineTxt);
            	get_line++;
            }
            br.close();
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }
        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/u2/hadoop_installs/hadoop-2.7.4/fulldata_merge_wordsum/fulldata_merge_wordsum.txt")), "UTF-8"));
            String lineTxt = null;
            int get_line=0;
            while (((lineTxt = br.readLine()) != null)&&(get_line<3266)) {
            	txt_wordsum[get_line] = new String(lineTxt);
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
			
			
			for(int i=0;i<1000;i++){word_appearance[i]=0.0;}
	
			String sentence = value.toString();
			String[] gettitle = sentence.split("\t");
			//System.out.println(sentence);
			Segment segment = HanLP.newSegment().enablePartOfSpeechTagging(false); 
			List<Term> segWords = segment.seg(gettitle[2]); 
			CoreStopWordDictionary.apply(segWords); 
			for (int i1 = 0; i1 < segWords.size(); i1++) { 
				for(int i=0;i<1000;i++)
				{
					String[] getword = top_ig_words[i].toString().split("\t");
					if(getword[0].equals(segWords.get(i1).toString()))
						word_appearance[i]++;
				}
			}  
			try{
		     double wordsum=-1.0;
			for(int i=0;i<3266;i++)
			{
				String[] getword = txt_wordsum[i].toString().split("\t");
				//System.out.println(getword.length);
				if((getword[0]).equals(gettitle[0]))
				{	
					wordsum=Double.parseDouble(getword[2]);
					//System.out.println("get the wordwum!!");
				}
			}
			StringBuffer s = new StringBuffer();
			for(int i=0;i<1000;i++)
			{
				String[] getword = top_ig_words[i].toString().split("\t");
				s=s.append(Double.toString(word_appearance[i]/wordsum*Double.parseDouble(getword[1]))+",");
			}
			
			word.set(gettitle[0] + "\t" + gettitle[1]);
			peoperties_1000.set(s.toString());
			context.write(word, peoperties_1000);
			}catch(Exception e){
				 System.err.println("read errors :" + e);
			}
			System.out.println(gettitle[0]);
			
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
	job.setJarByClass(test_wordappearance.class);//璁剧疆鎵ц浠诲姟鐨刯ar
	job.setMapperClass(TokenizerMapper.class); 
	job.setOutputKeyClass(Text.class); 
	job.setOutputValueClass(Text.class);
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
