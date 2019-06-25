package com.netwaymedia.dfs.base;

import java.util.Date;

public class BaseEntity {
	/**
	 * 创建日期
	 */
	protected Date createDate;

	/**
	 * 修改日期
	 */
	protected Date updateDate;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}
