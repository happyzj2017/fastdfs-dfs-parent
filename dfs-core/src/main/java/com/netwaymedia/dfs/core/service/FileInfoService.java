package com.netwaymedia.dfs.core.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netwaymedia.dfs.base.BaseService;
import com.netwaymedia.dfs.base.cache.CacheService;
import com.netwaymedia.dfs.core.entity.AppInfoEntity;
import com.netwaymedia.dfs.core.entity.FileInfoEntity;
import com.netwaymedia.dfs.core.mapper.FileInfoMapper;

/**
 * 文件信息服务类
 */
@Service
public class FileInfoService extends BaseService {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    /**
     * 添加文件信息
     *
     * @return
     */
    public int addFileInfo(String appKey, int accessType, String fileName, long fileLength) {
        AppInfoEntity app = CacheService.APP_INFO_CACHE.get(appKey);
        FileInfoEntity fileInfo = new FileInfoEntity();
        fileInfo.setName(fileName);
        fileInfo.setBytes(fileLength);
        fileInfo.setGroupName(app.getGroupName());
        fileInfo.setAccessType(accessType);
        fileInfo.setBelongsApp(appKey);
        fileInfo.setStatus(FileInfoEntity.FILE_STATUS_CREATED);
        Date now = new Date();
        fileInfo.setCreateDate(now);
        fileInfo.setUpdateDate(now);
        fileInfoMapper.addFileInfo(fileInfo);
        return fileInfo.getId();
    }

    /**
     * 按fileInfoId更新fileInfo必须设置id字段
     *
     * @param fileInfo
     */
    public void updateFileInfoById(FileInfoEntity fileInfo) {
        fileInfoMapper.updateFileInfoById(fileInfo);
    }

    /**
     * 按fileId更新fileInfo必须设置fileId字段
     *
     * @param fileInfo
     */
    public void updateFileInfoByFileId(FileInfoEntity fileInfo) {
        fileInfoMapper.updateFileInfoByFileId(fileInfo);
    }

    /**
     * 获取文件长度
     *
     * @param fileId
     * @return
     */
    public long getFileLengthByFIleId(String fileId) {
        return fileInfoMapper.getFileLengthByFIleId(fileId);
    }
}
