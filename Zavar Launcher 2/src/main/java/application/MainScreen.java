package application;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import download.DownloadZIP;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import util.ResourcesManager;

public class MainScreen 
{
	@FXML
	private ProgressBar bar;
	
	@FXML
	private Button launch_button, account_button, options_button;
	
	@FXML
	private Label info;
	
	private DownloadZIP down;
	
	
	@FXML
    void initialize() throws IOException 
    {
		setupLanguage();
		bar.setStyle("-fx-accent: green");

		options_button.setOnAction(event -> {
			try 
			{
				Scene scene = new Scene(FXMLLoader.load(ResourcesManager.getManager().getFXML(1)), Launcher.width, Launcher.height);
				Launcher.setScene(scene);
			} 
			catch (IOException e) 
			{ 
				e.printStackTrace();
			}
		});
		
		launch_button.setOnAction(event -> {
			try 
			{
				info.textProperty().unbind();
				bar.progressProperty().unbind();
				down = new DownloadZIP("http://109.167.166.234/MinecraftFont.zip", "G:\\JavaDownload\\MinecraftFont.zip");
				 
				Task<Void> task = new Task<Void>() {
				    @Override public Void call() {
				        final int max = 244816588;
				        while(!down.isDownloaded())
				        {
				            updateProgress(down.getFileSize(), max);
				            updateMessage(down.getState() + " " + down.getFileSize());
				        }
				        while(!down.isUnzipped())
				        {
				        	updateMessage(down.getState());
				        	if(down.getState().equals("ZipException"))
				        	{
				        		bar.setStyle("-fx-accent: red");
				        	}
				        	else if(down.getState().equals("IOException"))
				        	{
				        		bar.setStyle("-fx-accent: red");
				        	}
				        }
				        updateMessage(down.getState());
				        updateProgress(0, 0);
				        return null;
				    }
				};
				
				info.textProperty().bind(task.messageProperty());
				bar.progressProperty().bind(task.progressProperty());
				
				new Thread(task).start();
				down.downloadMinecraft();
			} 
			catch (IOException e) 
			{
				bar.setStyle("-fx-accent: red");
				e.printStackTrace();
			} 
			catch (Exception e) 
			{
				bar.setStyle("-fx-accent: red");
				e.printStackTrace();
			}
			
		});
    }
	
	private void setupLanguage() throws UnsupportedEncodingException, IOException
	{
		launch_button.setText(ResourcesManager.getManager().getLangFile().getProperty("launcher.button.launch"));
		account_button.setText(ResourcesManager.getManager().getLangFile().getProperty("launcher.button.account"));
		options_button.setText(ResourcesManager.getManager().getLangFile().getProperty("launcher.button.options"));
	}
}
