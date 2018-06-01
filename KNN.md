# 3.KNN基于公司新闻对公司分类
## 1 训练集数据前处理
### 1.1 训练集数据转码处理 
```
byte[] valueBytes = value.getBytes();
String sentence = new String(valueBytes, "GBK");
```
### 1.2 训练集数据保留中文处理，防止分词过程中出现不明符号
```
sentence=sentence.replaceAll("[^\u4e00-\u9fa5]", "");
```
## 2 文本向量化
### 2.1 特征选择——ID3方法
#### 2.1.1 输出每个类的词频
&emsp;&emsp;用MapReduce对文本进行分词、计数处理，输出的键值对<word,[positive#appearance_num,neutral#appearance_number,negative#appearance_number]>
#### 2.1.2 利用ID3方法得到每个词的信息增益
&emsp;&emsp;根据信息增益公式，Gain(D,具体类别)=划分前的信息墒-划分后的信息墒，由于划分前的信息墒都是相同的，只需要计算并比较划分后的信息墒的大小即可  
&emsp;&emsp;根据1.1中输出的键值对，可以构造以下格式计算划分后的信息墒
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/table1.JPG)  
&emsp;&emsp;对于每个word而言，划分后的信息墒 =（positive出现文本次数+neutral出现文本次数+negative出现文本次数）/文本总数 * I（positive出现文本次数，neutral出现文本次数，negative出现文本次数）+（总文本数-positive出现文本次数-neutral出现文本次数-negative出现文本次数）/总文本数 * I（positive文本数-positive出现文本次数，neutral文本数-neutral出现文本次数，negative文本数-negative出现文本次数）  
&emsp;&emsp;其中，I（x，y，z）= -x/(x+y+z) * log(x/(x+y+z))/log(2)-y/(x+y+z) * log(y/(x+y+z))/log(2)-z/(x+y+z) * log(z/(x+y+z))/log(2)  
&emsp;&emsp;在ID3方法中，信息增益大的属性更为重要，信息增益=划分前的信息墒-划分后的信息墒，划分后的信息墒为正，所以当只比较划分后的信息墒时，数值越小的word越重要  
&emsp;&emsp;截图如下（截图中算的是信息增益，但后来觉得划分前的信息墒都相同，所以只计算划分后的信息墒的方法更简洁）  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/Gain_change.JPG)  
&emsp;&emsp;筛选前1000个word作为每一个文本的属性，即构建一个1000的向量
#### 2.1.3 利用TF-IDF方法计算每一维的权重
&emsp;&emsp;对于每一个文本的每一维而言，其权重为  
&emsp;&emsp;W=词频 * log(所有文本个数/该词出现文本的个数 + 0.01)，其中，词频 = 该词在该文本出现的次数/该文本总词数  
&emsp;&emsp;该权重公式中，词频（TF）因文本而异，而log(所有文本个数/该词出现文本的个数 + 0.01)（IDF）则是固定的  
&emsp;&emsp;所以可以通过MapReduce,利用1.1中输出的键值对<word,[positive#appearance_num,neutral#appearance_number,negative#appearance_number]>，筛选符合的1000个词，计算出1000个词的IDF，输出<word,word_IDF>键值对  
&emsp;&emsp;然后，利用MapReduce读取训练集上所有文本，将前一步中得到的<word,word_IDF>键值对存入DistributedCache中供所有节点读取，在Map中建立一个1000维的初始值全为0的数组和一个计数符，1000维数组用于记录该文本中对于1000个词的词频，计数符用于记录该文本中的总词数。具体做法是，将分出的每一个词与DistributedCache中的word值比较，若DistributedCache中存在该词，则在1000维数组相应的项中的数加一，否则不改动数组值。在对所有词遍历完毕后，将1000维数组的值都除以计数符得到每一维的词频，并将其放入一个String类型中，每一维数值之间以逗号分隔。Map最终输出键值对<[类型  文本号]，1000维度权值>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/weighted.JPG)  
&emsp;&emsp;后续处理：在最终的测试文本与训练文本比较的过程中，因为训练文本中的某些文本在1000维上的数值都为0，影响分类效果，所以将1000维上数值都为0的训练文本删除  
&emsp;&emsp;用同样的方法得到测试集上所有文本在1000维上的数值，不过不删除都为0的文本  
### 2.2 对测试文本的前处理
&emsp;&emsp;在对测试文本处理的过程中，发现其因为篇幅短小的原因，造成含有的词数较小，导致在1000维上的数值有很大可能性全为0  
&emsp;&emsp;所以，我将同一个公司的所有新闻标题内容合并，当作一篇新闻处理
### 2.3 测试集文本分类
&emsp;&emsp;利用MapReduce读取测试集上的向量文本，将训练集上的向量文本存入DistributedCache中。对测试集上的每一个向量文本，计算其到所有训练向量文本上的欧氏距离（我用的是每一维的差平方之和），取距离最小的前20个向量文本，得到20个类别属性，统计并比较类别的数量，得到20条类别属性中出现次数最多的类别属性。输出键值对<[测试集文本公司编号 公司名称],类别>  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/knn_result.JPG)  
&emsp;&emsp;测试集类别的近似分布为：positive(5%)、neutral(90%)、negative(5%)。对此的合理解释是：因为将属于同一公司的新闻标题合并，作为一条新闻内容，所以这些新闻所含的表示积极或消极含义的词的比例是相似的，总体上说偏向于中立
## 3 存在的不足和可能改进部分
### 3.1 存在的不足
#### 3.1.1 KNN算法的约束性
&emsp;&emsp;在对训练集和测试集的文本向量化后发现，这些向量文本都是稀疏高维度的，KNN不适合比较稀疏高维度向量。相比而言，采用JVM算法可能会得到更精确的分类结果，因为JVM更适合对稀疏高维度向量进行分类
#### 3.1.2 串行过程占用大部分数据处理时间
&emsp;&emsp;该程序中的串行过程如对文本的每一个词计算其出现频数、对每一个测试集向量文本都要与训练集上的所有向量文本进行距离计算与比较等，占用了数据处理的大部分时间，大大增加了程序的执行时间
#### 3.1.3 没有实现一键式输出最终结果
&emsp;&emsp;我在写KNN算法实现文本分类时，由于需要完成特征选择、文本向量化、文本分类等具有后者的输入依赖前者的输出属性的步骤，所以编写了多个MapReduce程序分别实现单一的子任务，而没有使用Dreiver将多个MapReduce的Job连接起来
#### 3.1.4 扩展性不足
&emsp;&emsp;本程序中将训练集的文本数视为定值，如果训练集文本数改变的话，则需要更改所有程序中对应的变量，操作十分复杂
### 3.2 可能改进部分
#### 3.2.1 采用JVM算法
&emsp;&emsp;将得到的训练集文本向量和测试集文本向量经处理后放入weka中，利用weka中内置的JVM算法对测试集文本进行分类
#### 3.2.2 改进算法串行部分
&emsp;&emsp;或许可以尝试更少的维度来提高处理速度，但更少的维度可能会导致分类结果的精确性降低
#### 3.2.3 实现一键式输出结果
&emsp;&emsp;利用Driver将所有的MapReduce连接起来
#### 3.2.4 增加从键盘输入参数解决扩展性不足问题
&emsp;&emsp;可以利用从键盘输入参数解决程序扩展性不足问题，但需要对所有MapReduce的指定数值进行替代
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
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/Sort_word.JPG)  
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
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/test_KNN.JPG)  

