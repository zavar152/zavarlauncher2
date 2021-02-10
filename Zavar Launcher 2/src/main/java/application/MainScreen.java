package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import download.Downloader;
import javafx.application.Platform;
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
	private MenuItem open = new MenuItem();
	
	@FXML
	private MenuItem delete = new MenuItem();
	
	private static Downloader down;
	
	private static Task<Void> task;
	
	private ResourcesManager resources;
	private InstanceManager instManager;
	
	private static boolean launching = false;
	
	private String selected_instance;
	
	private ContextMenu contextMenu = new ContextMenu();
	
	private void buildList()
	{ 
		instances_listview.getItems().clear();
		contextMenu.getItems().clear();
		Pane[] instances = new Pane[instManager.getInstances().size()];
		
		for(int i = 0; i < instances.length; i++)
		{
			instances[i] = new Pane();
			InstanceManager.Instance inst = instManager.getInstances().get(i);
			String name = inst.getName();
			Label text = new Label(name);
			instances[i].setOnMousePressed(event -> {
				launch_button.setDisable(false);
				selected_instance = text.getText();
				if(inst.isInstalled() & inst.isUpdated())
				{
					launch_button.setText(resources.getLangFile().getProperty("launcher.button.launch"));
				}
				else if(inst.isInstalled() & !inst.isUpdated())
				{
					launch_button.setText(resources.getLangFile().getProperty("launcher.button.update"));
				}
				else if(!inst.isInstalled())
				{
					launch_button.setText(resources.getLangFile().getProperty("launcher.button.download"));
				}
				if(event.getButton().equals(MouseButton.SECONDARY))
				{
					if(inst.isInstalled())
					{
						selected_instance = text.getText();
						launch_button.setDisable(false);
						contextMenu.show(instances_listview, event.getScreenX(), event.getScreenY());
					}
					else
					{
						contextMenu.hide();
					}
				}
			});
			if(inst.isInstalled() & inst.isUpdated())
			{
				text.setTextFill(Color.GREEN);
			}
			else if(inst.isInstalled() & !inst.isUpdated())
			{
				text.setTextFill(Color.BLUEVIOLET);
			}
			else if(!inst.isInstalled())
			{
				text.setTextFill(Color.RED);
			}
			text.setFont(Font.font("System", FontWeight.BOLD, 14));
			instances[i].getChildren().add(text); 
		}
		
		open.setOnAction(event -> {
			try 
			{
				Desktop.getDesktop().open(new File(Launcher.getGamePath() + "/" + selected_instance));
			}
			catch (IOException e) 
			{
				ErrorWindow.show(e);
			}
		});
		
		delete.setStyle("-fx-text-fill: red");
		
		delete.setOnAction(event -> {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure?");
			alert.initOwner(Launcher.getStage());
			alert.showAndWait();
			if(alert.getResult().equals(ButtonType.OK))
			{
				try 
				{
					FileUtils.deleteDirectory(new File(Launcher.getGamePath() + "/" + selected_instance));
					instManager.deleteInstance(selected_instance);
				} 
				catch (IOException e) 
				{
					ErrorWindow.show(e);
				}
				launch_button.setText("");
				launch_button.setDisable(true);
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
		resources = ResourcesManager.getManager();
		instManager = InstanceManager.getManager();
		
		if(Boolean.parseBoolean(resources.getProperty("console")))
		{
			Launcher.consoleStage.show();
		}
		else
		{
			Launcher.consoleStage.hide();
		}
		
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
				ErrorWindow.show(e);
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
				ErrorWindow.show(e);
			}
		});
		
		launch_button.setOnMouseClicked(event -> {
			final InstanceManager.Instance inst = instManager.getInstanceByName(selected_instance);
			
			if(!inst.isInstalled() && !inst.isUpdated())
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
							        final int max = inst.getSize();
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
									options_button.setDisable(false);
									account_button.setDisable(false);
							        if(!this.isCancelled())
							        {
								        instManager.installInstance(selected_instance);
								        Platform.runLater(() -> {
											buildList();
											launch_button.setDisable(true);
											launch_button.setText("");
								        });
							        }
							        launching = false;
							        return null;
						    }
						};
						
						info.textProperty().bind(task.messageProperty());
						bar.progressProperty().bind(task.progressProperty());
						
						new Thread(task).start();
						down.downloadInstance();
					} 
					catch (Exception e) 
					{
						bar.setStyle("-fx-accent: red");
						ErrorWindow.show(e);
					}
				}
				else
				{
					down.cancel();
					task.cancel();
					launching = false;
				}
			}
			else if(inst.isInstalled() && !inst.isUpdated())
			{
				System.out.println("Updating");
			}
			else if(inst.isInstalled() && inst.isUpdated())
			{
				System.out.println("Launching");
			}
		});
    }
	
	private void setupLanguage() throws UnsupportedEncodingException, IOException
	{
		Properties lang = resources.getLangFile();
		//launch_button.setText(lang.getProperty("launcher.button.launch"));
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
