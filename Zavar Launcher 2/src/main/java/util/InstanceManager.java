package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import application.Launcher;

public class InstanceManager 
{
	private static InstanceManager instance;
	private JSONObject jsonRemote, jsonLocal;
	private JSONArray instanceArrayRemote, instanceArrayLocal;
	private Map<String, Integer> instanceMapRemote, instanceMapLocal;
	private File instancesFile = new File(Launcher.getMainPath() + "/instances.json");
	private String remoteInstance;
	private boolean connected = false;
	
	
	private InstanceManager()
	{	
		try 
		{
			remoteInstance = IOUtils.toString(new URL(ResourcesManager.getManager().getProperty("server") + "/instances.json").openStream(), Charset.defaultCharset());
			//FileUtils.copyURLToFile(new URL(ResourcesManager.getManager().getProperty("server") + "/instances.json"), instancesFile, 5000, 5000);
			connected = true;
			loadInstances();
			//System.out.println(isUpdated("GlebNaFome3"));
		} 
		catch (IOException e) 
		{
			ErrorWindow.show("Server is unavailable! " + e.getMessage());
			if(instancesFile.exists())
			{
				connected = false;
				loadInstances();
			}
			else
			{
				ErrorWindow.show("Instance file not found!");
		        System.exit(-1);
			}
		}
	}
	
    public static InstanceManager getManager()
    { 
    	if(instance == null)
    	{		
    		instance = new InstanceManager();	
    	}
    	return instance;		
    }
    
    public String[] getInstances()
    {
    	if(connected)
    	{
    		return instanceMapRemote.keySet().toArray(new String[instanceMapRemote.size()]);
    	}
    	else
    	{
    		return instanceMapLocal.keySet().toArray(new String[instanceMapLocal.size()]);
    	}
    }
    
    public boolean isUpdated(String name)
    {
    	System.out.println(getLocalVersion(name).split(".").length);
    	String[] local = getLocalVersion(name).split(".");
    	String[] remote = getRemoteVersion(name).split(".");
    	if(Integer.parseInt(remote[0]) > Integer.parseInt(local[0]))
    	{
    		return false;
    	}
    	else
    	{
    		if(Integer.parseInt(remote[1]) > Integer.parseInt(local[1]))
        	{
        		return false;
        	}
    		else
    		{
        		if(Integer.parseInt(remote[2]) > Integer.parseInt(local[2]))
            	{
            		return false;
            	}
        		else
        		{
        			return true;
        		}
    		}
    	}
    }
    
    public void removeLocalInstance(String name)
    {
    	instanceMapLocal.remove(name);
    	try 
    	{
			FileWriter writer = new FileWriter(instancesFile);
			writer.write(jsonLocal.toJSONString());
			writer.flush();
			writer.close();
		} 
    	catch (IOException e) 
    	{
    		ErrorWindow.show(e.getMessage());
		}
    }
    
    private String getLocalVersion(String name)
    {
    	return ((JSONObject) instanceArrayLocal.get(instanceMapLocal.get(name))).get("version").toString();
    }
    
    private String getRemoteVersion(String name)
    {
    	if(connected)
    	{
    		return ((JSONObject) instanceArrayRemote.get(instanceMapRemote.get(name))).get("version").toString();
    	}
    	else
    	{
    		return ((JSONObject) instanceArrayLocal.get(instanceMapLocal.get(name))).get("version").toString();
    	}
    }
    
    public String[] getInstanceArguments(String name)
    {
    	if(connected)
    	{
    		int index = instanceMapRemote.get(name);
    		return new String[] {((JSONObject) instanceArrayRemote.get(index)).get("version").toString(), ((JSONObject) instanceArrayRemote.get(index)).get("size").toString(), ((JSONObject) instanceArrayRemote.get(index)).get("minecraft").toString()};
    	}
    	else
    	{
    		int index = instanceMapLocal.get(name);
    		return new String[] {((JSONObject) instanceArrayLocal.get(index)).get("version").toString(), ((JSONObject) instanceArrayLocal.get(index)).get("size").toString(), ((JSONObject) instanceArrayLocal.get(index)).get("minecraft").toString()};
    	}
    }
    
    public boolean isInstalled(String name)
    {
    	//TODO
    	try 
    	{
    		return new File(Launcher.getGamePath() + "/" + name + "/versions/" + ((JSONObject) instanceArrayLocal.get(instanceMapLocal.get(name))).get("minecraft").toString() + "/" + ((JSONObject) instanceArrayLocal.get(instanceMapLocal.get(name))).get("minecraft").toString() + ".jar").exists();
    	}
    	catch(NullPointerException e)
    	{
    		return false;
    	}
    }
    
    private void loadInstances()
    {
    	try 
    	{
    		if(connected)
    		{
    			jsonRemote = (JSONObject) new JSONParser().parse(new StringReader(remoteInstance));
    			instanceMapRemote = new HashMap<String, Integer>();
    			instanceArrayRemote = (JSONArray) jsonRemote.get("instances");
    			JSONObject tempObj = null;
    			for(int i = 0; i < instanceArrayRemote.size(); i++)
    			{
    				tempObj = (JSONObject) instanceArrayRemote.get(i);
    				instanceMapRemote.put((String) tempObj.get("name"), i);
    			}
    		}
    		
    		try
    		{
    			jsonLocal = (JSONObject) new JSONParser().parse(new FileReader(instancesFile));
	    		instanceMapLocal = new HashMap<String, Integer>();
	    		instanceArrayLocal = (JSONArray) jsonLocal.get("instances");
	    		JSONObject tempObj = null;
	    		for(int i = 0; i < instanceArrayLocal.size(); i++)
	    		{
	    			tempObj = (JSONObject) instanceArrayLocal.get(i);
	    			instanceMapLocal.put((String) tempObj.get("name"), i);
	    		}
    		}
    		catch(FileNotFoundException e)
    		{
    			
    		}
		} 
    	catch (IOException | ParseException e) 
    	{
    		ErrorWindow.show(e.getMessage());
		}
    }
}
