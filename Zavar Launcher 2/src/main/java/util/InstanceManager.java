package util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import application.Launcher;

public class InstanceManager 
{
	private static InstanceManager instance;
	private File instancesFile = new File(Launcher.getMainPath() + "/instances.json");
	private String remoteInstances;
	private boolean connected = false;
	
	private ArrayList<Instance> instances;
	
	
	private InstanceManager()
	{	
		try 
		{
			URLConnection server = new URL(ResourcesManager.getManager().getProperty("server") + "/instances.json").openConnection();
			server.setConnectTimeout(3000);
			server.setReadTimeout(5000);
			remoteInstances = IOUtils.toString(server.getInputStream(), Charset.defaultCharset());
			connected = true;
			if(!instancesFile.exists())
			{
				instancesFile.createNewFile();
				Files.copy(getClass().getResourceAsStream("/launcher/instances.json"), instancesFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			loadRemoteInstances();
		} 
		catch (IOException e) 
		{
			ErrorWindow.show(e, "Server is unavailable!");
			if(instancesFile.exists())
			{
				connected = false;
				loadLocalInstances();
				if(instances.size() == 0)
				{
					ErrorWindow.show("You don't have any instances!");
			        System.exit(1);
				}
			}
			else
			{
				ErrorWindow.show("You don't have any instances!");
		        System.exit(1);
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
    
    public ArrayList<Instance> getInstances()
    {
    	return instances;
    }
    
    public Instance getInstanceByName(String name)
    {
    	Instance inst = null;
    	for(int i = 0; i < instances.size(); i++)
    	{
    		if(instances.get(i).getName().equals(name))
    		{
    			inst = instances.get(i);
    			break;
    		}
    	}
    	return inst;
    }
    
    public boolean isConnected() 
    {
		return connected;
	}
    
    @SuppressWarnings("unchecked")
	public void installInstance(String name)
    {
    	JSONObject jsonObjRemote, jsonObjLocal;
		JSONArray instanceArrayRemote, instanceArrayLocal;
    	try 
    	{
			jsonObjRemote = (JSONObject) new JSONParser().parse(new StringReader(remoteInstances));
			jsonObjLocal = (JSONObject) new JSONParser().parse(new FileReader(instancesFile));
			instanceArrayRemote = (JSONArray) jsonObjRemote.get("instances");
			instanceArrayLocal = (JSONArray) jsonObjLocal.get("instances");
			
			for(int i = 0; i < instanceArrayRemote.size(); i++)
			{
				JSONObject tempRemoteObj = (JSONObject) instanceArrayRemote.get(i);
            	if(tempRemoteObj.get("name").equals(name))
            	{
            		JSONObject tempLocalObj = new JSONObject();
            		tempLocalObj.put("name", tempRemoteObj.get("name"));
            		tempLocalObj.put("size", tempRemoteObj.get("size"));
            		tempLocalObj.put("version", tempRemoteObj.get("version"));
            		tempLocalObj.put("minecraft", tempRemoteObj.get("minecraft"));
            		instanceArrayLocal.add(tempLocalObj);
                    getInstanceByName(name).update(tempRemoteObj.get("minecraft").toString(), Integer.parseInt(tempRemoteObj.get("size").toString()), tempRemoteObj.get("version").toString(), true);
            		break;
            	}
			}
			
			jsonObjLocal.clear();
			jsonObjLocal.put("instances", instanceArrayLocal);
			
            try(FileWriter file = new FileWriter(instancesFile)) 
            {
                file.write(jsonObjLocal.toJSONString());
            } 
            catch (IOException e) 
            {
                ErrorWindow.show(e);
            }
    	} 
    	catch (IOException | ParseException e) 
    	{
    		ErrorWindow.show(e);
		}
    	loadRemoteInstances();
    }
    
    @SuppressWarnings("unchecked")
	public void deleteInstance(String name) 
    {
    	JSONObject jsonObjLocal;
		JSONArray instanceArrayLocal;
    	try 
    	{
			jsonObjLocal = (JSONObject) new JSONParser().parse(new FileReader(instancesFile));
			instanceArrayLocal = (JSONArray) jsonObjLocal.get("instances");
			
			for(int i = 0; i < instanceArrayLocal.size(); i++)
			{
				JSONObject tempLocalObj = (JSONObject) instanceArrayLocal.get(i);
				if(tempLocalObj.get("name").equals(name))
				{
					instanceArrayLocal.remove(i);
					break;
				}
			}
			
			jsonObjLocal.clear();
			jsonObjLocal.put("instances", instanceArrayLocal);
			
            try(FileWriter file = new FileWriter(instancesFile)) 
            {
                file.write(jsonObjLocal.toJSONString());
            } 
            catch (IOException e) 
            {
                ErrorWindow.show(e);
            }
    	} 
    	catch (IOException | ParseException e) 
    	{
    		ErrorWindow.show(e);
		}
    	
    	if(connected)
    	{
    		loadRemoteInstances();
    	}
    	else
    	{
    		loadLocalInstances();
    	}
	}
    
    private void loadLocalInstances()
    {
    	instances = new ArrayList<Instance>();
    	JSONObject jsonObjLocal;
		JSONArray instanceArrayLocal;
    	try 
    	{
			jsonObjLocal = (JSONObject) new JSONParser().parse(new FileReader(instancesFile));
			instanceArrayLocal = (JSONArray) jsonObjLocal.get("instances");
			
			for(int i = 0; i < instanceArrayLocal.size(); i++)
			{
				JSONObject tempLocalObj = (JSONObject) instanceArrayLocal.get(i);
            	Instance inst = new Instance(tempLocalObj.get("name").toString(), tempLocalObj.get("version").toString(), tempLocalObj.get("version").toString(), Integer.parseInt(tempLocalObj.get("size").toString()), tempLocalObj.get("minecraft").toString(), true);
            	System.out.println(inst.getName());
            	System.out.println("Installed: " + inst.isInstalled());
            	System.out.println("Updated: " + inst.isUpdated());
            	System.out.println("Size: " + inst.getSize());
            	System.out.println("Check: " + inst.checkInstallation());
            	System.out.println();
            	
            	instances.add(inst);
			}
			
			System.out.println(instances.toString());
			
    	} 
    	catch (IOException | ParseException e) 
    	{
    		ErrorWindow.show(e);
		}
    }
    
    private void loadRemoteInstances()
    {
    	instances = new ArrayList<Instance>();
    	JSONObject jsonObjRemote, jsonObjLocal;
		JSONArray instanceArrayRemote, instanceArrayLocal;
    	try 
    	{
			jsonObjRemote = (JSONObject) new JSONParser().parse(new StringReader(remoteInstances));
			jsonObjLocal = (JSONObject) new JSONParser().parse(new FileReader(instancesFile));
			instanceArrayRemote = (JSONArray) jsonObjRemote.get("instances");
			instanceArrayLocal = (JSONArray) jsonObjLocal.get("instances");
			
			for(int i = 0; i < instanceArrayRemote.size(); i++)
			{
				JSONObject tempRemoteObj = (JSONObject) instanceArrayRemote.get(i);
            	String name = (String) tempRemoteObj.get("name");
            	boolean inLocal = false;
            	String localVersion = "";
            	for(int j = 0; j < instanceArrayLocal.size(); j++)
            	{
            		JSONObject tempLocalObj = (JSONObject) instanceArrayLocal.get(j);
            		if(tempLocalObj.containsValue(name))
            		{
            			inLocal = true;
            			localVersion = (String) tempLocalObj.get("version");
            		}
            	}
            	
            	Instance inst = new Instance(name, localVersion, tempRemoteObj.get("version").toString(), Integer.parseInt(tempRemoteObj.get("size").toString()), tempRemoteObj.get("minecraft").toString(), inLocal);
            	System.out.println(inst.getName());
            	System.out.println("Installed: " + inst.isInstalled());
            	System.out.println("Updated: " + inst.isUpdated());
            	System.out.println("Size: " + inst.getSize());
            	System.out.println("Check: " + inst.checkInstallation());
            	System.out.println();
            	
            	instances.add(inst);
			}
			
			System.out.println(instances.toString());
			
    	} 
    	catch (IOException | ParseException e) 
    	{
    		ErrorWindow.show(e);
		}
    }
    
    public class Instance
    {
    	private String localVersion;
    	private String remoteVersion;
		private String name;
		private int size;
		private String minecraft;
		private boolean installed;

		public Instance(String name, String localVersion, String remoteVersion, int size, String minecraft, boolean installed) 
    	{
			this.name = name;
			if(installed)
			{
				this.localVersion = localVersion;
			}
			else
			{
				this.localVersion = "";
			}
			this.remoteVersion = remoteVersion;
			this.size = size;
			this.minecraft = minecraft;
			this.installed = installed;
		}
		
		public String getMinecraft() 
		{
			return minecraft;
		}
		
		public String getName() 
		{
			return name;
		}
		
		private void update(String minecraft, int size, String version, boolean installed)
		{
			this.localVersion = version;
			this.size = size;
			this.minecraft = minecraft;
			this.installed = installed;
		}
		
		public int getSize() 
		{
			return size;
		}
		
		public String getVersion() 
		{
			return localVersion;
		}
		
		public boolean isInstalled() 
		{
			return installed;
		}
		
		public boolean checkInstallation()
		{
			return new File(Launcher.getGamePath() + "/" + name + "/versions/" + minecraft + "/" + minecraft + ".jar").exists();
		}
		
		@Override
		public String toString() 
		{
			return name;
		}
		
		public boolean isUpdated()
		{
			if(installed)
			{
		    	String[] local = localVersion.split("\\.");
		    	String[] remote = remoteVersion.split("\\.");
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
			else
			{
				return false;
			}
		}
    }
}
