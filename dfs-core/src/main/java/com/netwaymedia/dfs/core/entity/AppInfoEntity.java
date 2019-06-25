package com.netwaymedia.dfs.core.entity;

import java.io.Serializable;

import com.netwaymedia.dfs.base.BaseEntity;

/**
 * 第三方应用信息
 *
 */
@SuppressWarnings("serial")
public class AppInfoEntity extends BaseEntity implements Serializable {
	public static final int APP_STATUS_OK = 1; // 启用
	public static final int APP_STATUS_STOP = 2;// 停用

	/**
	 * 应用唯一编码
	 */
	private String appKey;

	/**
	 * 应用密钥
	 */
	private String appSecret;

	/**
	 * 可以上传的组编号与fastdfs的组名对应
	 */
	private String groupName;

	/**
	 * 状态 1:启用,2:停用
	 */
	private Integer status;

	/**
	 * 创建者
	 */
	private Integer createBy;

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
