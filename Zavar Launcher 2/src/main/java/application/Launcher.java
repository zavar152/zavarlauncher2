package application;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mojang.AuthenticationException;
import util.LanguageManager;
import util.PropertiesManager;

public class Launcher extends Application 
{
	//private static Authentication auth  = new Authentication();
	private static Stage mainStage;
	public final static int width = 600;
	public final static int height = 400;
	
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
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
		PropertiesManager.loadProperties();
		LanguageManager.setupLanguage(PropertiesManager.getProperties().getProperty("lang"));
		launch(args);
	}
	
	public static void show()
	{
		mainStage.show();
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
