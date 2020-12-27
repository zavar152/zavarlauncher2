package application;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import util.ErrorWindow;
import util.ResourcesManager;

public class AccountScreen 
{
	@FXML
	private Button back_button;
	
	private ResourcesManager resources = ResourcesManager.getManager();
	
	@FXML
    void initialize()
    {
		back_button.setOnAction(event -> {
			try 
			{
				Scene scene = new Scene(FXMLLoader.load(resources.getFXML(0)), Launcher.width, Launcher.height);
				Launcher.setScene(scene);
			} 
			catch (IOException e) 
			{
				ErrorWindow.show(e.getMessage());
			}
		});
    }
	
	private void setupLanguage() throws UnsupportedEncodingException, IOException
	{
		Properties lang = resources.getLangFile();
	}
}
