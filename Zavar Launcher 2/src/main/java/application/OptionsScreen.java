package application;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
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
	private TitledPane launcher_settings_pane, java_settings_pane;
	
	@FXML
	private TextField jvm_path_field;
	
	@FXML
	private Spinner<Integer> xmx_spinner, xms_spinner, permgen_spinner;
	
	@FXML
	private CheckBox hide_checkbox, console_checkbox;
	
	private ResourcesManager resources = ResourcesManager.getManager();
	
	@FXML
    void initialize() throws IOException 
    {
		setupLanguage();
		lang_box.setItems(FXCollections.observableArrayList("English", "Русский"));
		theme_box.setItems(FXCollections.observableArrayList("Default", "Dark"));
		theme_box.setDisable(true);
		
		if(resources.getProperty("theme").equals("0"))
		{
			theme_box.setValue("Default");
		}
		else if(resources.getProperty("theme").equals("1"))
		{
			theme_box.setValue("Dark");
		}
		console_checkbox.setSelected(Boolean.parseBoolean(resources.getProperty("console")));
		hide_checkbox.setSelected(Boolean.parseBoolean(resources.getProperty("hide")));
		
		console_checkbox.setOnMouseClicked(event -> {
			try 
			{
				resources.changeProperty("console", Boolean.toString(console_checkbox.isSelected()));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		});
		
		hide_checkbox.setOnMouseClicked(event -> {
			try 
			{
				resources.changeProperty("hide", Boolean.toString(hide_checkbox.isSelected()));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		});
		
		theme_box.setOnAction(event -> {
			if(theme_box.getValue() == "Default")
			{
				try 
				{
					resources.changeProperty("theme", "0");
					resources.reloadFXML();
					Launcher.setScene(new Scene(FXMLLoader.load(resources.getFXML(1)), Launcher.width, Launcher.height));
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
					resources.changeProperty("theme", "1");
					resources.reloadFXML();
					Launcher.setScene(new Scene(FXMLLoader.load(resources.getFXML(1)), Launcher.width, Launcher.height));
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
					resources.changeProperty("lang", "ru");
					resources.reloadLanguage();
					setupLanguage();
				}
				else if(lang_box.getValue().equals("English"))
				{
					resources.changeProperty("lang", "en");
					resources.reloadLanguage();
					setupLanguage();
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		});
		
		back_button.setOnAction(event -> {
			try 
			{
				Scene scene = new Scene(FXMLLoader.load(resources.getFXML(0)), Launcher.width, Launcher.height);
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
		Properties lang = resources.getLangFile();
		if(resources.getProperty("lang").equals("ru"))
		{
			lang_box.setValue("Русский");
		}
		else if(resources.getProperty("lang").equals("en"))
		{
			lang_box.setValue("English");
		}
		back_button.setText(lang.getProperty("launcher.button.back"));
		lang_label.setText(lang.getProperty("launcher.options.lang"));
		theme_label.setText(lang.getProperty("launcher.options.theme"));
		java_settings_pane.setText(lang.getProperty("launcher.options.java"));
		launcher_settings_pane.setText(lang.getProperty("launcher.options.launcher"));
		console_checkbox.setText(lang.getProperty("launcher.options.console"));
		hide_checkbox.setText(lang.getProperty("launcher.options.hide"));
	}
}
