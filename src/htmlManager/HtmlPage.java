package htmlManager;

import java.util.ArrayList;

import server.ServerFileSystem;

public class HtmlPage {
	
	private HtmlBuilder html;
	
	public HtmlPage()
	{
		html = new HtmlBuilder();
	}
	
	public void index()
	{
		html.setTemplate("index.vm");
		String urlImages = "/Users/matteopolsinelli/FileSystem/Immagini/";
		ArrayList<String> images = ServerFileSystem.getListOfImagesByExtension(urlImages, ".jpg");
		ArrayList<String> urls = new ArrayList<String>();
		
		for(String s: images)
			urls.add(urlImages + s);
		
		html.setData("urls", urls);
		html.setData("nameImage", images);
	}
	
	//This method is call when user request a file that is not in file system.
	public void fileNotFound()
	{
		html.setTemplate("fileNotFound.vm");
	}
	
	public String toString()
	{
		return html.getHtmlPageString();
	}
}
