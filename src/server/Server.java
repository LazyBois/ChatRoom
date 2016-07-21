package server;

public class Server {
	
	//�������˿�
	private int textPort;
	private int videoPort;
	private int audioPort;
	//������IP
	private String multIP;//�鲥����IP
	
	//�û����
	private int max;//�����������
	private String[] groupIP;//ÿ���û��������鲥��ַ
	private boolean[] visited;//����groupIP�ı������
	private ServerThread serverThread  = null;
	public Server()
	{
		textPort = 5880;
		videoPort = 6881;
		audioPort = 6882;
		
		multIP = "239.255.8.";
		max = 4;
		
		groupIP = new String[max];
		visited = new boolean[max];
	}
	
	/**
	 * ����������
	 */
	public void start()
	{
		MulticastIP();
		
		//�����������߳�
		serverThread = new ServerThread(textPort,videoPort,audioPort,groupIP,visited,max);
		serverThread.start();
	}	
	
	/**
	 * ��ʼ���ಥ��ַ
	 */
	private void MulticastIP()
	{
		for(int i = 0;i<max;i++)
		{
			groupIP[i] = multIP+i*2;
			visited[i] = false;
		}
	}
	public static void main(String[] args) {
		new Server().start();
	}
}
