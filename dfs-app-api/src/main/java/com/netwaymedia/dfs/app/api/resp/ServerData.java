package com.netwaymedia.dfs.app.api.resp;

public class ServerData extends BaseResp {

	private ServerDataBody body;

	public ServerDataBody getBody() {
		return body;
	}

	public void setBody(ServerDataBody body) {
		this.body = body;
	}

	public class ServerDataBody {
		private String trackerServers; // tracker 服务器可以是多个 如:10.0.11.201:22122,10.0.11.202:22122,10.0.11.203:22122

		private String groupName; // 当前appKey对应的组

		public String getTrackerServers() {
			return trackerServers;
		}

		public void setTrackerServers(String trackerServers) {
			this.trackerServers = trackerServers;
		}

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
	}

}
