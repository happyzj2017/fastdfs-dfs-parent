package com.netwaymedia.dfs.app.api;

import com.netwaymedia.dfs.app.api.resp.ServerData;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * fastdfs客户端
 */
public class DFSAppClient {

    private static int UPLOAD_BUFFER_SIZE = 1024 * 1024; // 上传缓存
    private static int DOWNLOAD_BUFFER_SIZE = 1024 * 1024; // 下载缓存
    private static int NEED_BATCH_UPLOAD_SIZE = UPLOAD_BUFFER_SIZE; // 需要分批上传的大小
    private static int CORE_THREAD_SIZE = 5; // 线程池大小

    private static final String v1Server = "dfs/v1/server";
    private static final String v1Supload = "dfs/v1/sUpload";
    private static final String v1Eupload = "dfs/v1/eUpload";
    private static final String v1Del = "dfs/v1/del";

    private static String GET_SERVER_URL = null;
    private static String START_UPLOAD_URL = null;
    private static String END_UPLOAD_URL = null;
    private static String DELETE_URL = null;

    private static DFSAppClient instance = new DFSAppClient();

    private TrackerClient trackerClient = null;

    private APIConfigure config = null;

    private String clientAppKey;
    private String clientGroupName;

    /**
     * 线程池
     */
    private ExecutorService executorService = null;

    private DFSAppClient() {
    }

    public static DFSAppClient instance() {
        return instance;
    }

