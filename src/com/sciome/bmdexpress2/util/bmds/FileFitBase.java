package com.sciome.bmdexpress2.util.bmds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public abstract class FileFitBase
{

	protected void executeModel(String EXE, String fName)
	{
		// System.out.println("Path = " + path);absolutePath
		StringBuilder cmd = new StringBuilder(EXE + " " + fName);
		// System.out.println(cmd.toString());

		try
		{
			long startTime = System.currentTimeMillis();
			Runtime rt = Runtime.getRuntime();

			ProcessBuilder pb = new ProcessBuilder(EXE, new File(fName).getName());

			// set the directory of the process to the dir of the file.
			// for some reason using the shared dll's this is the only way I could get the executables to
			// work.
			pb.directory(new File(fName).getParentFile());
			Process process = pb.start();

			// Process process = rt.exec(EXE + " " + fName, new String[] {}, (new File(EXE)).getParentFile());
			// Process process = rt.exec(cmd.toString());

			try
			{

				// process is executing, but only give it a certain amount of time to execute.
				// give it 30 seconds to complete otherwise kill it.
				boolean processSurvived = false;
				while (System.currentTimeMillis() - startTime < 30000)
				{
					Thread.sleep(1000);
					if (!process.isAlive())
					{
						processSurvived = true;
						break;
					}
				}
				if (!processSurvived)
				{

					System.out.println("Destroying process: " + process.toString());
					process.destroyForcibly();
					System.out.println(getStringFromInputStream(process.getErrorStream()));
				}

			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			// System.out.println(getStringFromInputStream(process.getErrorStream()));
			// System.out.println("time2runt: " + String.valueOf(System.currentTimeMillis() - startTime));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public abstract double[] fitModel(String name, ModelInputParameters inputParameters, float[] inputX,
			float[] inputY);

	private String getStringFromInputStream(InputStream is)
	{

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try
		{

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

	protected File[] listFilesMatching(File root, String regex)
	{
		if (!root.isDirectory())
		{
			throw new IllegalArgumentException(root + " is no directory.");
		}
		final Pattern p = Pattern.compile(regex); // careful: could also throw an exception!
		return root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file)
			{
				return p.matcher(file.getName()).matches();
			}
		});
	}
}
