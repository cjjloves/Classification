# 朴素贝叶斯算法
## 1 类概率
### 1.1 对属性范围进行划分
&emsp;&emsp;首先，对于每一类Yi(i=1,2,3),即positive、neutral、negative三类，得到每一类的概率P(Yi)，这通过观察每一类中的文本文档数，在程序中写定即可  
&emsp;&emsp;其次，本程序利用KNN中利用ID3算法中信息增益最高的前1000个词作为属性  
&emsp;&emsp;对于这些属性xj(j=1,2,...,1000)，对属性的划分为每一类文本中该词出现数不为0，即xj；或者该词出现数为0，即-xj  
&emsp;&emsp;即对于每一个属性，只需要计算P（xj|Yi）和P（-xj|Yi）即可，其中，j=1,2,...,1000，i=1,2,3  
&emsp;&emsp;其中，P（xj|Yi）= xj对应词出现的文本数/Yi类所有文本数；P（-xj|Yi）= xj对应词未出现的文本数/Yi类所有文本数
&emsp;&emsp;对于P（xj|Yi）的计算，需要用MapReduce统计xj对应词出现的文本数，即用DistributedCache存储KNN中得到的信息增益前1000个词以及建立1000维初始值为0的数组  
&emsp;&emsp;Map过程对分出的每一个词与DistributedCache中的词比较，若DistributedCache中有相同的词且数组中相应项数值为0，则将数组中相应的项增1，并输出键值对<word_site,1>  
&emsp;&emsp;Reduce过程对数值求和并输出<class#word_site,sum>  
得到结果部分截图如下  