    /**
     * 初始化设置参数(只需要初始化一次)
     *
     * @param config API参数配置
     */
    public void initAPIConfigure(APIConfigure config) throws MyException {
        try {
            this.config = config;

            if (this.config.getUploadBufferSize() > 0) {
                UPLOAD_BUFFER_SIZE = this.config.getUploadBufferSize();
                NEED_BATCH_UPLOAD_SIZE = UPLOAD_BUFFER_SIZE;
            }

            if (this.config.getDownloadBufferSize() > 0) {
                DOWNLOAD_BUFFER_SIZE = this.config.getDownloadBufferSize();
            }

            if (this.config.getCoreThreadSize() > 0) {
                CORE_THREAD_SIZE = this.config.getCoreThreadSize();
            }

            executorService = new ThreadPoolExecutor(CORE_THREAD_SIZE, CORE_THREAD_SIZE, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

                @Override
                public Thread newThread(Runnable r) {
                    Thread s = Executors.defaultThreadFactory().newThread(r);
                    s.setDaemon(true);
                    s.setName("DFS-HTTP-TASK-THREAD-" + s.getId());
                    return s;
                }
            });

            if (this.config.getHttpServerUrl() == null || this.config.getHttpServerUrl().trim().length() == 0) {
                throw new MyException("init server error, http server url is empty!");
            }

            String baseServerUrl = this.config.getHttpServerUrl();
            if (!baseServerUrl.endsWith("/")) {
                baseServerUrl += "/";
            }

            GET_SERVER_URL = baseServerUrl + v1Server;
            START_UPLOAD_URL = baseServerUrl + v1Supload;
            END_UPLOAD_URL = baseServerUrl + v1Eupload;
            DELETE_URL = baseServerUrl + v1Del;

            clientAppKey = this.config.getAppKey();
            ServerData serverData = APIHttpUtils.getServerInfo(GET_SERVER_URL, clientAppKey);
            clientGroupName = serverData.getBody().getGroupName();
            if (StringUtils.isEmpty(clientAppKey) || StringUtils.isEmpty(clientGroupName)) {
                throw new MyException("init server error, can't get appKey or groupName from http server !");
            }
            Properties props = new Properties();
            props.setProperty(ClientGlobal.PROP_KEY_TRACKER_SERVERS, serverData.getBody().getTrackerServers());
            ClientGlobal.initByProperties(props);
        } catch (Exception e) {
            throw new MyException("init server error ! , " + e.getMessage());
        }
        trackerClient = new TrackerClient();
    }

    /**
     * 分配服务端的链接
     *
     * @return
     * @throws MyException
     */
    private StorageClient1 assignResourse() throws MyException {
        return assignResourseByFileId(null);
    }

    /**
     * 分配服务端的链接
     *
     * @param fileId
     * @return
     * @throws MyException
     */
    private StorageClient1 assignResourseByFileId(String fileId) throws MyException {

        StorageClient1 client = new StorageClient1();
        try {
            if (client.getTrackerServer() == null
                    || (client.getTrackerServer() != null && client.getTrackerServer().getSocket().isClosed())) {
                TrackerServer trackerServer = trackerClient.getConnection();
                if (trackerServer == null) {
                    throw new MyException("can't get trackerServer!");
                }
                client.setTrackerServer(trackerServer);
            }

            if (client.getStorageServer() == null
                    || (client.getStorageServer() != null && client.getStorageServer().getSocket().isClosed())) {
                StorageServer storageServer = null;
                if (fileId != null && fileId.length() > 0) {
                    int pos = fileId.indexOf(StorageClient1.SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR);
                    storageServer = trackerClient.getFetchStorage(client.getTrackerServer(), fileId.substring(0, pos),
                            fileId.substring(pos + 1));
                } else {
                    storageServer = trackerClient.getStoreStorage(client.getTrackerServer(), null);
                }
                if (storageServer == null) {
                    releaseResourse(client);
                    throw new MyException("can't get stroageServer!");
                }
                client.setStorageServer(storageServer);
            }

            return client;

        } catch (Exception e) {
            throw new MyException("connect to server error !");
        }
    }

    /**
     * 分配服务端的链接
     *
     * @param groupName 不能为空
     * @return
     * @throws MyException
     */
    private StorageClient1 assignResourseByGroupName(String groupName) throws MyException {
        if (groupName == null || groupName.trim().length() == 0) {
            throw new MyException("can't get connect ,because groupName is null !");
        }
        StorageClient1 client = new StorageClient1();
        try {
            if (client.getTrackerServer() == null
                    || (client.getTrackerServer() != null && client.getTrackerServer().getSocket().isClosed())) {
                TrackerServer trackerServer = trackerClient.getConnection();
                if (trackerServer == null) {
                    throw new MyException("can't get trackerServer!");
                }
                client.setTrackerServer(trackerServer);
            }
            if (client.getStorageServer() == null
                    || (client.getStorageServer() != null && client.getStorageServer().getSocket().isClosed())) {
                StorageServer storageServer = trackerClient.getStoreStorage(client.getTrackerServer(), groupName);
                if (storageServer == null) {
                    releaseResourse(client);
                    throw new MyException("can't get stroageServer!");
                }
                client.setStorageServer(storageServer);
            }
            return client;
        } catch (Exception e) {
            throw new MyException("connect to server error !");
        }
    }

    /**
     * 释放服务端的链接
     */
    private void releaseResourse(StorageClient1 client) {
        try {
            if (client != null) {
                if (client.getStorageServer() != null) {
                    client.getStorageServer().close();
                }

                if (client.getTrackerServer() != null) {
                    client.getTrackerServer().close();
                }

                client.setTrackerServer(null);
                client.setStorageServer(null);
            }

        } catch (Exception e) {

        }
    }

    /**
     * 上传文件
     *
     * @param file 需要上传的文件
     * @return 返回fileId
     * @throws FileNotFoundException
     * @throws MyException
     */
    public String uploadFile(File file) throws FileNotFoundException, MyException {
        return uploadFile(file, null);
    }

    /**
     * 上传文件
     *
     * @param file      需要上传的文件
     * @param meta_list 文件额外属性
     * @return 返回fileId
     * @throws FileNotFoundException
     * @throws MyException
     */
    private String uploadFile(File file, NameValuePair[] meta_list) throws FileNotFoundException, MyException {
        String fileName = file.getName();
        String extName = FilenameUtils.getExtension(fileName);
        Integer fileInfoId = -1;
        try {
            long fileLength = file.length();
            if (START_UPLOAD_URL == null
                    || END_UPLOAD_URL == null
                    || clientGroupName == null) {
                throw new MyException("DFS app api sdk not initialized");
            }
            fileInfoId = APIHttpUtils.startUpload(START_UPLOAD_URL, clientAppKey, fileName, fileLength);
        } catch (Exception e) {
            throw new MyException(e.getMessage());
        }
        return uploadFile(new FileInputStream(file), clientGroupName, extName, meta_list, fileInfoId);
    }

    /**
     * 上传文件
     *
     * @param in         需要上传的输入流
     * @param groupName  指定文件上传的组（卷），为空表示不指定
     * @param extName    文件扩展名(不能包含'.')
     * @param meta_list  文件额外属性
     * @param fileInfoId core server生成的文件id
     * @return 返回fileId
     * @throws MyException
     */
    private String uploadFile(InputStream in, String groupName, String extName, NameValuePair[] meta_list,
                              Integer fileInfoId) throws MyException {
        if (in == null) {
            throw new MyException("inputstream is null !");
        }
        StorageClient1 client = null;
        String fileId = null;
        try {
            boolean isSetGroup = false;
            if (StringUtils.isNotEmpty(groupName)) {
                isSetGroup = true; // 上传到指定的组（卷）
            }
            if (isSetGroup) {
                client = assignResourseByGroupName(groupName);
            } else {
                client = assignResourse();
            }
            long length = in.available();
            if (length > NEED_BATCH_UPLOAD_SIZE) { // 超过指定大小就进行分批上传
                if (!(in instanceof BufferedInputStream)) {
                    in = new BufferedInputStream(in);
                }
                int readFlag = -1;
                byte[] bytes = new byte[UPLOAD_BUFFER_SIZE];
                while ((readFlag = in.read(bytes)) > 0) {
                    if (fileId == null) { // 需要新上传文件
                        if (isSetGroup) {
                            fileId = client.upload_appender_file1(groupName, bytes, extName, meta_list);
                        } else {
                            fileId = client.upload_appender_file1(bytes, extName, meta_list);
                        }
                    } else {
                        client.append_file1(fileId, bytes, 0, readFlag);
                    }
                }
            } else {
                byte[] fileBytes = new byte[(int) length];
                in.read(fileBytes);
                if (isSetGroup) {
                    fileId = client.upload_file1(groupName, fileBytes, extName, meta_list);
                } else {
                    fileId = client.upload_file1(fileBytes, extName, meta_list);
                }
            }
        } catch (Exception e) {
            throw new MyException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
            releaseResourse(client);
        }

        try {
            if (StringUtils.isNotEmpty(fileId) && fileInfoId != null && fileInfoId > 0) {
                executorService.execute(new EndUpLoadHttpTask(END_UPLOAD_URL, fileId, fileInfoId));
            }
        } catch (Exception e) {
        }
        return fileId;
    }

    /**
     * 下载
     *
     * @param fileId  fastdfs的文件id
     * @param out     输出流
     * @param isClose 是否关闭输出流
     * @throws MyException
     */
    public void downloadFile(String fileId, OutputStream out, boolean isClose) throws MyException {
        downloadFile(fileId, out, isClose, -1L);
    }

    /**
     * 下载文件
     *
     * @param fileId     上传成功后的fileId
     * @param out        输出流
     * @param isClose    是否关闭输出流
     * @param fileLength 文件长度,-1表示未知
     * @throws MyException
     * @throws IOException
     */
    private void downloadFile(String fileId, OutputStream out, boolean isClose, long fileLength) throws MyException {
        StorageClient1 client = null;
        try {
            if (clientGroupName == null) {
                throw new MyException("DFS app api sdk not initialized");
            }
            client = assignResourseByFileId(fileId);
            if (fileLength < 0) {
                FileInfo fileInfo = client.get_file_info1(fileId);
                if (fileInfo != null) {
                    fileLength = fileInfo.getFileSize();
                }
            }
            if (fileLength < 0) {
                throw new MyException("Unkonw file info! fileId:" + fileId);
            }
            if (!(out instanceof BufferedOutputStream)) {
                out = new BufferedOutputStream(out);
            }
            int bufferSize = DOWNLOAD_BUFFER_SIZE;
            int offset = 0;
            while (fileLength > 0) {
                if (fileLength < bufferSize) {
                    bufferSize = (int) fileLength;
                }
                byte[] bytes = client.download_file1(fileId, offset, bufferSize);
                fileLength -= bytes.length;
                offset += bytes.length;
                out.write(bytes);
                out.flush();
            }
        } catch (MyException e) {
            throw new MyException(e.getMessage());
        } catch (Exception e) {
            throw new MyException("download file error ! , " + e.getMessage());
        } finally {
            releaseResourse(client);
            if (isClose) {
                IOUtils.closeQuietly(out);
            }
        }
    }

    /**
     * 删除文件
     *
     * @param fileId
     * @return 0 成功,2文件不存在,其他值失败
     * @throws IOException
     * @throws MyException
     */
    public int deleteFile(String fileId) throws MyException {
        StorageClient1 client = null;
        int result = -1;
        if (DELETE_URL == null || clientGroupName == null) {
            throw new MyException("DFS app api sdk not initialized");
        }
        try {
            client = assignResourseByFileId(fileId);
            result = client.delete_file1(fileId);
        } catch (Exception e) {
            result = -1;
        } finally {
            releaseResourse(client);
        }

        if (result == 0) {
            try {
                executorService.execute(new DeleteHttpTask(DELETE_URL, fileId));
            } catch (Exception e) {
            }
        }
        return result;
    }

    class EndUpLoadHttpTask implements Runnable {

        private String url;
        private String fileId;
        private Integer fileInfoId;

        public EndUpLoadHttpTask(String url, String fileId, Integer fileInfoId) {
            this.url = url;
            this.fileId = fileId;
            this.fileInfoId = fileInfoId;
        }

        @Override
        public void run() {
            APIHttpUtils.endUpload(url, fileId, fileInfoId);
        }

    }

    class DeleteHttpTask implements Runnable {

        private String url;
        private String fileId;

        public DeleteHttpTask(String url, String fileId) {
            this.url = url;
            this.fileId = fileId;
        }

        @Override
        public void run() {
            APIHttpUtils.delete(url, fileId);
        }

    }
}
