package util;

import application.Launcher;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ErrorWindow 
{
	public static void show(String text)
	{
		Alert alert = new Alert(AlertType.WARNING, text);
		alert.initOwner(Launcher.getStage());
		alert.showAndWait();
	}
}
