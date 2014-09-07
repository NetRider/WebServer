package server;

import java.io.File;
import java.util.ArrayList;

public class ServerFileSystem {
	
	public static ArrayList getListOfImagesByExtension(String path, String extension)
	{
		ArrayList listOfImages = new ArrayList<String>();
		File folder = new File(path);
		
		//initializes the list of files
		File[] listOfFiles = folder.listFiles();	
		
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(extension)) 
				listOfImages.add(listOfFiles[i].getName());	
		}
		
		return listOfImages;
	}
}
