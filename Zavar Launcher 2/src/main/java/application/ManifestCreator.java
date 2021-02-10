package application;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import lombok.EqualsAndHashCode.Exclude;
import util.ErrorWindow;

public class ManifestCreator 
{
	private static String home = System.getProperty("user.home");
	private static String main_path = home + "/zavarlauncher";
	private static String props_path = home + "/zavarlauncher/launcher.properties";
	private static String temp_path = home + "/zavarlauncher/temp";
	private static String game_path = home + "/zavarlauncher/game";
	private static String logs_path = home + "/zavarlauncher/logs";
	static int i = 0;
	
	private static ArrayList<String> exclude = new ArrayList<String>();
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException 
	{
		JSONObject jsonObj = new JSONObject();
		Map<String, String> map = new HashMap<String, String>();
		
		exclude.add("versions");
		
		Consumer<String> handle = new Consumer<String>() 
		{
			@Override
			public void accept(String t) 
			{
				String[] s = t.split("@");
				System.out.println(s[0] + " " + s[1]);
				JSONArray array = new JSONArray();
				JSONObject temp = new JSONObject();
				if(!exclude.contains(s[0].split("\\\\")[0]))
				{
					temp.put("path", s[0]);
					temp.put("hash", s[1]);
					array.add(temp);
					jsonObj.put("file" + i, array);
					i++;
				}
			}
		};
		
		try (Stream<Path> walk = Files.walk(Paths.get(home + "/instance"))) {

	        List<String> result = walk.filter(Files::isRegularFile)
	                .map(x -> Paths.get(home + "/instance").relativize(x) + "@" + x.hashCode()).collect(Collectors.toList());

	        result.forEach(handle);

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
		
		try(FileWriter file = new FileWriter("map.json")) 
        {
            file.write(jsonObj.toJSONString());
        } 
        catch (IOException e) 
        {
            ErrorWindow.show(e);
        }
		
		System.out.println();
		
		JSONObject jsonObj0 = (JSONObject) new JSONParser().parse(new FileReader("map.json")); 
		
		for(int i = 0; i < jsonObj0.size(); i++)
		{
			JSONObject temp = ((JSONObject)((JSONArray) jsonObj.get("file" + i)).get(0));
			System.out.println(i + " " + temp.get("path") + " " + temp.get("hash"));
		}
	}
}
