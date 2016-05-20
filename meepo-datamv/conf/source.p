url=jdbc:mysql://10.128.9.80:3306/test?useUnicode=true&amp;characterEncoding=UTF-8
driverClassName=com.mysql.jdbc.Driver
username=root
password=oneapm
initialSize=100
minIdle=100
maxActive=5000
maxWait=5000
defaultAutoCommit=true
timeBetweenEvictionRunsMillis=300000
minEvictableIdleTimeMillis=300000
validationQuery=SELECT 'x' FROME DUAL
testWhileIdle=true
testOnBorrow=false
testOnReturn=false
poolPreparedStatements=true
maxPoolPreparedStatementPerConnectionSize=20
removeAbandoned=true
removeAbandonedTimeout=1200
logAbandoned=true

