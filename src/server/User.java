package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.StringTokenizer;
import htmlManager.HtmlPage;


public class User implements Runnable {
	
	private Socket s;
	private BufferedReader inFromUser;
	private DataOutputStream outToUser;
	private HtmlPage htmlPage;
	
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
			//Reflection calls the right method of HtmlPage
			if(fileName.endsWith(".vm"))
			{
				htmlPage = new HtmlPage();
				Method method;
				
				//Fetch the name of request file without extension
				String [] splits = fileName.split("/");
				//Take the last element means take the name of file with extension
				String name = splits[splits.length - 1];
				//Remove the extension
				name = name.replaceAll(".vm", "");
				
				/*  Reflection use the name of file to invoke the method with the same name in 
				 * HtmlPage. */
			
				try {
						method = htmlPage.getClass().getMethod(name, null);
						try {
								method.invoke(htmlPage, null);
								this.httpResponse("200", "text/html",0, htmlPage.toString(), null);
						} catch (IllegalArgumentException e) {
						} catch (IllegalAccessException e) {
						} catch (InvocationTargetException e) {}
				} catch (SecurityException e) {
				} catch (NoSuchMethodException e) 
				{
					htmlPage.fileNotFound();
					this.httpResponse("404", "text/html", 0, htmlPage.toString(), null);
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
	/**
	 * Send the Http message response to the user with data
	 * 
	 * @param code			//Status code of Http. Can't be undefined.
	 * @param contentType	//Type of the object in the body. Put "" if undefined.
	 * @param numOfBytes	//Length of the object in the body. Put 0 if undefined.
	 * @param body			//Data. Put "" if undefined.
	 * @param file 			//The bytes of the file. Put null if undefined.
	 */
	private void httpResponse(String code, String contentType, int numOfBytes, String body, byte[] file)
	{
		try {
			outToUser.writeBytes("HTTP/1.0 " + code +  " \r\n");
			if(contentType != "")
				outToUser.writeBytes("Content-Type: " + contentType  + "\r\n");
			if(numOfBytes != 0)
				outToUser.writeBytes("Content-Length: " + numOfBytes + "\r\n");
			if(body != "")
			{
				outToUser.writeBytes("\r\n");
				outToUser.writeBytes(body);
			}
			else if (file != null)
					outToUser.write(file, 0, numOfBytes);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
