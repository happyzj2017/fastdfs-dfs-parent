**FastDfs安装配置手册**


**1.简介**<br>
&nbsp;&nbsp;&nbsp;&nbsp;FastDFS是由淘宝的余庆先生所开发，是一个轻量级、高性能的开源分布式文件系统。<br>
用纯C语言开发，包括文件存储、文件同步、文件访问（上传、下载）、存取负载均衡、在线扩容、相同内容只存储一份等功能，<br>
适合有大容量存储需求的应用或系统。<br> 
&nbsp;&nbsp;&nbsp;&nbsp;做分布式系统开发时，其中要解决的一个问题就是图片、音视频、文件共享的问题，分布式文件系统正好可以解决这个需求。
  
**1.1 FastDFS系统架构**<br>
FastDFS两个主要的角色：Tracker Server 和 Storage Server。 <br>
- Tracker Server：跟踪服务器<br>
主要负责调度storage节点与client通信，在访问上起负载均衡的作用，和记录storage节点的运行状态，<br>
是连接client和storage节点的枢纽。<br> 
- Storage Server：存储服务器<br>
保存文件和文件的meta data（元数据）<br> 
- Group：文件组，也可以称为卷。<br>
同组内服务器上的文件是完全相同的，做集群时往往一个组会有多台服务器，<br>
上传一个文件到同组内的一台机器上后，FastDFS会将该文件即时同步到同组内的其它所有机器上，<br>
起到备份的作用，多个Storage Server可同属于一个组，用户也可以将文件上传到指定的组。 
- meta data：文件相关属性，<br>
键值对（Key Value Pair）方式，如：width=1024, height=768。和阿里云OSS的meta data相似<br>
 
**1.2 FastDFS文件上传流程** <br>
1)client询问tracker上传到的storage，不需要附加参数； <br>
2)tracker返回一台可用的storage； <br>
3)client直接和storage通讯完成文件上传。<br>

**1.3 FastDFS文件下载流程**<br>
1)client询问tracker下载文件的storage，参数为文件标识（组名和文件名）；<br> 
2)tracker返回一台可用的storage； <br>
3)client直接和storage通讯完成文件下载。<br>

**2. 论坛及参考资料**<br>
论坛：http://bbs.chinaunix.net/forum-240-1.html<br>
配置：http://blog.csdn.net/xyang81/article/details/52837974<br>
**3. 安装包下载**<br>
https://github.com/happyfish100<br>
当前最新的是5.12,示例中使用的是5.11

**4.&nbsp;配置安装(CentOS 7 64位)**<br>
**4.1 安装所需的依赖包**<br>
shell> yum install make cmake gcc gcc-c++<br>
**4.2 安装libfatscommon**<br>
shell> cd /usr/zj<br>
shell> unzip libfastcommon-master.zip<br>
shell> cd libfastcommon-master<br>
shell> ll<br>
-rw-r--r--. 1 root root 6670 10月  8 09:39 HISTORY<br>
-rw-r--r--. 1 root root  566 10月  8 09:39 INSTALL<br>
-rw-r--r--. 1 root root 1438 10月  8 09:39 libfastcommon.spec<br>
-rwxr-xr-x. 1 root root 3099 10月  8 09:39 make.sh<br>
drwxr-xr-x. 2 root root 4096 10月  8 09:39 php-fastcommon<br>
-rw-r--r--. 1 root root  812 10月  8 09:39 README<br>
drwxr-xr-x. 3 root root 4096 10月  8 09:39 src<br>
编译、安装<br>
shell> ./make.sh<br>
shell> ./make.sh install<br>
**4.3 安装tracker**<br>
shell> cd  /usr/zj<br>
shell> unzip fastdf-5.11.zip<br>
shell> cd fastdf-5.11<br>
shell> ll<br>
[root@localhost fastdfs-master]# ll<br>
drwxr-xr-x. 3 root root  4096 8月   8 15:17 client<br>
drwxr-xr-x. 2 root root  4096 8月   8 15:17 common<br>
drwxr-xr-x. 2 root root  4096 8月   8 15:17 conf<br>
-rw-r--r--. 1 root root 35067 8月   8 15:17 COPYING-3_0.txt<br>
-rw-r--r--. 1 root root  2881 8月   8 15:17 fastdfs.spec<br>
-rw-r--r--. 1 root root 32259 8月   8 15:17 HISTORY<br>
drwxr-xr-x. 2 root root    46 8月   8 15:17 init.d<br>
-rw-r--r--. 1 root root  7755 8月   8 15:17 INSTALL<br>
-rwxr-xr-x. 1 root root  5548 8月   8 15:17 make.sh<br>
drwxr-xr-x. 2 root root  4096 8月   8 15:17 php_client<br>
-rw-r--r--. 1 root root  2380 8月   8 15:17 README.md<br>
-rwxr-xr-x. 1 root root  1768 8月   8 15:17 restart.sh<br>
-rwxr-xr-x. 1 root root  1680 8月   8 15:17 stop.sh<br>
drwxr-xr-x. 4 root root  4096 8月   8 15:17 storage<br>
drwxr-xr-x. 2 root root  4096 8月   8 15:17 test<br>
drwxr-xr-x. 2 root root  4096 8月   8 15:17 tracker<br>
编译、安装<br>
shell> ./make.sh<br>
shell> ./make.sh install<br>
**4.4 启动tracker**
默认安装成功后服务脚本是存放在<br>
/etc/init.d/fdfs_storaged<br>
/etc/init.d/fdfs_trackerd<br>
（如果没有可能在/etc/rc.d/init.d/目录,可以在etc目录用find查找一下）<br>

