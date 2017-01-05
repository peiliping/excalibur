JVM Monitor
=============

1）  -i metric采集间隔时间，默认3000（ms）

2）  -t N次metric采集后，上传数据到后端server， t×i等于上传周期

3）  -p 指定要监控的进程ID，可以写多个，以逗号间隔 -p 1,3,4

4）  -m 指定加载的模块，默认全部加载

5）  -r 后端server的IP和端口

6）  -d 无参数 开启debug功能，打印每次上传的信息，以及正在监控的PID

7）  -f 无参数 重新探针时，重新抓取jvmflags，默认是不开启的

8）  -M saas or test

9）  -a 指定appname信息，在test模式下有效

10） -o 打印所有的perfdata数据，不执行其他操作
