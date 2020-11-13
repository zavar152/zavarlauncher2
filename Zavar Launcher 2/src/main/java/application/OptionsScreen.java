package application;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import util.ResourcesManager;

public class OptionsScreen 
{
	@FXML
	private Button back_button;
	
	@FXML
	private ComboBox<String> lang_box;
	
	@FXML
	private Label lang_label;
	
	@FXML
    void initialize() throws IOException 
    {
		setupLanguage();
		lang_box.setItems(FXCollections.observableArrayList("English", "Русский"));
		
		lang_box.setOnAction(event -> {
			try {
				if(lang_box.getValue().equals("Русский"))
				{
					ResourcesManager.getManager().changeProperty("lang", "ru");
					ResourcesManager.getManager().reloadLanguage();
					setupLanguage();
				}
				else if(lang_box.getValue().equals("English"))
				{
					ResourcesManager.getManager().changeProperty("lang", "en");
					ResourcesManager.getManager().reloadLanguage();
					setupLanguage();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		back_button.setOnAction(event -> {
			try 
			{
				Scene scene = new Scene(FXMLLoader.load(ResourcesManager.getManager().getFXML(0)), Launcher.width, Launcher.height);
				Launcher.setScene(scene);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		});
    }
	
	private void setupLanguage() throws UnsupportedEncodingException, IOException
	{
		if(ResourcesManager.getManager().getLanguage().equals("ru"))
		{
			lang_box.setValue("Русский");
		}
		else if(ResourcesManager.getManager().getLanguage().equals("en"))
		{
			lang_box.setValue("English");
		}
		back_button.setText(ResourcesManager.getManager().getLangFile().getProperty("launcher.button.back"));
		lang_label.setText(ResourcesManager.getManager().getLangFile().getProperty("launcher.optins.lang"));
	}
}