复制tracker样例配置文件，并重命名（默认使用/etc/fdfs/tracker.conf）<br>
shell> cp /etc/fdfs/tracker.conf.sample /etc/fdfs/tracker.conf <br>
修改tracker配置文件 <br>
shell> vim /etc/fdfs/tracker.conf <br>
修改的内容如下：<br>
disabled=false              # 启用配置文件 <br>
port=22122                  # tracker服务器端口（默认22122）注意开启防火墙对应端口 <br>
base_path=/usr/zj/fastdfs/tracker  # 存储日志和数据的根目录 这个目录先手动创建好 <br>

具体配置请参考：http://bbs.chinaunix.net/thread-1941456-1-1.html <br>

启动tracker <br>
shell> /etc/init.d/fdfs_trackerd start <br>
首次启动成功后会在base_path下自动创建 data 和logs两个目录 <br>
如果出现失败可在查看logs/trackerd.log是否有异常 <br>

检测是否端口开启成功（如开启成功，远程机器无法telnet就注意开启防火墙对应的端口） <br>
 
设置tracker服务开机启动 <br>
shell> chkconfig fdfs_trackerd on <br>


**4.5 安装storage** <br>
编译安装与tracker类似 <br>
**4.6 启动storage** <br>
编辑配置文件 <br>
shell> cp /etc/fdfs/storage.conf.sample /etc/fdfs/storage.conf <br>
shell> vi /etc/fdfs/storage.conf <br>

修改的内容如下: <br>
disabled=false                # 启用配置文件 <br>
port=23000                     # storage服务端口 注意开启防火墙对应端口 <br>
base_path=/usr/zj/myfastdfs/storage #数据和日志文件存储根目录 目录要先创建好 <br>
store_path0=/usr/zj/myfastdfs/storage #第一个存储目录 目录要先创建好 <br>
tracker_server=192.168.14.129:22122  #tracker服务器IP和端口  <br>
http.server_port=8888               #http访问文件的端口 (默认是没法有开启) <br>

具体配置请参考：http://bbs.chinaunix.net/thread-1941456-1-1.html<br>

启动storage<br>
shell> /etc/init.d/fdfs_storaged start<br>
首次启动成功后会在base_path下自动创建 data 和logs两个目录<br>
如果出现失败可在查看logs/storaged.log是否有异常<br>
 
检测是否端口开启成功（如开启成功，远程机器无法telnet就注意开启防火墙对应的端口）<br>
 
设置storage服务开机启动<br>
shell> chkconfig fdfs_storaged on<br>

**4.7 测试上传**<br>
修改Tracker服务器客户端配置文件<br>
shell> cp /etc/fdfs/client.conf.sample /etc/fdfs/client.conf<br>
shell> vim /etc/fdfs/client.conf<br>
修改以下配置，其它保持默认<br>
base_path=/usr/zj/myfastdfs<br>
tracker_server=192.168.14.129:22122<br>
执行上传命令<br>
shell> /usr/bin/fdfs_upload_file /etc/fdfs/client.conf ./libfastcommon-master.zip<br>
这里注意的是<br>
/etc/fdfs/client.conf 表示使用的配置文件<br>
./libfastcommon-master.zip 表示要上传的文件<br>
返回了fileId表示操作成功 <br>
 
**4.8 测试下载**
修改Tracker服务器客户端配置文件 <br>
使用上传同配置文件 <br>
执行上传命令 <br>
shell> /usr/bin/fdfs_download_file <fileId> [localFileName]<br>
fileId表示上传文件返回的fileId<br>
localFileName 下载到本地后的新的文件名<br>

**5. 测试机 (192.168.2.123) 安装说明**<br>
启动脚本路径<br>
/etc/init.d/ <br>
Tracker <br>
fdfs_trackerd  <start| stop | restart> <br>
Storage <br>
fdfs_storaged  <start | stop | restart> <br>

配置路径<br>
/etc/fdfs/<br>
Tracker<br>
tracker.conf<br>

Storage<br>
strorage.conf(group1)<br>
strorage2.conf(group2)<br>
数据存储<br>
/usr/local/fdfs/data (group1)<br>
/usr/local/fdfs/data2/data (gropu2) <br>

**6. 注意事项**<br>
**6.1 启动关闭顺序** <br>
先启tracker再启动storage <br>
关闭最好是手动停止，先停storage再停tracker <br>

**6.2 http访问文件** <br>
从4.05版本开始已移除了对内置http的支持，需要额外安装Apache或Nginx模块。 <br>

**6.3 一台服务器装多个storage** <br>
复制一个storage.conf 修改服务端口。及数据目录，及日志目录 <br>
修改/etc/init.d/fdfs_storaged 里的CONF对应的配置文件为最新的 <br>
执行systemctl daemon-reload <br>
再执行/etc/init.d/fdfs_stroaged restart <br>

**6.4 上传文件到指定组** <br>
需要在获取stroage的连接时就指定好组，这样就可以获取到对应的组的连接，就可以上传到指定组。 <br>

