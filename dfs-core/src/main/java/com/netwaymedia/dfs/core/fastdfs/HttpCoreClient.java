package com.netwaymedia.dfs.core.fastdfs;

import com.netwaymedia.dfs.base.ErrorCode;
import com.netwaymedia.dfs.base.config.SystemConfig;
import com.netwaymedia.dfs.base.spring.SpringContextUtils;
import com.netwaymedia.dfs.core.fastdfs.task.DFSUploadFileTask;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * fastdfs客户端
 */
public class HttpCoreClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpCoreClient.class);

    private SystemConfig config = SpringContextUtils.getBean(SystemConfig.class);

    private static final String UTF8 = "utf-8";

    public static final int UPLOAD_RESULT_SUCCESS = 0;
    public static final int UPLOAD_RESULT_FAILURE = 1;

    private static final int NEED_BATCH_UPLOAD_SIZE = 1024 * 1024; // 大于1M分批上传
    private static final int UPLOAD_BUFFER_SIZE = 1024 * 1024; // 上传缓存
    private static final int DOWNLOAD_BUFFER_SIZE = 128 * 1024; // 下载缓存 这里只有http下载，所有将缓存设小，所以也不适应大于10M的文件下载

    private String tracker_servers = null;

    private int UPLOAD_THREAD_SIZE = config.getUpload();

    /**
     * 处理上传任务的线程池
     */
    private ExecutorService uploadExecutorService = new ThreadPoolExecutor(UPLOAD_THREAD_SIZE, UPLOAD_THREAD_SIZE, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread s = Executors.defaultThreadFactory().newThread(r);
            s.setDaemon(true);
            s.setName("DFS-UPLOAD-TASK-THREAD-" + s.getId());
            return s;
        }
    });

    private static HttpCoreClient instance = new HttpCoreClient();

    private TrackerClient trackerClient = null;

    private HttpCoreClient() {
        try {
            Properties props = new Properties();
            props.setProperty(ClientGlobal.PROP_KEY_TRACKER_SERVERS, config.getTrackers());
            props.setProperty(ClientGlobal.PROP_KEY_CHARSET, config.getCharset());
            props.setProperty(ClientGlobal.PROP_KEY_CONNECT_TIMEOUT_IN_SECONDS, config.getConnect_timeout_in_seconds());
            props.setProperty(ClientGlobal.PROP_KEY_NETWORK_TIMEOUT_IN_SECONDS, config.getNetwork_timeout_in_seconds());
            ClientGlobal.initByProperties(props);
        } catch (Exception e) {
            logger.error("init fastdfs error", e);
            System.exit(1);
        }
        trackerClient = new TrackerClient();
    }

    public static HttpCoreClient getInstance() {
        return instance;
    }

    public String getTrackersConfig() {
        if (StringUtils.isEmpty(tracker_servers)) {
            try {
                String trackerServersConf = config.getTrackers();
                tracker_servers = trackerServersConf.trim();
            } catch (Exception e) {
                logger.error("init error", e);
                System.exit(1);
            }
        }
        return tracker_servers;
    }

    /**
     * 分配服务端的链接
     *
     * @return
     * @throws MyException
     */
    private StorageClient1 assignResourse() throws MyException {
        return assignResourse(null);
    }

    /**
     * 分配服务端的链接
     *
     * @param fileId
     * @return
     * @throws MyException
     */
    private StorageClient1 assignResourse(String fileId) throws MyException {

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
     * 释放服务端的链接
     */
    private void releaseResourse(StorageClient1 client) {
        try {
            if (client != null) {
                logger.debug("release ：client->" + client + ",storageServer->" + client.getStorageServer()
                        + ",trackerServer->" + client.getTrackerServer());

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
        return uploadFile(file, null, null);
    }

    /**
     * 上传文件
     *
     * @param file      需要上传的文件
     * @param groupName 指定文件上传的组，可以为null
     * @return 返回fileId
     * @throws FileNotFoundException
     * @throws MyException
     */
    public String uploadFile(File file, String groupName) throws FileNotFoundException, MyException {
        return uploadFile(file, groupName, null);
    }

    /**
     * 上传文件
     *
     * @param file      需要上传的文件
     * @param groupName 指定文件上传的组（卷），为空表示不上指定
     * @param meta_list 文件额外属性
     * @return 返回fileId
     * @throws FileNotFoundException
     * @throws MyException
     */
    public String uploadFile(File file, String groupName, NameValuePair[] meta_list)
            throws FileNotFoundException, MyException {
        String fileName = file.getName();
        String extName = FilenameUtils.getExtension(fileName);
        return uploadFile(new FileInputStream(file), groupName, extName, meta_list);
    }

    public String uploadFile(InputStream in, String extName) throws MyException {
        return uploadFile(in, null, extName, null);
    }

    public String uploadFile(InputStream in, String groupName, String extName) throws MyException {
        return uploadFile(in, groupName, extName, null);
    }

    public String uploadFile(InputStream in, String extName, NameValuePair[] meta_list) throws MyException {
        return uploadFile(in, null, extName, meta_list);
    }

    /**
     * 上传文件
     *
     * @param in        需要上传的输入流
     * @param groupName 指定文件上传的组（卷），为空表示不指定
     * @param extName   文件扩展名(不能包含'.')
     * @param meta_list 文件额外属性
     * @return 返回fileId
     * @throws MyException
     */
    public String uploadFile(InputStream in, String groupName, String extName, NameValuePair[] meta_list)
            throws MyException {
        if (in == null) {
            throw new MyException("inputstream is null !");
        }
        StorageClient1 client = assignResourse();
        String fileId = null;
        try {
            long length = in.available();
            boolean isSetGroup = false;
            if (StringUtils.isNotEmpty(groupName)) {
                isSetGroup = true; // 上传到指定的组（卷）
            }
            if (length > NEED_BATCH_UPLOAD_SIZE) { // 超过指定大小就进行分批上传
                if (!(in instanceof BufferedInputStream)) {
                    in = new BufferedInputStream(in);
                }
                int readFlag = -1;
                byte[] bytes = new byte[UPLOAD_BUFFER_SIZE];
                int uploadCount = 0; // 已上传字节数
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
                    uploadCount += readFlag;
                    if (logger.isDebugEnabled()) {
                        logger.debug("uploaded:" + uploadCount);
                    }
                }
            } else {
                byte[] fileBytes = new byte[(int) length];
                in.read(fileBytes);
                if (isSetGroup) {
                    fileId = client.upload_appender_file1(groupName, fileBytes, extName, meta_list);
                } else {
                    fileId = client.upload_appender_file1(fileBytes, extName, meta_list);
                }
            }
        } catch (IOException e) {
            logger.error("upload file read file error!", e);
        } catch (MyException e) {
            logger.error(e.getMessage(), e);
            throw new MyException(e.getMessage());
        } catch (Exception e) {
            logger.error("upload file client error!", e);
            throw new MyException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
            releaseResourse(client);
        }

        return fileId;
    }


    /**
     * http下载文件
     *
     * @param fileId      上传成功后的fileId
     * @param response
     * @param direct      是否直接显示，true表示可以直接显示,false表示可以下载保存成文件(默认)
     * @param fileExtName 文件扩展名
     * @throws MyException
     * @throws IOException
     */
    public ErrorCode httpDownloadFile(String fileId, HttpServletResponse response, boolean direct, String fileExtName)
            throws MyException {
        StorageClient1 client = assignResourse(fileId);
        long fileLength = -1L;
        try {
            if (fileLength < 0) {
                FileInfo fileInfo = client.get_file_info1(fileId);
                if (fileInfo != null) {
                    fileLength = fileInfo.getFileSize();
                }
            }
            if (fileLength < 0) {
                return ErrorCode.RESOURCE_NOT_FOUND;
            } else {
                response.setContentLengthLong(fileLength); //这个必须在response.getOutputStream().write()之前调用
                if (!direct) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    StringBuilder sb = new StringBuilder(128);
                    // file可以下载
                    String fileName = sb.append(format.format(new Date())).append(".").append(fileExtName).toString();
                    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, UTF8));
                }
            }
            int bufferSize = DOWNLOAD_BUFFER_SIZE;
            if (fileLength > 10 * 1024 * 1024) {
                bufferSize = 1024 * 1024; // 文件大于10M 将缓存设为1M
            }
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream(), bufferSize);
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
                if (logger.isDebugEnabled()) {
                    logger.debug("fileLeftBytes:" + fileLength);
                }
            }
        } catch (MyException e) {
            logger.error(e.getMessage(), e);
            throw new MyException(e.getMessage());
        } catch (Exception e) {
            logger.error("download file error ! fileId:" + fileId, e);
            throw new MyException("download file error !");
        } finally {
            releaseResourse(client);
        }
        return ErrorCode.OK;
    }

    /**
     * 删除文件
     *
     * @param fileId
     * @return 0 成功,2文件不存在,其他值失败
     * @throws IOException
     * @throws MyException
     */
    public int deleteFile(String fileId) {
        StorageClient1 client = null;
        try {
            client = assignResourse(fileId);
            return client.delete_file1(fileId);
        } catch (Exception e) {
            logger.error("delete file error! fileId:" + fileId, e);
            return -1;
        } finally {
            releaseResourse(client);
        }
    }

    /**
     * 提交一个上传任务<br>
     * 异步执行
     *
     * @param fileInfoId 文件信息对应的id,上传成功后用于更新文件信息
     * @param groupName  上传到的组名 与fastdfs组对应
     * @param in         输入流
     * @param extName    文件扩展名
     */
    public void executeUploadTask(Integer fileInfoId, InputStream in, String groupName, String extName) {
        DFSUploadFileTask task = new DFSUploadFileTask(fileInfoId, in, groupName, extName);
        uploadExecutorService.execute(task);
    }

    // /**
    // * 提交一个下载任务<br>
    // * 异步执行
    // *
    // * @param fileId
    // * 文件id
    // * @param out
    // * 输出流
    // * @param isClose
    // * 是否在下载完成后关闭输出流
    // */
    // public void executeDownloadTask(String fileId, OutputStream out, boolean
    // isClose) {
    // DFSDownloadFileTask task = new DFSDownloadFileTask(fileId, out, isClose);
    // downloadExecutorService.execute(task);
    // }
}
