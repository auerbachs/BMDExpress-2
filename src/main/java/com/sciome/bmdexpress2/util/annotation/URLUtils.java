/**
 * URLUtils.java
 *
 * Created 8/25/2008
 * @Author Longlong Yang
 */

package com.sciome.bmdexpress2.util.annotation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/*
 * download data from URLs and save it to local files.
 * Run like this
 * java URLUtils http://schmidt.devlib.org/java/file-download.html
 */
public class URLUtils
{

	/**
	 * Check parent path, create if not exists
	 */
	public static void checkPath(File file)
	{
		File parentFile = file.getParentFile();

		if (!parentFile.exists())
		{
			parentFile.mkdirs();
		}
	}

	public static FileInfo httpAccessible(String address)
	{
		FileInfo fInfo = new FileInfo();

		try
		{
			URL url = new URL(address);
			URLConnection conn = url.openConnection();
			conn.connect();
			fInfo.setAccessible(true);
		}
		catch (Exception e)
		{
			fInfo.setException(e);
		}

		return fInfo;
	}

	/**
	 * Check remote file information of last modified date and file size
	 */
	public static boolean updateAvailable(String address, File localFile)
	{
		FileInfo fInfo = new FileInfo(address, localFile);
		URLUtils.updateAvailable(fInfo);

		return fInfo.getLastModified() > 0;
	}

	/**
	 * Check remote file information of last modified date and file size
	 */
	public static FileInfo updateAvailable(FileInfo fInfo)
	{
		try
		{
			URL url = new URL(fInfo.getHttpURL());
			URLConnection conn = url.openConnection();
			long date = conn.getLastModified();

			if (fInfo.getFile().exists() && fInfo.getFile().lastModified() >= date)
			{
				// date = 0;
			}

			fInfo.setLastModified(date);
			fInfo.setSize(conn.getContentLength());
			fInfo.setAccessible(true);
		}
		catch (Exception e)
		{
			fInfo.setException(e);
		}

		return fInfo;
	}

	/**
	 * Download file from url and write to a local file
	 * 
	 * @param address
	 *            is the url string of a file to be downloaded
	 * @param fileName
	 *            is the name of a local file to be written
	 *
	 * @return FileInfo containing features of original file such as
	 *         last modified date and recording exception if any
	 */
	public static FileInfo download(String address, String fileName)
	{
		File file = new File(fileName);

		return download(address, file);
	}

	/*
	 * Download file from url and write to a local file
	 * 
	 * @param address is the url of file to be downloaded
	 * 
	 * @param localFile is the local file to be written
	 *
	 * @return FileInfo containing features of original file such as
	 * last modified date and recording exception if any
	 */
	public static FileInfo download(String address, File localFile)
	{
		FileInfo fInfo = new FileInfo(address, localFile);

		return URLUtils.download(fInfo);
	}

	/*
	 * Download file from url and write to a local file
	 * 
	 * @param address is the url of file to be downloaded
	 * 
	 * @param localFile is the local file to be written
	 *
	 * @return long value of date last modified of original file
	 */
	public static FileInfo download(FileInfo fInfo)
	{
		File localFile = fInfo.getFile();
		checkPath(localFile);
		OutputStream output = null;
		URLConnection conn = null;
		InputStream input = null;

		try
		{
			URL url = new URL(fInfo.getHttpURL());
			conn = url.openConnection();
			input = conn.getInputStream();

			output = new BufferedOutputStream(new FileOutputStream(localFile));
			byte[] buffer = new byte[1024];
			int numRead;
			long numWritten = 0;

			while ((numRead = input.read(buffer)) != -1)
			{
				output.write(buffer, 0, numRead);
				numWritten += numRead;
			}

			fInfo.setLastModified(conn.getLastModified());
			// fInfo.setSize(conn.getContentLength());
			// fInfo.setAccessible(true);
		}
		catch (Exception e)
		{
			fInfo.setException(e);
		}
		finally
		{
			try
			{
				if (input != null)
				{
					input.close();
				}

				if (output != null)
				{
					output.close();
				}
			}
			catch (IOException ioe)
			{
				fInfo.setException(ioe);
			}
		}

		return fInfo;
	}

	/**
	 * Check if local file exists, if not then download from http
	 *
	 * @param http
	 *            is the URL address of a remote host/server
	 * @param path
	 *            is the relative path to the file (local and remote)
	 * @param fName
	 *            is the name of the file
	 *
	 * @param return
	 *            an instance of FileInfo
	 */
	public static FileInfo checkDownload(String http, String path, String fName)
	{
		File inFile = new File(path);

		if (!inFile.exists())
		{
			inFile.mkdirs();
		}

		inFile = new File(inFile.getAbsolutePath(), fName);
		FileInfo fInfo = new FileInfo(http, inFile);

		if (!inFile.exists())
		{
			// FileInfo fInfo = URLUtils.download(http, inFile);
			fInfo = URLUtils.download(fInfo);

			if (fInfo.getLastModified() > 0)
			{
				inFile.setLastModified(fInfo.getLastModified());
			} /*
				 * else {
				 * if (fInfo.getException() != null) {
				 * parent.showException("Download File - " + fName,
				 * fInfo.getException());
				 * }
				 * }
				 */
		}

		return fInfo;
	}

	/**
	 * Use the original file's name as local
	 */
	public static void download(String address)
	{
		int lastSlashIndex = address.lastIndexOf('/');

		if (lastSlashIndex >= 0 && lastSlashIndex < address.length() - 1)
		{
			String fileName = address.substring(lastSlashIndex + 1);
			download(address, fileName);
		}
		else
		{
			System.err.println("Could not figure out local file name for " + address);
		}
	}

	/**
	 * For testing to download from an URL address
	 */
	public static void main(String[] args)
	{
		for (int i = 0; i < args.length; i++)
		{
			download(args[i]);
		}
	}
}
