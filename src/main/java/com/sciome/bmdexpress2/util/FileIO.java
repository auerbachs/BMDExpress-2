/**
 * FileIO.java
 */

//package org.ciit.commons.util;
package com.sciome.bmdexpress2.util;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.Trimmer;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class FileIO
{
	public FileIO()
	{
	}

	public static String readString(File file, String lineSeparator)
	{
		StringBuffer bf = new StringBuffer("");

		try
		{
			FileReader infile = new FileReader(file);
			BufferedReader br = new BufferedReader(infile);
			String line;

			try
			{
				while ((line = br.readLine()) != null)
				{
					bf.append(line + lineSeparator);
				}
			}
			catch (IOException e)
			{
				System.out.println("Read preferences problem: " + e);
			}
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Find preferences problem: " + e);
		}

		return bf.toString();
	}

	public static Vector<String> readVectorString(File file)
	{
		Vector<String> vect = new Vector<String>();

		try
		{
			FileReader infile = new FileReader(file);
			BufferedReader br = new BufferedReader(infile);
			String line;

			try
			{
				while ((line = br.readLine()) != null)
				{
					vect.add(line.trim());
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		return vect;
	}

	public static File chooseInputFile(Window owner, String title, String path)
	{
		if (path == null)
		{
			path = ".";
		}

		File file = new File(path);

		if (!file.exists())
		{
			file = new File(".");
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Tab delimited", "*.txt"));
		File initialDirectory = new File(BMDExpressProperties.getInstance().getDefinedPath());
		if (initialDirectory.exists())
			fileChooser.setInitialDirectory(initialDirectory);
		File selectedFile = fileChooser.showOpenDialog(owner);

		if (selectedFile != null)
		{
			BMDExpressProperties.getInstance().setDefinedPath(selectedFile.getParent());
		}
		return selectedFile;
	}

	public static String trimExtension(String name)
	{
		int idx = name.lastIndexOf(".");

		if (idx > 0)
		{
			name = name.substring(0, idx);
		}

		return name;
	}

	public static MatrixData readFileMatrix(Component owner, File infile)
	{
		try
		{
			FileReader fr = new FileReader(infile);
			BufferedReader br = new BufferedReader(fr);
			Vector<String[]> vecData = new Vector<String[]>();
			StringBuffer bfNotes = new StringBuffer();
			String line = "";
			int c = 0;

			try
			{
				while ((line = br.readLine()) != null)
				{
					if (line.indexOf("\t") >= 0)
					{
						String[] array = line.split("\t");
						int n = array.length;
						vecData.add(array);

						if (n > c)
						{
							c = n;
						}
					}
					else
					{
						bfNotes.append(line + "\n");
					}
				}

				String[] headers = new String[c];

				for (int j = 0; j < c; j++)
				{
					headers[j] = "Column " + j;
				}

				if (vecData.size() > 0)
				{
					String name = Trimmer.trimEnds(infile.getName(), ".txt");
					MatrixData matrix = new MatrixData(name);
					matrix.setColumnNames(headers);
					matrix.setData(vecData);
					matrix.setHasHeaders(true);

					if (bfNotes.length() > 0)
					{
						matrix.setNote(bfNotes.toString());
					}

					return matrix;
				}
				else
				{
					JOptionPane.showMessageDialog(owner, "There is not any data available.");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		return null;
	}

}