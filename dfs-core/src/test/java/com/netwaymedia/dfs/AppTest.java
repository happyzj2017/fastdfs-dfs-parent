package com.netwaymedia.dfs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.csource.fastdfs.StorageClient1;

import com.netwaymedia.dfs.utils.MD5Utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public final String HEADER_APP_KEY = "dfs-request-app-key"; // 应用appKey
	public final String HEADER_TIMESTAMP = "dfs-request-timestamp"; // 时间戳
	public final String HEADER_SIGN = "dfs-request-sign"; // 签名

	String appKey = "ofweek";
	String appSecret = "ofweek_secret";

	//String fileAbsPath = "d:\\EXH_AUDIENCE_REGISTER_OLD.sql";
	String fileAbsPath = "d:\\全程回放.flv";
	
	String fileId = "group1/M00/00/00/wKgCe1nO7wyEdI1LAAAAAEnsH28091.exe";

	/**
	 * Rigourous Test :-)
	 */
	final String newLine = "\r\n";

	public void testUpload() {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		BufferedReader reader = null;
		HttpURLConnection conn = null;
		try {
			// 换行符
			final String boundaryPrefix = "--";
			// 定义数据分隔线
			String BOUNDARY = "========7d4a6d158c9";
			// 服务器的域名
			URL url = new URL("http://localhost:8881/dfs/auth/v1/upload/self");
			conn = (HttpURLConnection) url.openConnection();
			// 设置为POST情
			conn.setRequestMethod("POST");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setChunkedStreamingMode(0);

			File file = new File(fileAbsPath);
			FileInputStream fis = new FileInputStream(file);
			// 设置请求头参数
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-Length", String.valueOf(fis.available()));
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

			// -------------开始设置鉴权参数-------------
			String timestamp = String.valueOf(new Date().getTime());
			conn.setRequestProperty(HEADER_APP_KEY, appKey);
			conn.setRequestProperty(HEADER_TIMESTAMP, timestamp);
			conn.setRequestProperty(HEADER_SIGN, MD5Utils.md5(appendSeq(appKey, appSecret, timestamp)));
			// -------------结束设置鉴权参数---------------

			// 上传文件
			out = new BufferedOutputStream(conn.getOutputStream());
			StringBuilder sb = new StringBuilder();
			sb.append(boundaryPrefix);
			sb.append(BOUNDARY);
			sb.append(newLine);
			// 文件参数,photo参数名可以随意修改
			sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + newLine);
			// 参数头设置完以后需要两个换行，然后才是参数内容
			sb.append(newLine);

			// 将参数头的数据写入到输出流中
			out.write(sb.toString().getBytes("UTF-8"));
			out.flush();

			// 数据输入流,用于读取文件数据
			in = new BufferedInputStream(fis);
			byte[] bufferOut = new byte[1 * 1024 * 1024];
			int bytes = 0;
			// 每次读1MB数据,并且将文件数据写入到输出流中
			while ((bytes = in.read(bufferOut)) > 0) {
				out.write(bufferOut, 0, bytes);
				out.flush();
			}
			// 最后添加换行
			out.write(newLine.getBytes("UTF-8"));
			// 定义最后数据分隔线，即--加上BOUNDARY再加上--。
			byte[] end_data = (boundaryPrefix + BOUNDARY + boundaryPrefix + newLine).getBytes("UTF-8");
			// 写上结尾标识
			out.write(end_data);
			out.flush();
			int status = conn.getResponseCode();
			if (status == 200) { // 不等于 200个表示异常
				// 定义BufferedReader输入流来读取URL的响应
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
			} else {
				
			}
		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(reader);
		}
	}

	public void testDwonload() {
		BufferedOutputStream fos = null;
		BufferedInputStream bis = null;
		BufferedReader reader = null;
		try {
			String urlstr = "http://localhost:8881/dfs/auth/v1/download";
			String reqParams = "fileId=" + URLEncoder.encode(fileId, "UTF-8");
			URL url = new URL(urlstr + "?" + reqParams);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 设置为GET
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求头参数
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Charsert", "UTF-8");

			// -------------开始设置鉴权参数-------------
			String timestamp = String.valueOf(new Date().getTime());
			conn.setRequestProperty(HEADER_APP_KEY, appKey);
			conn.setRequestProperty(HEADER_TIMESTAMP, timestamp);
			conn.setRequestProperty(HEADER_SIGN, MD5Utils.md5(appendSeq(appKey, appSecret, timestamp)));
			// -------------结束设置鉴权参数---------------

			File fout = new File("f:/test/"
					+ fileId.substring(fileId.lastIndexOf(StorageClient1.SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR) + 1));
			if (fout.exists()) {
				fout.delete();
			}
			int status = conn.getResponseCode();
			if (status == 200) { // 不等于 200个表示异常
				fos = new BufferedOutputStream(new FileOutputStream(fout));
				bis = new BufferedInputStream(conn.getInputStream());
				int readFlag = -1;
				byte[] buffer = new byte[1 * 1024 * 1024];
				while ((readFlag = bis.read(buffer)) > 0) {
					fos.write(buffer, 0, readFlag);
					fos.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(fos);
			IOUtils.closeQuietly(reader);
		}
	}

	private String appendSeq(String appKey, String appSecret, String timestamp) {
		StringBuilder seq = new StringBuilder(256);
		seq.append(appKey);
		seq.append("$");
		seq.append(appSecret);
		seq.append("$");
		seq.append(timestamp);

		return seq.toString();
	}
}
