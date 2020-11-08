package application;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

public class ResourcesManager 
{
	private ArrayList<URL> fxml;
	private static ResourcesManager instance;
	
    private ResourcesManager() throws UnsupportedEncodingException, IOException
    {
		fxml = new ArrayList<URL>();
		fxml.add(getClass().getResource("/fxml/mainScreen.fxml"));
		fxml.add(getClass().getResource("/fxml/optionsScreen.fxml"));
    }
    public static ResourcesManager getManager() throws UnsupportedEncodingException, IOException
    { 
    	if(instance == null)
    	{		
    		instance = new ResourcesManager();	
    	}
    	return instance;		
    }
	
	public URL getFXML(int index)
	{
		return fxml.get(index);
	}
}

