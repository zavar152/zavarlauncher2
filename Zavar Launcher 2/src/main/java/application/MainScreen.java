package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import download.Downloader;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import util.ErrorWindow;
import util.InstanceManager;
import util.ResourcesManager;

public class MainScreen 
{
	@FXML
	private ProgressBar bar;
	
	@FXML
	private Button launch_button, account_button, options_button;
	
	@FXML
	private Label info;
	
	@FXML
	private WebView news_webview;
	
	@FXML
	private ListView<Pane> instances_listview;
	
	@FXML
	private MenuItem open;
	
	@FXML
	private MenuItem delete;
	
	private static Downloader down;
	
	private static Task<Void> task;
	
	private ResourcesManager resources = ResourcesManager.getManager();
	
	private static boolean launching = false;
	
	private String selected_instance;
	
	private ContextMenu contextMenu;
	
	private void buildList()
	{
		instances_listview.getItems().clear();
		Pane[] instances = new Pane[InstanceManager.getManager().getInstances().length];
		
		for(int i = 0; i < instances.length; i++)
		{
			instances[i] = new Pane();
			String name = InstanceManager.getManager().getInstances()[i];
			Label text = new Label(name);
			instances[i].setOnMouseClicked(event -> {
				if(event.getButton().equals(MouseButton.PRIMARY))
				{
					selected_instance = text.getText();
					if(InstanceManager.getManager().isInstalled(name))
					{
						launch_button.setText(resources.getLangFile().getProperty("launcher.button.launch"));
					}
					else
					{
						launch_button.setText(resources.getLangFile().getProperty("launcher.button.download"));
					}
				}
				if(event.getButton().equals(MouseButton.SECONDARY))
				{
					if(InstanceManager.getManager().isInstalled(name))
					{
						selected_instance = text.getText();
						contextMenu.show(instances_listview, event.getScreenX(), event.getScreenY());
					}
					else
					{
						contextMenu.hide();
					}
				}
			});
			if(InstanceManager.getManager().isInstalled(name))
			{
				text.setTextFill(Color.GREEN);
			}
			else
			{
				text.setTextFill(Color.RED);
			}
			text.setFont(Font.font("System", FontWeight.BOLD, 14));
			instances[i].getChildren().add(text); 
		}
		
		
		
		contextMenu = new ContextMenu();
		open = new MenuItem();
		delete = new MenuItem();
		
		open.setOnAction(event -> {
			try 
			{
				Desktop.getDesktop().open(new File(Launcher.getGamePath() + "/" + selected_instance));
			}
			catch (IOException e) 
			{
				ErrorWindow.show(e.getMessage());
			}
		});
		
		delete.setOnAction(event -> {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure?");
			alert.initOwner(Launcher.getStage());
			alert.showAndWait();
			if(alert.getResult().equals(ButtonType.OK))
			{
				try 
				{
					FileUtils.deleteDirectory(new File(Launcher.getGamePath() + "/" + selected_instance));
					InstanceManager.getManager().removeLocalInstance(selected_instance);
				} 
				catch (IOException e) 
				{
					ErrorWindow.show(e.getMessage());
				}
				buildList();
			}
		});
        
        contextMenu.getItems().addAll(open, delete);
		
        instances_listview.getItems().addAll(instances);
		
        instances_listview.setOnMouseClicked(event -> {
        	if(event.getButton().equals(MouseButton.PRIMARY))
        	{
        		contextMenu.hide();
        	}
        });
	}
	
	@FXML
    void initialize() throws IOException 
    {
		//String[] instances = (String[]) InstanceManager.getManager().getInstances();
		//instances_listview.setItems(FXCollections.observableArrayList(instances));
		/*Label[] instances = new Label[InstanceManager.getManager().getInstances().length];
		
		for(int i = 0; i < instances.length; i++)
		{
			instances[i] = new Label();
			instances[i].setOnMouseClicked(event -> {
				if(event.getButton().equals(MouseButton.SECONDARY))
				{
					contextMenu.show(instances_listview, event.getScreenX(), event.getScreenY());
				}
			});
			instances[i].setText(InstanceManager.getManager().getInstances()[i]);
		}*/
		

		buildList();
		
		setupLanguage();
		bar.setStyle("-fx-accent: green");
		
		news_webview.getEngine().load(resources.getProperty("server") + "/news.html");
		
		options_button.setOnAction(event -> {
			try 
			{
				Scene scene = new Scene(FXMLLoader.load(resources.getFXML(1)), Launcher.width, Launcher.height);
				Launcher.setScene(scene);
			} 
			catch (IOException e) 
			{ 
				ErrorWindow.show(e.getMessage());
			}
		});
		
		account_button.setOnAction(event -> {
			try 
			{
				Scene scene = new Scene(FXMLLoader.load(resources.getFXML(2)), Launcher.width, Launcher.height);
				Launcher.setScene(scene);
			} 
			catch (IOException e) 
			{ 
				ErrorWindow.show(e.getMessage());
			}
		});
		
		launch_button.setOnMouseClicked(event -> {
			if(!InstanceManager.getManager().isInstalled(selected_instance))
			{
				if(!launching)
				{
					launching = true;
					options_button.setDisable(true);
					account_button.setDisable(true);
					try 
					{
						info.textProperty().unbind();
						bar.progressProperty().unbind();
						down = new Downloader(resources.getProperty("server") + "/" + selected_instance + ".zip", selected_instance + ".zip", Launcher.getGamePath() + "/" + selected_instance);
						 
						task = new Task<Void>() 
						{
						    @Override 
						    public Void call() 
						    {
							        final int max = 244816588;
							        while(!down.isDownloaded() && !this.isCancelled())
							        {
							            updateProgress(down.getFileSize(), max);
							            updateMessage(down.getState() + " " + down.getFileSize());
							        }
							        launch_button.setDisable(true);
							        while(!down.isUnzipped() && !this.isCancelled())
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
							        launch_button.setDisable(false);
							        return null;
						    }
						};
						
						info.textProperty().bind(task.messageProperty());
						bar.progressProperty().bind(task.progressProperty());
						
						new Thread(task).start();
						down.downloadMinecraft();
					} 
					catch (Exception e) 
					{
						bar.setStyle("-fx-accent: red");
						ErrorWindow.show(e.getMessage());
					}
				}
				else
				{
					down.cancel();
					task.cancel();
					launching = false;
				}
			}
			else
			{
				System.out.println("Launching");
			}
		});
    }
	
	private void setupLanguage() throws UnsupportedEncodingException, IOException
	{
		Properties lang = resources.getLangFile();
		launch_button.setText(lang.getProperty("launcher.button.launch"));
		account_button.setText(lang.getProperty("launcher.button.account"));
		options_button.setText(lang.getProperty("launcher.button.options"));
		open.setText(lang.getProperty("launcher.menu.open"));
		delete.setText(lang.getProperty("launcher.menu.delete"));
	}
	
	public static void stopDownload()
	{
		if(launching && !down.getState().equals("Unzipping") && down != null)
		{
			down.cancel();
			task.cancel();
		}
	}
}
