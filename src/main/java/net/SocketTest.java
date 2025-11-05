package net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketTest {

	public static void socketClient(String ip,int port) throws IOException {

		Socket socket = null;
		try {
			socket = new Socket(ip,port);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		InputStream inputStream=socket.getInputStream();
		OutputStream outputStream =socket.getOutputStream();
		byte[] helloWordByte="helloWorld".getBytes();
		outputStream.write(helloWordByte);
		byte[] readByte=new byte[100];
		//阻塞
		while(true) {
			int readNum = inputStream.read(readByte, 0, 100);
			System.out.println(new String(readByte));
		}

	}

	public static void main(String[] args){
		try {
			socketClient("127.0.0.1",Config.PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
