package client.data;

//�û���Ϣ��
public class User implements Cloneable {
	// �û�����Ϣ
	public String name;
	public int ID;// ��ݱ��

	// �������˿�
	public int textPort;
	public int videoPort;
	public int audioPort;
	public int publicPort;

	// ������IP
	public String serverIP;// ������IP
	public String publicIP;// ����ͨ��IP
	public String groupIP;// �鲥IP

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
