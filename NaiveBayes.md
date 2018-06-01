# 4.朴素贝叶斯算法基于公司新闻对公司分类
## 1 类概率
### 1.1 类概率逻辑
&emsp;&emsp;首先，对于每一类Yi(i=1,2,3),即positive、neutral、negative三类，得到每一类的概率P(Yi)，这通过观察每一类中的文本文档数，在程序中写定即可  
&emsp;&emsp;其次，本程序利用KNN中利用ID3算法中信息增益最高的前1000个词作为属性  
&emsp;&emsp;对于这些属性xj(j=1,2,...,1000)，对属性包含的情况划分为两类：在每一类文本中该词出现数不为0，即xj；或者该词出现数为0，即-xj  
&emsp;&emsp;即对于每一个属性，只需要计算P（xj | Yi）和P（-xj | Yi）即可，其中，j=1,2,...,1000，i=1,2,3  
&emsp;&emsp;其中，P（xj | Yi）= xj对应词在Yi类中出现的文本数/Yi类所有文本数；P（-xj | Yi）= xj对应词在Yi类中未出现的文本数/Yi类所有文本数  
&emsp;&emsp;对于P（xj | Yi）的计算，需要用MapReduce统计xj属性所对应的词在Yi类中出现的文本数，具体方法是用DistributedCache存储KNN中得到的信息增益前1000个词以及建立1000维初始值为0的数组  
&emsp;&emsp;Map过程对分出的每一个词与DistributedCache中的词比较，若DistributedCache中有相同的词且数组中相应项数值为0，则将数组中相应的项增1，并输出键值对<word_site,1>  
&emsp;&emsp;&emsp;Reduce过程对数值求和并输出<class#word_site,sum>  
得到结果部分截图如下  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/NB_wordsum.JPG)  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/p_result1.JPG)  
## 2 对测试文本向量进行分类
### 2.1 分类逻辑
&emsp;&emsp;分类材料利用KNN中得到的测试集文本向量，将1.1中得到的P(Yi)、P（xj | Yi）和P（-xj | Yi）存入DistributedCache中  
&emsp;&emsp;Map过程计算每一类的概率。设定三个Double类p值为1，对于测试集文本向量中每一维属性，若数值为0，则分别找到三类相应位置的P（-xj | Yi），分别将三个p与P（-xj | Yi）相乘；若数值不为0，则分别找到三类相应位置的P（xj | Yi），分别将三个p与P（xj | Yi）相乘。最后，将三个p分别与P(Yi)相乘，比较三个p的大小，取最大p值对应的类作为该文本的类。输出<[公司标号  公司名称]，所属类别>  
&emsp;&emsp;分类结果：positive（22%）、neutral（16%）、negative（62%），原因在于:negative类训练集数据只含有1000个词中的550个左右，导致1000维属性中有大约45%的属性的P（-xj | Yi）值为1，其比例比positive类（39%）和neutral类（37%）都高。在测试集文本向量为稀疏向量的情况下，每个测试集文本更偏向negative类  
分类结果截图  
![Image text](https://raw.github.com/cjjloves/Project2/master/pro2_pic/NB_result1.JPG) 
### 2.2 遇到的问题
&emsp;&emsp;在实际p与P（xj | Yi）或P（-xj | Yi）相乘的过程中，由于P（xj | Yi）和P（-xj | Yi）的数值都小于1，所以多个P（xj | Yi）或P（-xj | Yi）相乘会让最终p值趋于0，所以实际相乘过程中对于每一个P（xj | Yi）或P（-xj | Yi）与p相乘，再乘以2保证最终结果不趋于0而易于比较
## 3 存在的不足和可能的改进之处
### 3.1 存在的不足
&emsp;&emsp;由于该程序的处理数据方法与KNN程序相似，所以该含有KNN程序中列出的问题  
&emsp;&emsp;另外，在进行测试集文本向量分类的过程中，需要对每一维属性找到对应位置的P（xj | Yi）和P（-xj | Yi），本程序是将维度的标号与分布存贮中的键值对<类别_维度标号,[P（xj | Yi）  P（-xj | Yi）]>中的维度标号比较，若标号相同，则根据该维度上的权值获取对应的P（xj | Yi）或P（-xj | Yi）。该过程使得程序对于每一个维度属性需要最多进行三千次比较，严重增大了程序的处理时间
### 3.2 可能的改进之处
&emsp;&emsp;对于多次比较的问题，可以再通过一次MapReduce对键值对<类别_维度标号,[P（xj | Yi）  P（-xj | Yi）]> 按照维度标号从小到大进行排序，这种处理方法使得每一维度无需再寻找对应的位置，减少了不必要的比较

