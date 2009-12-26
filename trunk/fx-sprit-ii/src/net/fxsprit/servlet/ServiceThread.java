package net.fxsprit.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceThread implements Runnable {

	Socket socket = null;

	public ServiceThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {

			// 由Socket对象得到输入流,并构造相应的BufferedReader对象
			BufferedReader in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));

			// 由Socket对象得到输出流,并构造PrintWriter对象
			PrintWriter out = new PrintWriter(socket.getOutputStream());

			for (int i = 0; i < 20; i++) {
				// pr = new PrintStream(response.getOutputStream());

				out.println("有时候你不得不相信" + i);
				// out.println(("有时候你不得不相信有时候你不得不相信有时候你不得不相信有时候你不得不相信有时候你不得不相信有时候你不得不相信"+i).getBytes());
				// pr.println("有时候你不得不相信"+i);
				// flush的作用很重要，当你任务写给客户端的数据总够多的时候
				// 调用之，客户端方能读取到。
				// 否则，在数据长度达到上限或者连接关闭之前，客户端读不到数据
				System.out.println(i);
				out.flush();
				// pr.flush();
				Thread.sleep(2000);
			}
			//
			// // 由系统标准输入设备构造BufferedReader对象
			// BufferedReader sysin = new BufferedReader(new InputStreamReader(
			// System.in));
			//
			// // 在标准输出上打印从客户端读入的字符串
			// System.out.println("[Client " + number + "]: " + in.readLine());
			//
			// String line; // 保存一行内容
			//
			// // 从标准输入读入一字符串
			// line = sysin.readLine();
			//
			// while (!line.equals("bye")) { // 如果该字符串为 "bye",则停止循环
			//
			// // 向客户端输出该字符串
			// out.println(line);
			//
			// // 刷新输出流,使Client马上收到该字符串
			// out.flush();
			//
			// // 在系统标准输出上打印读入的字符串
			// System.out.println("[Server]: " + line);
			//
			// // 从Client读入一字符串,并打印到标准输出上
			// System.out.println("[Client " + number + "]: " + in.readLine());
			//
			// // 从系统标准输入读入一字符串
			// line = sysin.readLine();
			// }

			out.close(); // 关闭Socket输出流
			in.close(); // 关闭Socket输入流
			socket.close(); // 关闭Socket
		} catch (Exception e) {
			System.out.println("Error. " + e);
		}

	}

}
