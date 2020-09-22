package zavar.application;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import zavar.download.DownloadZIP;
import zavar.mojang.AuthenticationException;

public class Launcher extends Application 
{
	//private static Authentication auth  = new Authentication();
	private static Stage mainStage;
	
	private static int width = 600;
	private static int height = 400;
	
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		Parent main_fxml = FXMLLoader.load(getClass().getResource("/zavar/fxml/main.fxml"));
			try 
			{
				Scene scene = new Scene(main_fxml, width, height);
				mainStage = primaryStage;
				mainStage.setScene(scene);
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
	
	public static void hide()
	{
		mainStage.hide();
	}
} 
