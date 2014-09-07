package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import htmlManager.HtmlBuilder;


public class User implements Runnable {
	
	private Socket s;
	private BufferedReader inFromUser;
	private DataOutputStream outToUser;
	
	public User(Socket userSocket){
		s = userSocket;
	}

	public void run() {
		try
		{
			try
			{
				inFromUser = new BufferedReader(new InputStreamReader(s.getInputStream()));
				outToUser = new DataOutputStream(s.getOutputStream());
				fetchRequest();
				
			}finally
			{
				System.out.println("User Disconnected" + this.hashCode());
				s.close();
			}
		}catch (IOException exception) 
		{
			exception.printStackTrace();
		}
	}
	
	private void fetchRequest() throws IOException
	{
		//Read the REQUEST LINE of HTTP request message.
		StringTokenizer tokenizedLine = new StringTokenizer(inFromUser.readLine());
		
		//Server can only manage GET and POST request. 
		if(tokenizedLine.nextToken().equals("GET"))
		{
			String fileName = tokenizedLine.nextToken();
			
			//User is requesting text/html file.
			if(fileName.endsWith(".vm"))
			{
				if(fileName.endsWith("index.vm"))
				{
					HtmlBuilder html = new HtmlBuilder("index.vm");
					String urlImages = "/Users/matteopolsinelli/FileSystem/Immagini/";
					ArrayList<String> images = ServerFileSystem.getListOfImagesByExtension(urlImages, ".jpg");
					ArrayList<String> urls = new ArrayList<String>();
					
					for(String s: images)
						urls.add(urlImages + s);
					
					html.setData("urls", urls);
					html.setData("nameImage", images);
					
					outToUser.writeBytes("HTTP/1.0 200 Document Follows\r\n");
					outToUser.writeBytes("Content-Type: text/html\r\n");
					outToUser.writeBytes("\r\n");
					outToUser.writeBytes(html.getHtmlPageString());
				}
				
				
			}
			//User is requesting a jpg image.
			else if (fileName.endsWith(".jpg"))
			{
				File file = new File(fileName);
				
				int numOfBytes = (int) file.length();
				FileInputStream  inFile = new FileInputStream(fileName);
				
				byte[] fileInBytes = new byte[numOfBytes];
				
				inFile.read(fileInBytes);
				outToUser.writeBytes("HTTP/1.0 200 Document Follows\r\n");
				outToUser.writeBytes("Content-Type:image/jpeg\r\n");
				
				outToUser.writeBytes("Content-Length: " + numOfBytes + "\r\n");
				outToUser.writeBytes("\r\n");
				outToUser.write(fileInBytes, 0 , numOfBytes);
					
			}
			
		}else if(tokenizedLine.nextToken().equals("POST"))
		{
			
		}else
		{
			System.out.println("Impossible to fetch HTTP request message");
			//Avverti il browser
		}
	}
}
