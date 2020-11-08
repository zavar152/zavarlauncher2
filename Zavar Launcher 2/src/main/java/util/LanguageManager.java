package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class LanguageManager 
{
	private static Properties lang;
	
	public static void setupLanguage(String name) throws UnsupportedEncodingException, IOException
	{
		lang = new Properties();
		lang.load(new FileInputStream("/lang/" + name + ".properties"));
	}
	
    public static Properties getLang()
    {
    	return lang;
    }
}
