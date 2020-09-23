package download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor.Result;

public class DownloadZIP implements Runnable
{
	private File zip;
	private ReadableByteChannel rbc;
	private FileOutputStream fos;
	private URL website;
	private boolean downloaded, unzipped = false;
	private Thread thr;
	private ZipFile zipFile;
	private String state;
	
	
	public void downloadMinecraft() throws Exception
	{
		thr.start();
	}

	public DownloadZIP(String url, String dest) throws IOException 
	{
		website = new URL(url);
		rbc = Channels.newChannel(website.openStream());
		fos = new FileOutputStream(dest);
		zip = new File(dest);
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
	
	@Override
	public void run() 
	{
		try {
			state = "Downloading";
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			state = "Downloaded";
			downloaded = true;
			zipFile = new ZipFile("G:\\JavaDownload\\MinecraftFont.zip");
			state = "Unzipping";
			zipFile.extractAll("G:\\JavaDownload\\");
			state = "Unzipped";
			unzipped = true;
			System.out.println("unzip");
			rbc.close();
			fos.close();
		} 
		catch (IOException e) 
		{
			state = "IOException";
		}
		}
    }


