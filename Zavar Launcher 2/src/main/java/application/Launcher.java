package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mojang.AuthenticationException;
import util.ErrorWindow;
import util.ResourcesManager;

public class Launcher extends Application 
{
	private static Stage mainStage;
	public static Stage consoleStage;
	
	public final static int width = 600;
	public final static int height = 400;
	
	private static String home = System.getProperty("user.home");
	private static String main_path = home + "/zavarlauncher";
	private static String props_path = home + "/zavarlauncher/launcher.properties";
	private static String temp_path = home + "/zavarlauncher/temp";
	private static String game_path = home + "/zavarlauncher/game";
	private static String logs_path = home + "/zavarlauncher/logs";
	
	private File log = new File(logs_path + "/log" + new SimpleDateFormat(" yyyy.MM.dd HHmmss").format(new Date()) + ".txt");
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{ 
		consoleStage = new Stage();
		TextArea textArea = new TextArea();
		textArea.setEditable(false);
		
        System.setOut(new PrintStream(new StreamCapturer(new Consumer() {
            @Override 
            public void appendText(String text) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        textArea.appendText(text);
                        try(FileWriter f = new FileWriter(log)) 
                        {
							f.write(textArea.getText());
						} 
                        catch (IOException e) 
                        {
							e.printStackTrace();
						}
                    }
                });
            }
        }, System.out)));
        
        System.setErr(new PrintStream(new StreamCapturer(new Consumer() {
            @Override 
            public void appendText(String text) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        textArea.appendText(text);
                        try(FileWriter f = new FileWriter(log)) 
                        {
							f.write(textArea.getText());
						} 
                        catch (IOException e) 
                        {
							e.printStackTrace();
						}
                    }
                });
            }
        }, System.err)));
		
		BorderPane root = new BorderPane(textArea);
		Scene sceneCon = new Scene(root, 500, 400);
		consoleStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icon.png")));
		consoleStage.setScene(sceneCon);
		consoleStage.setResizable(false);
		consoleStage.setTitle("Launcher Console"); 
		
		System.out.println("Checking launcher's paths...");
		checkPaths();
		try 
		{
			Scene scene = new Scene(FXMLLoader.load(ResourcesManager.getManager().getFXML(0)), width, height);
			mainStage = primaryStage;
			mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icon.png")));
			mainStage.setScene(scene);
			mainStage.setResizable(false);
			mainStage.setTitle("Zavar Launcher"); 
			mainStage.setOnCloseRequest(event -> {
				System.out.println("Closing launcher...");
				MainScreen.stopDownload();
				Platform.exit();
			});
			mainStage.show();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			ErrorWindow.show(e); 
		} 
	}
	
	public static void main(String[] args) throws AuthenticationException, IOException, InterruptedException
	{ 
		launch(args);
	}
	
	public static void show()
	{
		mainStage.show();
	}
	
	private void checkPaths() throws IOException
	{
		File logs = new File(logs_path);
		if(!logs.exists())
		{
			logs.mkdirs();
			System.out.println("Created logs folder");
		}
		
		File main = new File(main_path);
		if(!main.exists())
		{
			main.mkdirs();
			System.out.println("Created main folder");
		}
		
		File prop = new File(props_path);
		if(!prop.exists())
		{
			prop.createNewFile();
			Files.copy(getClass().getResourceAsStream("/launcher/launcher.properties"), prop.toPath(), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Copyed properties");
		}
		
		File temp = new File(temp_path);
		if(!temp.exists())
		{
			temp.mkdirs();
			System.out.println("Created temp folder");
		}
		
		File game = new File(game_path);
		if(!game.exists())
		{
			game.mkdirs();
			System.out.println("Created game folder");
		}
	}
	
	public static String getPropsPath()
	{
		return props_path;
	}
	
	public static String getMainPath()
	{
		return main_path;
	}
	
	public static String getTempPath()
	{
		return temp_path;
	}
	
	public static String getGamePath()
	{
		return game_path;
	}
	
	public static void hide()
	{
		mainStage.hide();
	}
	
	public static void setScene(Scene scene)
	{
		mainStage.setScene(scene);
	}
	
	public static Stage getStage()
	{
		return mainStage;
	}
	
	private interface Consumer 
    {
        public void appendText(String text);
    }
	
    private class StreamCapturer extends OutputStream 
	{
		private StringBuilder buffer;
		private Consumer consumer;
		private PrintStream old;

		public StreamCapturer(Consumer consumer, PrintStream old) 
		{
			buffer = new StringBuilder(128);
			this.old = old;
			this.consumer = consumer;
		}

		@Override
		public void write(int b) throws IOException 
		{
			char c = (char) b;
			String value = Character.toString(c);
			buffer.append(value);
			if(value.equals("\n")) 
			{
				consumer.appendText(buffer.toString());
				buffer.delete(0, buffer.length());
			}
			old.print(c);
		}
	}
} 
