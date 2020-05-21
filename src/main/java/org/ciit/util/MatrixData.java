/**
 * MatrixData.java
 *
 * Created 7/21/2006
 * Last Modified 11/21/2006
 * Author: Longlong Yang
 */

package org.ciit.util;

//import org.ciit.util.parser.PropertiesParser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class MatrixData
{
	public String			name, note, srcName;
	public String[]			columnNames;
	public Object[][]		data;
	public Vector<String[]>	vData;
	public Vector<String[]>	vHeaders;
	public boolean			hasHeaders	= false,
									allString = false;
	public int				rows, cols, type;

	public MatrixData()
	{
		this("New", null, null);
	}

	public MatrixData(String name)
	{
		this(name, null, null);
	}

	public MatrixData(String name, String[] colNames, Object[][] dt)
	{
		this.name = name;
		columnNames = colNames;
		data = dt;
		type = -1;
		srcName = null;

		if (columnNames != null)
		{
			cols = columnNames.length;
		}
		else
		{
			cols = 0;
		}

		if (data != null)
		{
			rows = data.length;
		}
		else
		{
			rows = 0;
		}
	}

	public void setName(String st)
	{
		name = st;
	}

	public void setColumnNames(String[] colNames)
	{
		columnNames = colNames;
		cols = columnNames.length;
	}

	public void removeHeaders()
	{
		String[] rowDt = vData.remove(0);

		if (rowDt.length == columnNames.length)
		{
			if (vHeaders == null)
			{
				columnNames = rowDt;
				hasHeaders = false;
				vHeaders = new Vector<String[]>();
			}
			else
			{
				vHeaders.add(rowDt);
			}

			convertVector();
		}
		else
		{
			appendToNote(rowDt);
		}
	}

	private void appendToNote(String[] array)
	{
		StringBuffer bf = new StringBuffer();

		if (note != null)
		{
			bf.append(note + "\n");
		}

		for (int i = 0; i < array.length; i++)
		{
			if (i > 0)
			{
				bf.append("\t");
			}

			bf.append(array[i]);
		}

		bf.append("\n");

		note = bf.toString();
	}

	public void setHasHeaders(boolean bool)
	{
		hasHeaders = bool;
	}

	/**
	 * If true, all data will be type String when convert Vector to matrix
	 */
	public void setAllString(boolean bool)
	{
		allString = bool;
	}

	public void setData(Object[][] dt)
	{
		data = dt;
		rows = data.length;
	}

	public void setData(Vector<String[]> dt)
	{
		vData = dt;
		convertVector();
	}

	public void setObjData(Vector<Object[]> dt)
	{
		convertVector(dt);
	}

	public void setSource(String name)
	{
		srcName = name;
	}

	public void setType(int i)
	{
		type = i;
	}

	public void convertVector()
	{
		rows = vData.size();
		cols = columnNames.length;

		data = new Object[rows][cols];

		for (int i = 0; i < rows; i++)
		{
			String[] array = vData.get(i);

			for (int j = 0; j < cols; j++)
			{
				if (j < array.length)
				{
					data[i][j] = array[j];
				}
				else
				{
					data[i][j] = "";
				}
			}
		}
	}

	public void convertVector(Vector<Object[]> dt)
	{
		rows = dt.size();
		cols = columnNames.length;

		data = new Object[rows][cols];

		for (int i = 0; i < rows; i++)
		{
			data[i] = dt.get(i);
		}
	}

	/**
	 * Try to convert string values to Integer or Double objects when a unique object applies to the whole
	 * column, ignoring the first column
	 *
	 * Added 8/27/2008
	 */
	public void numeralColumns()
	{
		if (!allString)
		{
			if (data != null)
			{
				for (int j = 1; j < cols; j++)
				{
					Object[] colValues = columnInteger(j);

					if (colValues == null)
					{
						colValues = columnDouble(j);
					}

					if (colValues != null)
					{
						for (int i = 0; i < colValues.length; i++)
						{
							data[i][j] = colValues[i];
						}

						colValues = null;
					}
				}
			}
		}
	}

	private Object[] columnInteger(int c)
	{
		Object[] colData = new Object[rows];

		for (int i = 0; i < rows; i++)
		{
			String value = (String) data[i][c];

			if (value == null || value.isEmpty())
			{
				colData[i] = null;
			}
			else
			{
				if (value.endsWith(".0"))
				{
					value = value.substring(0, value.length() - 2);
				}

				try
				{
					colData[i] = Integer.parseInt(value);
				}
				catch (Exception e)
				{
					return null;
				}
			}
		}

		return colData;
	}

	private Object[] columnDouble(int c)
	{
		Object[] colData = new Object[rows];

		for (int i = 0; i < rows; i++)
		{
			String value = (String) data[i][c];

			if (value == null || value.isEmpty())
			{
				colData[i] = null;
			}
			else
			{
				if (value.endsWith(".0"))
				{
					value = value.substring(0, value.length() - 2);
				}

				try
				{
					colData[i] = Double.parseDouble(value);
				}
				catch (Exception e)
				{
					return null;
				}
			}
		}

		return colData;
	}

	public void setNote(String nt)
	{
		note = nt;
	}

	public int columns()
	{
		return cols;
	}

	public int rows()
	{
		return rows;
	}

	public Object valueAt(int r, int c)
	{
		return data[r][c];
	}

	public String getName()
	{
		return name;
	}

	public int getType()
	{
		return type;
	}

	public String getSource()
	{
		return srcName;
	}

	public String[] getColumnNames()
	{
		return columnNames;
	}

	public String[] copyColumnNames()
	{
		String[] names = new String[columns()];

		for (int i = 0; i < columns(); i++)
		{
			names[i] = columnNames[i];
		}

		return names;
	}

	public Object[] rowData(int r)
	{
		return data[r];
	}

	public Object[][] getData()
	{
		return data;
	}

	public Vector<String[]> getVectorData()
	{
		return vData;
	}

	public Vector<String[]> getExtraHeaders()
	{
		return vHeaders;
	}

	public String getNote()
	{
		return note;
	}

	public boolean hasHeaders()
	{
		return hasHeaders;
	}

	/**
	 * Return memory usage
	 */
	public void emptyVectorData()
	{
		if (vData != null)
		{
			vData.removeAllElements();
			vData = null;
		}
	}

	public void writeToFile(File file)
	{
		String newline = "\n";

		try
		{
			FileWriter out = new FileWriter(file);

			if (note != null)
			{
				out.write(note + newline);
			}

			out.write(columnNames[0]);

			for (int i = 1; i < cols; i++)
			{
				out.write("\t" + columnNames[i]);
			}

			out.write(newline);

			for (int i = 0; i < rows; i++)
			{
				if (data[i][0] == null)
				{
					out.write("");
				}
				else
				{
					out.write(data[i][0] + "");
				}

				for (int j = 1; j < cols; j++)
				{
					if (data[i][j] == null)
					{
						out.write("\t");
					}
					else
					{
						out.write("\t" + data[i][j]);
					}
				}

				out.write(newline);
			}

			out.close();
		}
		catch (IOException e)
		{
			String title = "Write to File";
			// ExceptionDialog.showError(null, title, e);
			e.printStackTrace();
		}
	}

	/**
	 * For tree node display purpose
	 */
	public String toString()
	{
		return name;

	}
}