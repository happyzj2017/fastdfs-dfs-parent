package com.netwaymedia.dfs.core.controller;

import com.netwaymedia.dfs.base.BaseConntroller;
import com.netwaymedia.dfs.base.ErrorCode;
import com.netwaymedia.dfs.core.entity.AppInfoEntity;
import com.netwaymedia.dfs.core.entity.FileInfoEntity;
import com.netwaymedia.dfs.core.fastdfs.HttpCoreClient;
import com.netwaymedia.dfs.core.service.AppInfoService;
import com.netwaymedia.dfs.core.service.FileInfoService;
import com.netwaymedia.dfs.utils.MimeUtils;
import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.StorageClient1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * DFS相关http接口
 */
@RequestMapping(value = "/dfs")
@RestController
public class DFSController extends BaseConntroller {
    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private AppInfoService appInfoService;

    /**
     * 上传文件信息
     *
     * @param appKey
     * @param fileName
     * @param fileLength
     */
    @RequestMapping(value = "/v1/sUpload")
    public @ResponseBody
    String startUpload(@RequestParam("appKey") String appKey,
                       @RequestParam("fileName") String fileName, @RequestParam("fileLength") Long fileLength) {
        int fileInfoId = -1;
        try {
            fileInfoId = fileInfoService.addFileInfo(appKey, FileInfoEntity.FILE_ACCESS_TYPE_NO_AUTH, fileName,
                    fileLength);
        } catch (Exception e) {
            logger.error("add file info error !", e);
        }
        // 返回json
        if (fileInfoId > 0) {
            String body = "{\"fileInfoId\":" + fileInfoId + "}";
            return getResponseOKWithBody(body);
        } else {
            return getResponseByCode(ErrorCode.SERVER_ERROR);
        }
    }

    /**
     * 更新fileId
     *
     * @param fileInfoId
     * @param fileId
     */
    @RequestMapping(value = "/v1/eUpload")
    public void endUpload(@RequestParam("fileInfoId") Integer fileInfoId, @RequestParam("fileId") String fileId) {
        // 上传成功执行一些保存fileId与业务数据映射关系的操作
        if (fileId != null) {
            FileInfoEntity fileInfo = new FileInfoEntity();
            int pos = fileId.indexOf(StorageClient1.SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR);
            if (pos > 0) {
                String groupName = fileId.substring(0, pos);
                fileInfo.setGroupName(groupName);
            }
            fileInfo.setId(fileInfoId);
            fileInfo.setFileId(fileId);
            fileInfo.setUpdateDate(new Date());
            fileInfo.setStatus(FileInfoEntity.FILE_STATUS_UPLOADED);
            fileInfoService.updateFileInfoById(fileInfo);
        }
    }

    /**
     * 获取服务器信息
     *
     * @param appKey
     */
    @RequestMapping(value = "/v1/server")
    public @ResponseBody
    String server(@RequestParam("appKey") String appKey) {
        String tracker_servers = HttpCoreClient.getInstance().getTrackersConfig();
        AppInfoEntity app = appInfoService.getAppInfo(appKey);
        String groupName = null;
        String body = null;
        if (app != null) {
            groupName = app.getGroupName();
            body = "{\"trackerServers\":\"" + tracker_servers + "\",\"groupName\":\"" + groupName + "\"}";
            return getResponseOKWithBody(body);
        } else {
            return getResponseByCode(ErrorCode.APP_NOT_EXIST);
        }
    }

    /**
     * 下载
     *
     * @param fileId   fastdfs返回的fileId
     * @param direct   是否直接显示，true表示可以直接显示,false表示可以下载保存成文件(默认)
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/v1/download", method = RequestMethod.GET)
    public void downloadFile(@RequestParam("fileId") String fileId, boolean direct, HttpServletResponse response)
            throws IOException {
        BufferedOutputStream bos = null;
        try {
            response.setCharacterEncoding(UTF8);
            response.setHeader("Connection", "close"); //注意区分大小写
            String fileExtName = FilenameUtils.getExtension(fileId);
            String contextType = MimeUtils.guessMimeTypeFromExtension(fileExtName);
            if (contextType != null) {
                response.setContentType(contextType);
            }
            ErrorCode eCode = HttpCoreClient.getInstance().httpDownloadFile(fileId, response, direct, fileExtName);
            if (eCode != ErrorCode.OK) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("download file error ! fileId:" + fileId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
        }
    }

    /**
     * 删除指定文件
     *
     * @param fileId   fastdfs返回的fileId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/v1/del")
    public void deleteFile(@RequestParam("fileId") String fileId, HttpServletResponse response) throws IOException {
        try {
            FileInfoEntity fileInfo = new FileInfoEntity();
            fileInfo.setFileId(fileId);
            fileInfo.setUpdateDate(new Date());
            fileInfo.setStatus(FileInfoEntity.FILE_STATUS_DELETED);
            fileInfoService.updateFileInfoByFileId(fileInfo);
        } catch (Exception e) {
            logger.error("delete file data error!", e);
        }
    }

}
