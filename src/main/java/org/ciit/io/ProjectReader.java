/*
 * ProjectReader.java
 *
 * Created 8/22/2008
 *
 * Used to read project data from binary file
 *
 */

package org.ciit.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import org.ciit.bmde.BMDConstants;
import org.ciit.data.ArrayMatrixData;
import org.ciit.data.BMDMatrixData;
import org.ciit.data.ModelParameters;
import org.ciit.data.WSMatrixData;

public class ProjectReader
{
	// variables
	private int						rows, cols;
	private File					file		= null;
	private Vector<WSMatrixData>	vectMData	= null;
	private boolean					EOF			= false;
	private String					version;

	private final int				UTFLIMIT	= 65535;
	private final String			SEMICOLON	= ";";

	public ProjectReader()
	{
	}

	public ProjectReader(File f)
	{
		file = f;
	}

	public Vector<WSMatrixData> getVectMData()
	{
		return vectMData;
	}

	public boolean read()
	{

		try
		{
			vectMData = new Vector<WSMatrixData>();

			BufferedInputStream bIn = new BufferedInputStream(new FileInputStream(file), 1024 * 2000);
			DataInputStream in = new DataInputStream(bIn);

			// read in the tag
			String check = in.readUTF();

			if (check.equals(BMDConstants.BMDTAG))
			{ // version 0 check
				version = check;
			}
			else
			{ // file does not start with a recognized label
				return false;
			}

			do
			{// count != null && count > 0) {
				readType(in);
			} while (in.available() > 0);

			in.close();

			return true;

		}
		catch (EOFException e)
		{
			// file has been completely read
			System.out.println("Finished Read on: " + file.getName());
			return true;
		}
		catch (NullPointerException e)
		{}
		catch (Exception e)
		{
			//
		}

		return false;
	}

	private void readType(DataInputStream in)
	{
		try
		{
			WSMatrixData input = readMatrixData(in);

			if (input != null)
			{
				int type = input.getType();

				if (input instanceof BMDMatrixData)
				{
					readModelParams((BMDMatrixData) input, in);
				}

				vectMData.add(input);
			}
		}
		catch (Exception e)
		{
			//
		}
	}

