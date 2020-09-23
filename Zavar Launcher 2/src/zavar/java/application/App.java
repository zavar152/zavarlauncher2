package application;

import java.io.IOException;

import download.DownloadZIP;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class App 
{
	@FXML
	private ProgressBar bar;
	
	@FXML
	private Button begin;
	
	@FXML
	private Button options_button;
	
	@FXML
	private Label info;
	
	private DownloadZIP down;
	
	
	@FXML
    void initialize() throws IOException 
    {
		bar.setStyle("-fx-accent: green");
		
		options_button.setOnAction(event -> {
			
		});
		
		begin.setOnAction(event -> {
			try 
			{
				info.textProperty().unbind();
				bar.progressProperty().unbind();
				down = new DownloadZIP("http://109.167.166.234/MinecraftFont.zip", "G:\\JavaDownload\\MinecraftFont.zip");
				
				Task task = new Task<Void>() {
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
}
