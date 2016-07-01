提高性能：

 TargetDataSource的URL里添加rewriteBatchedStatements=true能够大幅提高Mysql的性能
 
 DruidDataSource使用unfairlock
 
 加大Reader、Writer的个数
 
 加大Reader、Writer的BatchSize

 加大BufferSize
 
 增大JVM内存
 
玩法： 
 
  同步或批量迁移一个表到另外一个表
  
  聚合一个表写到另外一个表

  生成parquet file保存在本地
