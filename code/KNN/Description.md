# 程序代码说明
## 1 特征选取
&emsp;&emsp;1.1 DoFre.java分别得到三类文本的词频，对于每一类，分别输出键值对<word,frequency>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/DoFre.JPG)  
&emsp;&emsp;1.2 merge_word.java对1.1得到的键值对进行合并，输出键值对<word,[positive#frequency,neutral#frequency,negative#frequency]>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/merge_word.JPG)  
&emsp;&emsp;1.3 Word_GR.java利用1.2得到的键值对，计算每一个词的信息增益，输出键值对<word,Gain>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/Word_GR.JPG)  
&emsp;&emsp;1.4 Sort_word.java对1.3得到的键值对按照Gain值进行从大到小排序，输出键值对<word,Gain>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/Sort_word.JPG)  
&emsp;&emsp;1.5 select_word.java取1.4中前1000个键值对  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/select_word.JPG)  
## 2 权重赋值
&emsp;&emsp;2.1 Tfidf_apearsum.java利用1.1，得到整个训练集所有词的出现次数，输出键值对<word,frequency>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/Tfidf_apearsum.JPG)  
&emsp;&emsp;2.2 Tfidf_idf.java利用2.1得到的键值对，所有词的IDF，输出键值对<word,word_idf>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/Tfidf_idf.JPG)  
&emsp;&emsp;2.3 Tfidf_idf_select.java利用2.2和1.5得到的键值对，筛选得到1000维所对应的词的IDF值，输出键值对<word_select,idf>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/Tfidf_idf_select.JPG)  
&emsp;&emsp;2.4 txt_wordsum.java得到每一类训练集的单词总数，输出键值对<class,word_sum>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/txt_wordsum.JPG)  
&emsp;&emsp;2.5 txt_wordappearance_time.java利用2.3和2.4得到的键值对，得到每一个训练集文本的文本向量，输出键值对<[class,文本名]，1000维权值>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/txt_wordappearance_time.JPG)  
&emsp;&emsp;2.6 merge_test_title.java合并测试集新闻标题，输出键值对<[公司编号，公司名称]，新闻标题合并值>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/merge_test_title.JPG)  
&emsp;&emsp;2.7 test_wordsum.java得到测试集每一条合并新闻的总次数，输出键值对<[公司编号，公司名称]，总词数>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/test_wordsum.JPG)  
&emsp;&emsp;2.8 test_wordappearance.java利用2.3和2.7得到的键值对，得到每一个测试集文本的文本向量，输出键值对<[公司编号，公司名称]，1000维权值>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/test_wordappearance.JPG)  
&emsp;&emsp;2.9 get_nearest_k.java利用2.5和2.8得到的键值对，比较测试集文本向量和训练集文本向量的欧氏距离，得到距离最近的前20个类，输出键值对<[公司编号，公司名称]，距离最近的前20个类>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/get_nearest_k.JPG)  
&emsp;&emsp;2.10 get_result.java利用2.9得到的键值对，得到前20个类中数量最多的类，输出键值对<[公司编号，公司名称]，类名>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/get_result.JPG)  
## 3 分类比例
&emsp;&emsp;3.1 test.java输出类结果  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/test.JPG)  
