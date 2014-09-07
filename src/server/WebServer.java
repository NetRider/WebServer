package server;

import server.User;

import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
	
	public static void main (String args[]) throws Exception
	{
		ServerSocket welcomeSocket = new ServerSocket(6788);
		
		while(true)
		{
			Socket socket = welcomeSocket.accept();
			System.out.println("User Connected");
			User user = new User(socket);
			Thread t = new Thread(user);
			t.start();
		}
	}
}
