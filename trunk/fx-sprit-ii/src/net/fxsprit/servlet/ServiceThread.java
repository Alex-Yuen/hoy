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

			// ��Socket����õ�������,��������Ӧ��BufferedReader����
			BufferedReader in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));

			// ��Socket����õ������,������PrintWriter����
			PrintWriter out = new PrintWriter(socket.getOutputStream());

			for (int i = 0; i < 20; i++) {
				// pr = new PrintStream(response.getOutputStream());

				out.println("��ʱ���㲻�ò�����" + i);
				// out.println(("��ʱ���㲻�ò�������ʱ���㲻�ò�������ʱ���㲻�ò�������ʱ���㲻�ò�������ʱ���㲻�ò�������ʱ���㲻�ò�����"+i).getBytes());
				// pr.println("��ʱ���㲻�ò�����"+i);
				// flush�����ú���Ҫ����������д���ͻ��˵������ܹ����ʱ��
				// ����֮���ͻ��˷��ܶ�ȡ����
				// ���������ݳ��ȴﵽ���޻������ӹر�֮ǰ���ͻ��˶���������
				System.out.println(i);
				out.flush();
				// pr.flush();
				Thread.sleep(2000);
			}
			//
			// // ��ϵͳ��׼�����豸����BufferedReader����
			// BufferedReader sysin = new BufferedReader(new InputStreamReader(
			// System.in));
			//
			// // �ڱ�׼����ϴ�ӡ�ӿͻ��˶�����ַ���
			// System.out.println("[Client " + number + "]: " + in.readLine());
			//
			// String line; // ����һ������
			//
			// // �ӱ�׼�������һ�ַ���
			// line = sysin.readLine();
			//
			// while (!line.equals("bye")) { // ������ַ���Ϊ "bye",��ֹͣѭ��
			//
			// // ��ͻ���������ַ���
			// out.println(line);
			//
			// // ˢ�������,ʹClient�����յ����ַ���
			// out.flush();
			//
			// // ��ϵͳ��׼����ϴ�ӡ������ַ���
			// System.out.println("[Server]: " + line);
			//
			// // ��Client����һ�ַ���,����ӡ����׼�����
			// System.out.println("[Client " + number + "]: " + in.readLine());
			//
			// // ��ϵͳ��׼�������һ�ַ���
			// line = sysin.readLine();
			// }

			out.close(); // �ر�Socket�����
			in.close(); // �ر�Socket������
			socket.close(); // �ر�Socket
		} catch (Exception e) {
			System.out.println("Error. " + e);
		}

	}

}
