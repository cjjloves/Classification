# 文本向量化
## 1 特征选择——ID3方法
### 1.1 输出每个类的词频
用MapReduce对文本进行分词、计数处理，输出的键值对<word,[positive#appearance_num,neutral#appearance_number,negative#appearance_number>
### 1.2 利用ID3方法得到每个词的信息增益
根据信息增益公式，Gain(D,具体类别)=划分前的信息墒-划分后的信息墒，由于划分前的信息墒都是相同的，只需要计算并比较（-划分后的信息墒）即可  
根据1.1中输出的键值对，可以构造以下格式计算（-划分后的信息墒）
