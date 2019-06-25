package com.netwaymedia.dfs.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.netwaymedia.dfs.core.entity.AppInfoEntity;

/**
 * 应用mapper
 * 
 */
public interface AppInfoMapper {
	
	/**
	 * 查询出所有应用信息
	 * 
	 */
	List<AppInfoEntity> getAllAppInfo();
	
	/**
	 * 查询出指定应用信息
	 * 
	 */
	AppInfoEntity getAppInfoByAppKey(@Param("appKey")String appKey);
}