	/**
	 * Read and recreate the ModelParameters for BenchmarkDose
	 */
	private void readModelParams(BMDMatrixData input, DataInputStream in)
	{
		try
		{

			// read in the models vector
			Vector<String> models = new Vector<String>();
			int modelSize = in.readInt();

			for (int i = 0; i < modelSize; i++)
			{
				models.add(in.readUTF());
			}

			ModelParameters inputParams = new ModelParameters(models);

			// read in the probes vector
			// Vector<String> probes = new Vector<String>();
			int probeSize = in.readInt();
			String[] probes = new String[probeSize];

			for (int i = 0; i < probeSize; i++)
			{
				// probes.add(in.readUTF());
				probes[i] = in.readUTF();
			}

			inputParams.setIdentifiers(probes);
			// read in the doses array
			int doseSize = in.readInt();
			double[] doses = new double[doseSize];
			for (int i = 0; i < doseSize; i++)
			{
				doses[i] = in.readDouble();
			}

			inputParams.setDoses(doses);
			String DMatrixName = "";
			// Vector<double[]> resp = null;
			// read in the responses vector
			if (version.equals(BMDConstants.BMDTAG))
			{
				int respS = in.readInt();

				for (int i = 0; i < respS; i++)
				{
					int idxLen = in.readInt();
					// resp.get(i) = new double[idxLen];
					// double[] idxArray = new double[idxLen];
					for (int j = 0; j < idxLen; j++)
					{
						// resp.get(i)[j] = in.readDouble();
						// idxArray[j] = in.readDouble();
						in.readDouble();
					}

				}

				DMatrixName = input.getSource();
			}
			else
			{ // ver.equals(1.0)
				// read in the datamatrix name and retrieve the matrix from
				// the parent BenchmarkDose object
				DMatrixName = in.readUTF();
			}

			inputParams.setDataMatrixName(DMatrixName);
			// read in the modelParams array
			int modelParamsSize = in.readInt();
			// Vector[] modelParams = new Vector[modelParamsSize];
			int max = probeSize * modelParamsSize;
			int cnt = 1;

			for (int i = 0; i < modelParamsSize; i++)
			{
				// find the size of this vector
				int rows = in.readInt();
				double[][] parameters = new double[rows][];

				for (int j = 0; j < rows; j++)
				{
					// find the size of the array in this index of the vector
					int jSlotSize = in.readInt();
					double[] jArray = new double[jSlotSize];

					for (int k = 0; k < jSlotSize; k++)
					{
						// get the data for the array
						jArray[k] = in.readDouble();
					}

					// modelParams[i].add(j,jArray);
					parameters[j] = jArray;
				}

				inputParams.setModelParameters(i, parameters);
			}

			// create the ModelParameters object and set it
			// inputParams.setResponses(resp);
			// inputParams.setModelParams(modelParams);
			input.setModelParameters(inputParams);
			matchDataSource(inputParams, DMatrixName);
			// progressBar.setDone();
		}
		catch (EOFException e)
		{
			EOF = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Used to read original matrix data with name, rows, columns, column headers and matrix data
	 */
	private WSMatrixData readMatrixData(DataInputStream in)
	{
		WSMatrixData input = null;

		try
		{
			// loop the data retrieval
			// first the name, then rows and cols
			int type = in.readInt();
			String name = in.readUTF();

			String msg = "Read " + BMDConstants.dataTypes[type];

			if (type == 0)
			{
				input = new ArrayMatrixData(name);
			}
			else if (type == 2)
			{
				input = new BMDMatrixData(name);
				/*
				 * } else if (type == 4 || type == 6) { input = new PWMatrixData(name); } else if (type == 7)
				 * { input = new ClusterMatrixData(name);
				 */
			}
			else
			{
				input = new WSMatrixData(name);
			}

			int rows = in.readInt();
			int cols = in.readInt();
			Vector<String[]> vecData = new Vector<String[]>();

			String[] headers = new String[cols];

			// loop to read in the headers
			for (int q = 0; q < cols; q++)
			{
				headers[q] = in.readUTF();
			}

			// loops to read in the data
			for (int k = 0; k < rows; k++)
			{ // rows
				String[] rowH = new String[cols];
				for (int j = 0; j < cols; j++)
				{ // cols
					// rowH[j] = in.readUTF();
					String st = in.readUTF();

					while (st.endsWith(SEMICOLON))
					{
						String st2 = in.readUTF();
						st = st.concat(st2);
						// System.out.println(st.length());
					}

					rowH[j] = st;
				}
				vecData.add(rowH);
			}

			boolean hasNote = in.readBoolean();

			if (hasNote)
			{
				// a note exists
				String noteH = new String(in.readUTF());
				input.setNote(noteH);
			}

			// put into a MatrixData object
			input.setType(type);
			input.setColumnNames(headers);
			input.setHasHeaders(true);
			input.setData(vecData);
			input.numeralColumns();
			// prevent memory leak used by vecData after convesion
			input.emptyVectorData();
		}
		catch (EOFException e)
		{
			EOF = true;
		}
		catch (Exception e)
		{
			// ExceptionDialog.showError(parent, "Read Matrix Data", e);
			e.printStackTrace();
		}

		return input;
	}

	/**
	 * Added May 18, 2011 Used dataNodes[0] as the expression data sources to match srcName
	 *
	 * @param inputParams
	 *            is an object of ModelParameters
	 * @param srcName
	 *            is the name of the data source of the ModelParameters object
	 */
	private void matchDataSource(ModelParameters inputParams, String srcName)
	{
		int cnt = vectMData.size();

		for (int i = 0; i < cnt; i++)
		{
			WSMatrixData wsMData = vectMData.get(i);

			if (wsMData != null && wsMData instanceof ArrayMatrixData)
			{
				ArrayMatrixData mData = (ArrayMatrixData) wsMData;

				if (mData != null && mData.getName().equals(srcName))
				{
					inputParams.setDataMatrix(mData);
					break;
				}
			}
		}
	}
}
