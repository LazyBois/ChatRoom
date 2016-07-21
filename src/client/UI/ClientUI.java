package client.UI;

import java.awt.Component;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class ClientUI extends JFrame {

	// 控制器
	public JButton sender;
	public JButton connect;
	public JButton close;

	// 视图
	private JTextArea sendText;
	private JTextArea receiveText;
	private JList memberList;
	private JPanel panel_1;
	private DefaultListModel listModel;
	private JLabel numberMember;
	// 组件标记
	private int len;
	private boolean visited[];

	public ClientUI() {
		len = 4;
		visited = new boolean[len];
		for (int i = 0; i < len; i++) {
			visited[i] = false;
		}

		initUI();
		setUI();
	}

	public void setUI() {
		setBounds(200, 200, 849, 509);
		setVisible(true);
	}

	public void initUI() {
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 167, 470);
		getContentPane().add(panel);
		panel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 23, 147, 437);
		panel.add(scrollPane);

		listModel = new DefaultListModel();
		memberList = new JList(listModel);
		scrollPane.setViewportView(memberList);

		numberMember = new JLabel("在线人数");
		numberMember.setBounds(10, 0, 157, 26);
		panel.add(numberMember);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(631, 0, 201, 470);
		getContentPane().add(panel_2);
		panel_2.setLayout(null);

		sender = new JButton("发送");
		sender.setBounds(108, 448, 93, 23);
		panel_2.add(sender);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(0, 10, 201, 308);
		panel_2.add(scrollPane_1);

		receiveText = new JTextArea();
		scrollPane_1.setViewportView(receiveText);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(0, 328, 201, 120);
		panel_2.add(scrollPane_2);

		sendText = new JTextArea();
		scrollPane_2.setViewportView(sendText);

		panel_1 = new JPanel();
		panel_1.setBounds(171, 0, 459, 470);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);

		connect = new JButton("连接");
		connect.setBounds(258, 447, 93, 23);
		panel_1.add(connect);

		close = new JButton("断开");
		close.setBounds(366, 447, 93, 23);
		panel_1.add(close);
	}

	// 修改接收文本框
	public void setReceiveText(String message) {
		receiveText.append(message);
	}

	// 获取发送文本框内容
	public String getSendText() {
		return sendText.getText();
	}

	// 修改发送文本框内容
	public void setSendText(String message) {
		sendText.setText(message);
	}

	// 添加人员
	public void addListModel(String user) {
		listModel.addElement(user);
	}

	// 移除人员
	public void removeListModel(String user) {
		listModel.removeElement(user);
	}

	/**
	 * 修改在线人数
	 * 
	 * @param number
	 */
	public void setNumberMember(int number) {
		numberMember.setText("在线人数" + number);
	}

	//添加视频播放窗口
	public synchronized void addVideo(Component cp, int i) {
		//计算绘制位置
		int y = i < 2 ? 0 : 1;
		int x = i % 2;

		cp.setBounds(x * 230, y * 225, 230, 225);
		panel_1.add(cp);
		panel_1.repaint();//容器重画
		panel_1.validate();// 刷新容器
	}

	//移除视频播放窗口
	public synchronized void removeVideo(int ID) {
		panel_1.remove(ID+2);
		panel_1.repaint();//容器重画
		panel_1.validate();// 刷新容器
	}
}