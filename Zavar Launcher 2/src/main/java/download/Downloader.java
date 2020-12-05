package download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import application.Launcher;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.progress.ProgressMonitor.Result;

public class Downloader implements Runnable
{
	private File zip;
	private ReadableByteChannel rbc;
	private FileOutputStream fos;
	private URL website;
	private boolean downloaded, unzipped, canceled = false;
	private Thread thr;
	private ZipFile zipFile;
	private String state, extract, name;
	
	
	public void downloadMinecraft()
	{
		thr.start();
	}

	public Downloader(String url, String name, String extract)
	{
		this.extract = extract;
		this.name = name;
		try 
		{
			website = new URL(url);
		} 
		catch (MalformedURLException e)
		{
			Alert alert = new Alert(AlertType.WARNING, e.getMessage());
	        alert.showAndWait();
		}
		
		try 
		{
			rbc = Channels.newChannel(website.openStream());
		} 
		catch (IOException e1) 
		{
			Alert alert = new Alert(AlertType.WARNING, e1.getMessage());
	        alert.showAndWait();
		}
		
		try 
		{
			fos = new FileOutputStream(Launcher.getTempPath() + "/" + name);
		} 
		catch (FileNotFoundException e) 
		{
			Alert alert = new Alert(AlertType.WARNING, e.getMessage());
	        alert.showAndWait();
		}
		zip = new File(Launcher.getTempPath() + "/" + name);
		thr = new Thread(this);
	}
	
	
	public boolean isDownloaded()
	{
		return downloaded;
	}
	
	public boolean isUnzipped()
	{
		return unzipped;
	}
	
	public long getFileSize()
	{
		return zip.length();
	}

	public synchronized Result getZipState()
	{
		return zipFile.getProgressMonitor().getResult();
	}
	
	public String getState()
	{
		return state;
	}
	
	public void cancel()
	{
		canceled = true;
		try 
		{
			rbc.close();
			fos.close();
			zip.delete();
			state = "Canceled";
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() 
	{
		try 
		{
			state = "Downloading";
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			state = "Downloaded";
			downloaded = true;
			if(!canceled)
			{
				zipFile = new ZipFile(Launcher.getTempPath() + "/" + name);
				state = "Unzipping";
				zipFile.extractAll(extract);
				state = "Unzipped";
				unzipped = true;
				System.out.println("unzip");
				rbc.close();
				fos.close();
				zip.delete();
			}
			else
			{
				canceled = false;
			}
		} 
		catch (IOException e) 
		{
			state = "IOException";
			Alert alert = new Alert(AlertType.WARNING, e.getMessage());
	        alert.showAndWait();
		}
	}
}


