# 代码说明
&emsp;&emsp;1 get_probabilities.java利用KNN方法得到1000维属性，得到每一维属性上对应词在每一类上出现的次数，输出键值对<类别_维度标号，对应词在该类别中出现的次数>  
&emsp;&emsp;2 probabilities_process.java利用1中得到的键值对，计算出每一类的每一位度的P（xj | Yi）和P（-xj | Yi），输出键值对<类别_维度标号，[P（xj | Yi） P（-xj | Yi）]>  
&emsp;&emsp;3 probabilities_process_2.java对2中得到的键值对进行特殊处理，防止其过多P（xj | Yi）的数值为0，输出键值对<类别_维度标号，[P（xj | Yi） P（-xj | Yi）]>  
&emsp;&emsp;4 testset_class.java利用KNN中得到的测试集文本向量和3中得到的键值对，对测试集文本向量进行分类，输出键值对<[公司编号，公司名称],类别>  
&emsp;&emsp;5 test.java得到分类频率  
