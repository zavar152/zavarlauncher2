package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import application.Launcher;

public class ResourcesManager 
{
	private ArrayList<URL> fxml;
	private static ResourcesManager instance;
	private Properties lang, prop;
	
    private ResourcesManager()
    {
    	prop = new Properties();
    	lang = new Properties();
    	try 
    	{
			prop.load(new FileInputStream(Launcher.getPropsPath()));
			reloadLanguage();
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
		}
		fxml = new ArrayList<URL>();
		reloadFXML();
    }
    public static ResourcesManager getManager()
    { 
    	if(instance == null)
    	{		
    		instance = new ResourcesManager();	
    	}
    	return instance;		
    }
	
    public void changeProperty(String key, String value)
    {
    	prop.setProperty(key, value);
    	try 
    	{
			prop.store(new FileOutputStream(Launcher.getPropsPath()), null);
		} 
    	catch (IOException e) 
    	{
			ErrorWindow.show(e);
		}
    }
    
    public String getProperty(String key)
    {
    	return prop.getProperty(key);
    }
    
    public void reloadLanguage()
    {
    	try 
    	{
			lang.load(new InputStreamReader(getClass().getResourceAsStream("/lang/"+ prop.getProperty("lang") +".properties"), "UTF-8"));
		} 
    	catch (IOException e) 
    	{
    		ErrorWindow.show(e);
		}
    }
    
	public Properties getLangFile()
	{
		return lang;
	}
	
	public void reloadFXML()
	{
		fxml.clear();
		fxml.add(getClass().getResource("/fxml/mainScreen" + prop.getProperty("theme") + ".fxml"));
		fxml.add(getClass().getResource("/fxml/optionsScreen" + prop.getProperty("theme") + ".fxml"));
		fxml.add(getClass().getResource("/fxml/accountScreen" + prop.getProperty("theme") + ".fxml"));
	}
	
	public URL getFXML(int index)
	{
		return fxml.get(index); 
	}
}

