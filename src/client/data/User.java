package client.data;

//用户信息类
public class User implements Cloneable {
	// 用户名信息
	public String name;
	public int ID;// 身份标记

	// 服务器端口
	public int textPort;
	public int videoPort;
	public int audioPort;
	public int publicPort;

	// 服务器IP
	public String serverIP;// 服务器IP
	public String publicIP;// 公用通信IP
	public String groupIP;// 组播IP

	public User() {
	}

	public User(String name) {
		this.name = name;
	}

	public User(String name, int ID) {
		this.name = name;
		this.ID = ID;

	}

	public User(String name, String serverIP, String publicIP, int textPort,
			int videoPort, int audioPort, int publicPort) {
		this.name = name;
		this.textPort = textPort;
		this.videoPort = videoPort;
		this.audioPort = audioPort;
		this.publicPort = publicPort;
		this.serverIP = serverIP;
		this.publicIP = publicIP;
		ID = 0;
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
}
