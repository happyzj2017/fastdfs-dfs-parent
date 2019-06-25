**dfs文件系统说明**<br>

**1.系统架构**<br>
由dfs-fastdfs-client-api、dfs-core、 dfs-app-api三部分组成。<br>

- **dfs-fastdfs-client-api（fastdfs 客户端）**<br>
fastdfs提供的java客户端api，所有java相关功能都在基于这个基础上封装，扩展。<br>
第三方应用不需要关心该接口.<br>

- **dfs-core（http服务器）**<br>
提供http接口服务，基于spring boot实现。<br>
提供http服务器信息获取，http上传,删除上报，及http下载（建议直接用于页面显示的图片类资调用该接口），<br>
该服务会记录文件的基本信息。其中服务器信息获取，上传上报都由dfs-app-api自动完成，<br>
第三方应用不需要关心。http下载会一些参数，文档后面中有详细描述。<br>

- **dfs-app-api（应用sdk）**<br>
第三方java应用可以集成该sdk，完成初始化后可以直接调用api完成上传，下载，及删除文件。

**2.dfs-app-api（应用sdk）说明**

- **初始化**<br>
APIConfigure config = new APIConfigure("ofweek", "http://192.168.2.123:8881/");<br>
DFSAppClient.instance().initAPIConfigure(config);<br>
第一行代码新建一个配置对象，第一个参数是appKey（目前是手动分配），<br>
第二个参数是dfs-core对应的http服务ip及端口。<br>
这两个参数都是必须是正确的。否则无法完成初始化。<br>
第二行代码，实现执行初化操作，从dfs-core获取trackers服务器信息，及appKey对应的groupName，<br>
这些动作都由sdk自动完成，第三方应用不需要关心。<br>

**`注意：必须完成初始化并且只需要初始化一次，其他操作之前必须在完成初化，建议在应用启动时完成初始化。`**

- **上传文件**<br>
String fileId = DFSAppClient.instance().uploadFile(new File("d://ideaIU-14.1.exe"));<br>
上传文件只需要给出File对象就可以完成上传。<br>
返回的fileId字符串:<br>
示例："group1/M00/00/00/wKgCe1ncO4qEPu9uAAAAABYqHQ8942.exe"<br>
fileId是后续对文件进行操作的基本参数，第三方应用拿到该值后应本地做好保存。<br>

- **下载文件**<br>
FileOutputStream fos = new FileOutputStream(f);<br>
DFSAppClient.instance().downloadFile(fileId, fos, true);<br>
下载文件需要三个参数，<br>
第一个是fileId，上传成功返回的。<br>
第二个是输出流，下载的文件将通过该输出流输出。<br>
第三个是是否关闭第二个参数的输出流。true为关闭。false为不关闭。<br>

- **删除文件**<br>
DFSAppClient.instance().deleteFile(fileId);<br>
删除文件只需要传入fileId,就可以删除指定的文件<br>
该方法会返回0表示删除成功，其他表示失败。<br>

**3.dfs-core（http服务器）说明**<br>

- **http下载接口**<br>
http://192.168.2.123:8881/dfs/v1/download?fileId=group1/M00/00/00/wKgCe1nOFtaEb-XWAAAAAPCUA8M333.jpg&direct=true<br>
http下载请求类似上面这种地址，<br>
请求地址：http://192.168.2.123:8881/dfs/v1/download<br>
其中有两个参数<br>
fileId表示文件id,表示上传成功后返回的文件id。<br>
direct表示是否直接显示，非直接显示会提示下载，默认是非直接显示。<br>


