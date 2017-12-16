package knn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import knn.test_wordappearance.TokenizerMapper;

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


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

public class get_nearest_k {
	public static class TokenizerMapper 
	extends Mapper<Object, Text, Text, Text>
	{
	private Text word = new Text();
	private Text peoperties_20 = new Text();
	String[] get_TrainingSet_information = new String[1371];
	
	//signal the site
	private int[] get_TrainingSet_site=new int[1371];
	
	//signal the distance
	private double[] get_TrainingSet_distance=new double[1371];	
	
	//get the TrainingSet information
    protected void setup(Context context) throws IOException,InterruptedException{
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/u2/hadoop_installs/hadoop-2.7.4/TrainingData_properties/TrainingData_properties.txt")), "UTF-8"));
            String lineTxt = null;
            int get_line=0;
            while (((lineTxt = br.readLine()) != null)&&(get_line<1371)) {
            	get_TrainingSet_information[get_line] = new String(lineTxt);
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
			for(int i=0;i<1371;i++)
			{
				get_TrainingSet_distance[i]=0.0;
				get_TrainingSet_site[i]=i;
			}
			
			String sentence = value.toString();
			String[] gettitle = sentence.split("\t");
			
			String[] get_TestingSet_dimention = gettitle[2].split(",");
			//System.out.println(sentence);
			
			//System.out.println("begin!!");
			
			//calculate the distance
			for(int TrainingSet_num=0;TrainingSet_num<1371;TrainingSet_num++)
			{
				String[] split_TrainingSet_information = get_TrainingSet_information[TrainingSet_num].split("\t");
				String[] get_TrainingSet_dimention = split_TrainingSet_information[2].split(",");
				//System.out.println(split_TrainingSet_information[2]);
				//System.out.println(get_TrainingSet_distance[TrainingSet_num]);
				for(int dimention=0;dimention<1000;dimention++)
				{
					double x = 100*Double.parseDouble(get_TestingSet_dimention[dimention]);
					double y = 100*Double.parseDouble(get_TrainingSet_dimention[dimention]);
					get_TrainingSet_distance[TrainingSet_num]=get_TrainingSet_distance[TrainingSet_num]+(x-y)*(x-y);
				}
				//System.out.println(get_TrainingSet_distance[TrainingSet_num]);
			}
			//System.out.println(get_TrainingSet_distance.length);
			//bubble sort
			for(int i=0;i<get_TrainingSet_distance.length-1;i++)
			{
				for(int j=0;j<get_TrainingSet_distance.length-1-i;j++)
				{//内层循环控制每一趟排序多少次
					if(get_TrainingSet_distance[j]>get_TrainingSet_distance[j+1])
					{
							//change the site
							int temp=get_TrainingSet_site[j];
							get_TrainingSet_site[j]=get_TrainingSet_site[j+1];
							get_TrainingSet_site[j+1]=temp;
							
							//change the value
							double temp1 = get_TrainingSet_distance[j];
							get_TrainingSet_distance[j] = get_TrainingSet_distance[j+1];
							get_TrainingSet_distance[j+1] = temp1;
					}
				}
			} 
			
			//get the nearest 20
			StringBuffer s = new StringBuffer();
			for(int i = 0 ; i < 20 ; i++)
			{
				String[] get_site_information = get_TrainingSet_information[get_TrainingSet_site[i]].split("\t");
				s=s.append(get_site_information[0] + ",");
			}
		
			System.out.println(gettitle[0]);
			word.set(gettitle[0] + "\t" + gettitle[1]);
			peoperties_20.set(s.toString());
			context.write(word, peoperties_20);

			
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
	job.setJarByClass(get_nearest_k.class);
	job.setMapperClass(TokenizerMapper.class); 
	job.setOutputKeyClass(Text.class); 
	job.setOutputValueClass(Text.class);
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
