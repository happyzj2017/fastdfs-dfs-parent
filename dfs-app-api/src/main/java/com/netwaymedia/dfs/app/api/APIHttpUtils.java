package com.netwaymedia.dfs.app.api;

import com.netwaymedia.dfs.app.api.resp.BaseResp;
import com.netwaymedia.dfs.app.api.resp.FileInfoIdData;
import com.netwaymedia.dfs.app.api.resp.ServerData;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.csource.common.MyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIHttpUtils {
    private static final String utf8 = "utf-8";
    private static final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000)
            .setConnectTimeout(30000).build();//设置请求和传输超时时间

    /**
     * 获取ServerData
     *
     * @param url
     * @param appKey
     * @return
     * @throws MyException
     */
    static ServerData getServerInfo(String url, String appKey) throws MyException {
        ServerData data = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            String result = null;
            HttpPost request = new HttpPost(url);// 这里发送请求
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("appKey", appKey));
            request.setEntity(new UrlEncodedFormEntity(params));
            request.setConfig(requestConfig);
            // 获取当前客户端对象
            httpClient = HttpClients.createDefault();
            // 通过请求对象获取响应对象
            response = httpClient.execute(request);
            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), utf8);
                data = GsonUtils.parseObject(result, ServerData.class);
                if (data.getResult() != BaseResp.RESP_OK) {
                    throw new MyException("get error resp from core server on " + url + ", resp:" + result);
                }
            }
        } catch (Exception e) {
            throw new MyException(e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                }
            }
        }
        return data;
    }

    /**
     * 开始上传文件，上报core server
     *
     * @param url
     * @param appKey
     * @param fileName
     * @param fileLength
     * @return
     */
    static Integer startUpload(String url, String appKey, String fileName, long fileLength) throws MyException {
        Integer fileInfoId = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            String result = null;
            HttpPost request = new HttpPost(url);// 这里发送请求
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("appKey", appKey));
            params.add(new BasicNameValuePair("fileName", fileName));
            params.add(new BasicNameValuePair("fileLength", String.valueOf(fileLength)));
            request.setEntity(new UrlEncodedFormEntity(params, CharsetUtils.get(utf8)));
            request.setConfig(requestConfig);
            // 获取当前客户端对象
            httpClient = HttpClients.createDefault();
            // 通过请求对象获取响应对象
            response = httpClient.execute(request);
            // 判断网络连接状态码是否正常
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), utf8);
                FileInfoIdData data = GsonUtils.parseObject(result, FileInfoIdData.class);
                if (data.getResult() == BaseResp.RESP_OK) {
                    fileInfoId = data.getBody().getFileInfoId();
                } else {
                    throw new MyException("get error from  core server on " + url + ", resp:" + result);
                }
            }
        } catch (Exception e) {
            throw new MyException(e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                }
            }
        }

        return fileInfoId;
    }

    /**
     * 结束上传文件，上报core server
     *
     * @param url
     * @param fileId
     * @param fileInfoId
     * @return
     */
    static void endUpload(String url, String fileId, Integer fileInfoId) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            HttpPost request = new HttpPost(url);// 这里发送请求
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("fileId", fileId));
            params.add(new BasicNameValuePair("fileInfoId", String.valueOf(fileInfoId)));
            request.setEntity(new UrlEncodedFormEntity(params, CharsetUtils.get(utf8)));
            request.setConfig(requestConfig);
            // 获取当前客户端对象
            httpClient = HttpClients.createDefault();
            // 通过请求对象获取响应对象
            response = httpClient.execute(request);
            // 判断网络连接状态码是否正常
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 删除文件
     *
     * @param url
     * @param fileId
     * @return
     */
    static void delete(String url, String fileId) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            HttpPost request = new HttpPost(url);// 这里发送请求
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("fileId", fileId));
            request.setEntity(new UrlEncodedFormEntity(params, CharsetUtils.get(utf8)));
            // 获取当前客户端对象
            httpClient = HttpClients.createDefault();
            // 通过请求对象获取响应对象
            response = httpClient.execute(request);
            // 判断网络连接状态码是否正常
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
