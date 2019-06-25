package com.dfs.test;

import com.netwaymedia.dfs.app.api.APIConfigure;
import com.netwaymedia.dfs.app.api.DFSAppClient;
import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class DfsAPITest {

    /**
     * SDK初始化
     *
     * @throws MyException
     */
    @BeforeClass
    public static void init() throws MyException {
        // 必须先完成初始化，才能做其他操作,只需要初始化一次
        APIConfigure config = new APIConfigure("lived", "http://192.168.2.123:8881/");
//        APIConfigure config = new APIConfigure("lived", "http://192.168.2.123:8881/",
//                1024 * 1024, 1024 * 1024, 10);
        DFSAppClient.instance().initAPIConfigure(config);
    }

    /**
     * 测试SDK上传
     *
     * @throws MyException
     * @throws FileNotFoundException
     */
    @Test
    public void testAUpload() throws FileNotFoundException, MyException, InterruptedException {
        String fileId = DFSAppClient.instance().uploadFile(new File("d://中文测试.jpg"));
        Assert.assertNotNull(fileId);
        Thread.sleep(5000); //上报过程是异步执行。等待异步执行完成
    }

    /**
     * 测试SDK下载
     *
     * @throws MyException
     * @throws FileNotFoundException
     */
    @Test
    public void testDownload() throws MyException, FileNotFoundException, InterruptedException {
        String fileId = "group2/M00/00/00/wKgCe1ndyhSAVbFQAAA4-IRNuzo277.jpg";
        File f = new File("f://test//test2." + FilenameUtils.getExtension(fileId));
        FileOutputStream fos = new FileOutputStream(f);
        DFSAppClient.instance().downloadFile(fileId, fos, true);
        Thread.sleep(5000);//上报过程是异步执行。等待异步执行完成
    }

    /**
     * 测试删除文件
     *
     * @throws MyException
     */
    @Test
    public void testDelete() throws MyException, InterruptedException {
        String fileId = "group2/M00/00/00/wKgCe1ndya-AeVJDAAA4-IRNuzo173.jpg";
        int result = DFSAppClient.instance().deleteFile(fileId);
        Assert.assertEquals(0, result);
        Thread.sleep(5000);//上报过程是异步执行。等待异步执行完成
    }

    // public static void main(String[] args) {
    // APIConfigure config = new APIConfigure("ofweek", "http://localhost:8881/");
    // File f = null;
    // try {
    // long st = System.currentTimeMillis();
    // DFSAppClient.instance().initAPIConfigure(config); //
    // 必须先完成初始化，才能做其他操作,只需要初始化一次
    // System.out.println("http init time:" + (System.currentTimeMillis() - st));
    // long ust = System.currentTimeMillis();
    // String fileId = DFSAppClient.instance().uploadFile(new
    // File("d://rj_yt1992.exe"));
    // System.out.println("upload time:" + (System.currentTimeMillis() - ust));
    // long dst = System.currentTimeMillis();
    // // String fileId = "group1/M00/00/00/wKgCe1na1vCEUdruAAAAABYqHQ8949.exe";
    // System.out.println("fileId=" + fileId);
    // f = new File("f://test//rj_yt1992." + FilenameUtils.getExtension(fileId));
    // if (f.exists()) {
    // f.delete();
    // }
    // FileOutputStream fos = new FileOutputStream(f);
    // DFSAppClient.instance().downloadFile(fileId, fos, true);
    // System.out.println("downdload time:" + (System.currentTimeMillis() - dst));
    // System.out.println("----------------------------");
    // System.out.println("total time:" + (System.currentTimeMillis() - st));
    // } catch (Exception e) {
    // e.printStackTrace();
    // if (f != null) {
    // if (f.exists()) {
    // f.delete();
    // }
    // }
    // }
    // }
}
