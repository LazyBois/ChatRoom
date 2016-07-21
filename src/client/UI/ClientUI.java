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

	// ������
	public JButton sender;
	public JButton connect;
	public JButton close;

	// ��ͼ
	private JTextArea sendText;
	private JTextArea receiveText;
	private JList memberList;
	private JPanel panel_1;
	private DefaultListModel listModel;
	private JLabel numberMember;
	// ������
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

		numberMember = new JLabel("��������");
		numberMember.setBounds(10, 0, 157, 26);
		panel.add(numberMember);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(631, 0, 201, 470);
		getContentPane().add(panel_2);
		panel_2.setLayout(null);

		sender = new JButton("����");
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

		connect = new JButton("����");
		connect.setBounds(258, 447, 93, 23);
		panel_1.add(connect);

		close = new JButton("�Ͽ�");
		close.setBounds(366, 447, 93, 23);
		panel_1.add(close);
	}

	// �޸Ľ����ı���
	public void setReceiveText(String message) {
		receiveText.append(message);
	}

	// ��ȡ�����ı�������
	public String getSendText() {
		return sendText.getText();
	}

	// �޸ķ����ı�������
	public void setSendText(String message) {
		sendText.setText(message);
	}

	// �����Ա
	public void addListModel(String user) {
		listModel.addElement(user);
	}

	// �Ƴ���Ա
	public void removeListModel(String user) {
		listModel.removeElement(user);
	}

	/**
	 * �޸���������
	 * 
	 * @param number
	 */
	public void setNumberMember(int number) {
		numberMember.setText("��������" + number);
	}

	//�����Ƶ���Ŵ���
	public synchronized void addVideo(Component cp, int i) {
		//�������λ��
		int y = i < 2 ? 0 : 1;
		int x = i % 2;

		cp.setBounds(x * 230, y * 225, 230, 225);
		panel_1.add(cp);
		panel_1.repaint();//�����ػ�
		panel_1.validate();// ˢ������
	}

	//�Ƴ���Ƶ���Ŵ���
	public synchronized void removeVideo(int ID) {
		panel_1.remove(ID+2);
		panel_1.repaint();//�����ػ�
		panel_1.validate();// ˢ������
	}
}