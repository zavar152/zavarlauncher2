package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import application.Launcher;

public class ResourcesManager 
{
	private ArrayList<URL> fxml;
	private static ResourcesManager instance;
	private static Properties lang, prop;
	
    private ResourcesManager() throws UnsupportedEncodingException, IOException
    {
    	prop = new Properties();
    	prop.load(new FileInputStream(Launcher.getPropsPath()));
    	
		lang = new Properties();
		lang.load(new InputStreamReader(getClass().getResourceAsStream("/lang/"+ prop.getProperty("lang") +".properties"), "UTF-8"));
    
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
	
    public void changeProperty(String key, String value) throws FileNotFoundException, IOException
    {
    	prop.setProperty(key, value);
    	prop.store(new FileOutputStream(Launcher.getPropsPath()), null);
    }
    
    public void reloadLanguage() throws UnsupportedEncodingException, IOException
    {
    	lang.load(new InputStreamReader(getClass().getResourceAsStream("/lang/"+ prop.getProperty("lang") +".properties"), "UTF-8"));
    }
    
	public Properties getLangFile()
	{
		return lang;
	}
	
	public String getLanguage()
	{
		return prop.getProperty("lang");
	}
	
	public URL getFXML(int index)
	{
		return fxml.get(index);
	}
}

