package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mojang.AuthenticationException;
import util.ResourcesManager;

public class Launcher extends Application 
{
	private static Stage mainStage;
	
	public final static int width = 600;
	public final static int height = 400;
	
	private static String home = System.getProperty("user.home");
	private static String props_path = home + "/zavarlauncher/launcher.properties";
	
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		checkPaths();
			try 
			{
				Scene scene = new Scene(FXMLLoader.load(ResourcesManager.getManager().getFXML(0)), width, height);
				mainStage = primaryStage;
				mainStage.setScene(scene);
				mainStage.setResizable(false);
				mainStage.setTitle("Zavar Launcher");
				mainStage.show();
			} 
			catch(Exception e)
			{
				e.printStackTrace(); 
			} 
	}
	
	public static void main(String[] args) throws AuthenticationException, IOException, InterruptedException
	{ 
		launch(args);
	}
	
	public static void show()
	{
		mainStage.show();
	}
	
	private void checkPaths() throws IOException
	{
		File props = new File(home + "/zavarlauncher");
		if(!props.exists())
		{
			props.mkdirs();
			props = new File(home + "/zavarlauncher/launcher.properties");
			props.createNewFile();
			Files.copy(getClass().getResourceAsStream("/launcher/launcher.properties"), props.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	public static String getPropsPath()
	{
		return props_path;
	}
	
	public static void hide()
	{
		mainStage.hide();
	}
	
	public static void setScene(Scene scene)
	{
		mainStage.setScene(scene);
	}
} 
