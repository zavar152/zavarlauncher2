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
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.progress.ProgressMonitor.Result;
import util.ErrorWindow;

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
	
	
	public void downloadInstance()
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
			ErrorWindow.show(e);
		}
		
		try 
		{
			rbc = Channels.newChannel(website.openStream());
		} 
		catch (IOException e1) 
		{
			ErrorWindow.show(e1);
		}
		
		try 
		{
			fos = new FileOutputStream(Launcher.getTempPath() + "/" + name);
		} 
		catch (FileNotFoundException e) 
		{
			ErrorWindow.show(e);
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
			System.out.println("Canceled downloading " + name);
		} 
		catch (IOException e) 
		{
			ErrorWindow.show(e);
		}
	}
	
	@Override
	public void run() 
	{
		try 
		{
			state = "Downloading";
			System.out.println("Downloading " + name);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			state = "Downloaded";
			System.out.println("Downloaded " + name);
			downloaded = true;
			if(!canceled)
			{
				zipFile = new ZipFile(Launcher.getTempPath() + "/" + name);
				state = "Unzipping";
				System.out.println("Unzipping " + name);
				zipFile.extractAll(extract);
				state = "Unzipped";
				unzipped = true;
				System.out.println("Unzipped " + name);
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
			ErrorWindow.show(e);
		}
	}
}


