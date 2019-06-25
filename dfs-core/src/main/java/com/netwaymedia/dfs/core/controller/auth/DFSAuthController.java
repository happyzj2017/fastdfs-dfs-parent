package com.netwaymedia.dfs.core.controller.auth;

import com.netwaymedia.dfs.base.BaseConntroller;
import com.netwaymedia.dfs.base.ErrorCode;
import com.netwaymedia.dfs.base.cache.CacheService;
import com.netwaymedia.dfs.core.entity.AppInfoEntity;
import com.netwaymedia.dfs.core.entity.FileInfoEntity;
import com.netwaymedia.dfs.core.fastdfs.HttpCoreClient;
import com.netwaymedia.dfs.core.service.FileInfoService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;

/**
 * 需要鉴权的接口
 * 
 */
@RequestMapping(value = "/dfs/auth")
@RestController
@Deprecated
public class DFSAuthController extends BaseConntroller {

	@Autowired
	private FileInfoService fileInfoService;

	/**
	 * APP上传私有文件 建议10M以类的文件使用http接口上传
	 * 
	 * @param file
	 * @param request
	 * @return
	 */
//	@RequestMapping(value = "/v1/upload/self", method = RequestMethod.POST)
	public @ResponseBody String uploadToSelf(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		int fileInfoId = -1;
		try {
			String fileName = file.getOriginalFilename();

			String appKey = request.getHeader(HEADER_APP_KEY);
			// 添加文件信息，返回最插的id
			fileInfoId = fileInfoService.addFileInfo(appKey, FileInfoEntity.FILE_ACCESS_TYPE_BELONGS_AUTH, fileName,
					file.getSize());
			AppInfoEntity appInfo = CacheService.APP_INFO_CACHE.get(appKey);
			BufferedInputStream bis = new BufferedInputStream(file.getInputStream());
			HttpCoreClient.getInstance().executeUploadTask(fileInfoId, bis, appInfo.getGroupName(),
					FilenameUtils.getExtension(fileName));
		} catch (Exception e) {
			logger.error("upload file error!", e);
			return getResponseByCode(ErrorCode.SERVER_ERROR);
		}
		// 返回json
		if (fileInfoId > 0) {
			String body = "{\"id\":" + fileInfoId + "}";
			return getResponseOKWithBody(body);
		} else {
			return getResponseByCode(ErrorCode.SERVER_ERROR);
		}
	}

	/**
	 * 下载
	 * 
	 * @param fileId
	 * @param response
	 */

//	@RequestMapping(value = "/v1/download", method = RequestMethod.GET)
	public void downloadFile(@RequestParam("fileId") String fileId, HttpServletRequest request,
			HttpServletResponse response) {
	}

}
