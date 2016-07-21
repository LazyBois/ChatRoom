package server;

public class Server {
	
	//服务器端口
	private int textPort;
	private int videoPort;
	private int audioPort;
	//服务器IP
	private String multIP;//组播基础IP
	
	//用户相关
	private int max;//最大在线人数
	private String[] groupIP;//每个用户都有其组播地址
	private boolean[] visited;//对于groupIP的标记数组
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
	 * 开启服务器
	 */
	public void start()
	{
		MulticastIP();
		
		//创建服务器线程
		serverThread = new ServerThread(textPort,videoPort,audioPort,groupIP,visited,max);
		serverThread.start();
	}	
	
	/**
	 * 初始化多播地址
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
