package com.netwaymedia.dfs.core.entity;

import java.io.Serializable;

import com.netwaymedia.dfs.base.BaseEntity;

/**
 * 文件信息
 *
 */
@SuppressWarnings("serial")
public class FileInfoEntity extends BaseEntity implements Serializable {
	/**
	 * 1:所属应用 + 鉴权访问
	 */
	public static final int FILE_ACCESS_TYPE_BELONGS_AUTH = 1;
	/**
	 * 2:鉴权访问
	 */
	public static final int FILE_ACCESS_TYPE_AUTH = 2;

	/**
	 * 3:无鉴权访问
	 */
	public static final int FILE_ACCESS_TYPE_NO_AUTH = 3;

	/**
	 * 1:创建成功
	 */
	public static final int FILE_STATUS_CREATED = 1;

	/**
	 * 2:上传成功
	 */
	public static final int FILE_STATUS_UPLOADED = 2;

	/**
	 * 3:已删除
	 */
	public static final int FILE_STATUS_DELETED = 3;

	private Integer id;

	private String fileId; // fastdfs返回的fileId

	private String name; // 原始文件名

	private long bytes; // 文件字节数

	private String groupName; // 所存放的组编号与fastdfs的组名对应

	private Integer accessType; // 访问类型 1:所属应用鉴权访问,2:所有应用鉴权访问,3:无鉴权访问

	private String belongsApp; // 所属应用的编码

	private Integer status; // 状态 1:创建成功,2:上传成功,3:已删除

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public long getBytes() {
		return bytes;
	}

	public void setBytes(long bytes) {
		this.bytes = bytes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getAccessType() {
		return accessType;
	}

	public void setAccessType(Integer accessType) {
		this.accessType = accessType;
	}

	public String getBelongsApp() {
		return belongsApp;
	}

	public void setBelongsApp(String belongsApp) {
		this.belongsApp = belongsApp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
