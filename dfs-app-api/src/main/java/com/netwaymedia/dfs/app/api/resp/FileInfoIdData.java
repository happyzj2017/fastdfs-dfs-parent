package com.netwaymedia.dfs.app.api.resp;

public class FileInfoIdData extends BaseResp{

	private FileInfoId body;

	public FileInfoId getBody() {
		return body;
	}

	public void setBody(FileInfoId body) {
		this.body = body;
	}

	public class FileInfoId {
		public Integer fileInfoId;

		public Integer getFileInfoId() {
			return fileInfoId;
		}

		public void setFileInfoId(Integer fileInfoId) {
			this.fileInfoId = fileInfoId;
		}

	}

}
