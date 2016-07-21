package server.data;

//用户信息类
public class User implements Cloneable {
	// 用户名信息
	private String name;
	public int ID;// 标记

	// 服务器端口
	private int textPort;
	private int videoPort;
	private int audioPort;

	// 服务器IP
	private String serverIP;// 服务器IP
	private String groupIP;// 组播IP

	public User() {
	}

	public User(String name) {
		this.name = name;
	};
	public User(String name,int ID) {
		this.name = name;
		this.ID = ID;
	};
	public User(String name, String groupIP, String serverIP, int textPort,
			int videoPort, int audioPort, int ID) {
		this.name = name;
		this.textPort = textPort;
		this.videoPort = videoPort;
		this.audioPort = audioPort;
		this.serverIP = serverIP;
		this.groupIP = groupIP;
		this.ID = ID;
	}

	public User(String name, String groupIP, int videoPort, int ID) {
		this.name = name;
		this.videoPort = videoPort;
		this.groupIP = groupIP;
		this.ID = ID;
	}

	public Object clone() {
		User u = null;
		try {
			u = (User) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return u;
	}

	public boolean equals(Object obj) {
		if (obj instanceof User) {
			if (((User) obj).name.equals(name))
				return true;
		}
		return false;
	}

	public int hashCode() {
		return this.ID;
	}

	// 字段函数
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupIP() {
		return groupIP;
	}

	public String getServerIP() {
		return serverIP;
	}

	public int getTextPort() {
		return textPort;
	}

	public int getVideoPort() {
		return videoPort;
	}

	public int getAudioPort() {
		return audioPort;
	}

}
