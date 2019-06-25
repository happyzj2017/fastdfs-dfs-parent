DROP TABLE IF EXISTS `dfs_app_info`;
CREATE TABLE `dfs_app_info` (
  `app_key` varchar(128) NOT NULL COMMENT '应用唯一编码',
  `app_secret` varchar(128) NOT NULL COMMENT '应用密钥',
  `group_name` varchar(32) NOT NULL COMMENT '可以上传的组编号与fastdfs的组名对应',
  `status` int(1) NOT NULL COMMENT '状态 1:启用,2:停用',
  `create_by` int(9) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`app_key`),
  UNIQUE KEY `dfs_app_info_appkey_index` (`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='第三方应用信息';

DROP TABLE IF EXISTS `dfs_file_info`;
CREATE TABLE `dfs_file_info` (
  `id` int(9) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `file_id` varchar(128) DEFAULT NULL COMMENT 'fastdfs返回的fileId',
  `name` varchar(256) DEFAULT NULL COMMENT '原始文件名',
  `bytes` int(9) DEFAULT NULL COMMENT '文件长度',
  `group_name` varchar(32) NOT NULL COMMENT '所在组编号与fastdfs的组名对应',
  `access_type` int(1) NOT NULL COMMENT '访问类型 1:所属应用鉴权访问,2:所有应用鉴权访问,3:无鉴权访问',
  `belongs_app` varchar(128) NOT NULL COMMENT '所属应用的编码',
  `status` int(1) NOT NULL COMMENT '状态 1:创建成功,2:上传成功,3:已删除',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dfs_file_info_id_index` (`id`),
  INDEX `dfs_file_info_fileId_index` (`file_id`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件信息';