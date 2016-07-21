package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import client.UI.ClientUI;
import client.data.User;

public class Client {

	// 服务器端口
	private int textPort;
	private int videoPort;
	private int audioPort;
	private int publicPort;
	// 服务器IP
	private String serverIP;// 服务器IP
	private String publicIP;// 公用通信IP

	private User user;// 此客户端为此用户服务
	private String userName = null;
	private Control clientControl;
	private ClientUI userUI;

	public Client() {
		textPort = 5880;
		videoPort = 6881;
		audioPort = videoPort + 2;
		publicPort = 10000;

		serverIP = "127.0.0.1";
		publicIP = "239.255.6.1";
	}

	public void start() {
		clientControl = new Control(user, userUI);
		clientControl.start();
	}

	public void close() {
		clientControl.kill();
	}

	public void initUI() {
		userUI = new ClientUI();
		userUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		userUI.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				if (JOptionPane.showConfirmDialog(userUI, "确定退出程序?",
						"Exit  Confirm", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					close();
					System.exit(0);
				}
			}
		});

		while (true) {
			userName = JOptionPane.showInputDialog(userUI, "输入用户名", "用户名",
					JOptionPane.WARNING_MESSAGE);

			if (userName == null) {
				System.exit(0);
			} else if (userName.equals("")) {
				continue;
			} else if (!userName.equals("")) {
				break;
			}
		}

		user = new User(userName, serverIP, publicIP, textPort, videoPort,
				audioPort, publicPort);
	}

	public static void main(String[] args) {
		Client test = new Client();
		test.initUI();// 初始化UI
		test.start();// 开启客户端线程
	}
}
