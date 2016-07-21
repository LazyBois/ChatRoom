package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import client.UI.ClientUI;
import client.data.User;

public class Client {

	// �������˿�
	private int textPort;
	private int videoPort;
	private int audioPort;
	private int publicPort;
	// ������IP
	private String serverIP;// ������IP
	private String publicIP;// ����ͨ��IP

	private User user;// �˿ͻ���Ϊ���û�����
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
				if (JOptionPane.showConfirmDialog(userUI, "ȷ���˳�����?",
						"Exit  Confirm", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					close();
					System.exit(0);
				}
			}
		});

		while (true) {
			userName = JOptionPane.showInputDialog(userUI, "�����û���", "�û���",
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
		test.initUI();// ��ʼ��UI
		test.start();// �����ͻ����߳�
	}
}
