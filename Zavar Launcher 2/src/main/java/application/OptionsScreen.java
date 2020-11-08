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
import util.LanguageManager;
import util.PropertiesManager;

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
		switch(PropertiesManager.getProperties().get("lang").toString()) 
		{
			case "ru": 
			{
				lang_box.setPromptText("Русский");
				break;
			}
			case "en":
			{
				lang_box.setPromptText("English");
				break;
			}
			default:
			{
				lang_box.setPromptText("English");
				break;
			}
		}
		
		lang_box.setOnAction(event -> {
			switch(lang_box.getValue())
			{
				case "English": 
				{
					try 
					{
						//ResourcesManager.getManager().getProperties().setProperty("lang", "en");
						setupLanguage();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
				case "Русский":
				{
					try 
					{
						//ResourcesManager.getManager().getProperties().setProperty("lang", "ru");
						setupLanguage();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
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
		back_button.setText(LanguageManager.getLang().get("launcher.button.back").toString());
		lang_label.setText(LanguageManager.getLang().get("launcher.optins.lang").toString());
	}
}
