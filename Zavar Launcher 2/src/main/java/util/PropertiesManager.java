package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class PropertiesManager 
{
	private static Properties prop;
	
	public static void loadProperties() throws UnsupportedEncodingException, IOException
	{
		prop = new Properties();
		prop.load(new FileInputStream("/launcher/launcher.properties"));
	}
	
    public static Properties getProperties()
    {
    	return prop;
    }
}
