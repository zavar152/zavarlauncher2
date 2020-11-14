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
	private ComboBox<String> lang_box, theme_box;
	
	@FXML
	private Label lang_label, theme_label;
	
	@FXML
    void initialize() throws IOException 
    {
		setupLanguage();
		lang_box.setItems(FXCollections.observableArrayList("English", "Русский"));
		theme_box.setItems(FXCollections.observableArrayList("Default", "Dark"));
		theme_box.setDisable(true);
		
		try 
		{
			if(ResourcesManager.getManager().getTheme().equals("0"))
			{
				theme_box.setValue("Default");
			}
			else if(ResourcesManager.getManager().getTheme().equals("1"))
			{
				theme_box.setValue("Dark");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		theme_box.setOnAction(event -> {
			if(theme_box.getValue() == "Default")
			{
				try 
				{
					ResourcesManager.getManager().changeProperty("theme", "0");
					ResourcesManager.getManager().reloadFXML();
					Launcher.setScene(new Scene(FXMLLoader.load(ResourcesManager.getManager().getFXML(1)), Launcher.width, Launcher.height));
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			else if(theme_box.getValue() == "Dark")
			{
				try 
				{
					ResourcesManager.getManager().changeProperty("theme", "1");
					ResourcesManager.getManager().reloadFXML();
					Launcher.setScene(new Scene(FXMLLoader.load(ResourcesManager.getManager().getFXML(1)), Launcher.width, Launcher.height));
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		});
		
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
