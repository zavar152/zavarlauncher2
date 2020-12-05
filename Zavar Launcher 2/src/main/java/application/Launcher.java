package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mojang.AuthenticationException;
import util.ErrorWindow;
import util.ResourcesManager;

public class Launcher extends Application 
{
	private static Stage mainStage;
	
	public final static int width = 600;
	public final static int height = 400;
	
	private static String home = System.getProperty("user.home");
	private static String main_path = home + "/zavarlauncher";
	private static String props_path = home + "/zavarlauncher/launcher.properties";
	private static String temp_path = home + "/zavarlauncher/temp";
	private static String game_path = home + "/zavarlauncher/game";
	
	public static boolean online = false;
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{
			checkPaths();
			try 
			{
				Scene scene = new Scene(FXMLLoader.load(ResourcesManager.getManager().getFXML(0)), width, height);
				mainStage = primaryStage;
				mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icon.png")));
				mainStage.setScene(scene);
				mainStage.setResizable(false);
				mainStage.setTitle("Zavar Launcher");
				mainStage.setOnCloseRequest(event -> {
					MainScreen.stopDownload();
				});
				mainStage.show();
			} 
			catch(Exception e)
			{
				ErrorWindow.show(e.getMessage()); 
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
		File main = new File(main_path);
		if(!main.exists())
		{
			main.mkdirs();
		}
		
		File prop = new File(props_path);
		if(!prop.exists())
		{
			prop.createNewFile();
			Files.copy(getClass().getResourceAsStream("/launcher/launcher.properties"), prop.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		
		File temp = new File(temp_path);
		if(!temp.exists())
		{
			temp.mkdirs();
		}
		
		File game = new File(game_path);
		if(!game.exists())
		{
			game.mkdirs();
		}
	}
	
	public static String getPropsPath()
	{
		return props_path;
	}
	
	public static String getMainPath()
	{
		return main_path;
	}
	
	public static String getTempPath()
	{
		return temp_path;
	}
	
	public static String getGamePath()
	{
		return game_path;
	}
	
	public static void hide()
	{
		mainStage.hide();
	}
	
	public static void setScene(Scene scene)
	{
		mainStage.setScene(scene);
	}
	
	public static Stage getStage()
	{
		return mainStage;
	}
	
	public static boolean showScreen(String title, String text, AlertType type)
	{
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(text);
		alert.initOwner(Launcher.mainStage);
  		Optional<ButtonType> result = alert.showAndWait();
  		if(result.get() == ButtonType.OK)
		{
			return true;
		}
		else if(result.get() == ButtonType.CANCEL)
		{
			return false;
		}
		return false;
	}
} 
