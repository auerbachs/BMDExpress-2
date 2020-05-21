/**
 * ModelParameters.java
 *
 */

package org.ciit.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.ciit.bmds.ModelsUtil;

public class ModelParameters
{
	private Vector<String>				models;
	private String[]					identifiers;
	private Hashtable<String, Integer>	idsHash	= null;
	private int							maxRow	= 0, numModels;
	private double[]					doses;
	private double[][][]				modelParams;
	private String[][]					modelColNames;			// used only for file IO
	private String						DMatrixName;
	private ArrayMatrixData				arrayMatrix;			// used (with its ArrayAnnotation) to get
																// reponse data

	public ModelParameters()
	{
	}

	public ModelParameters(Vector<String> models)
	{
		this.models = models;
		numModels = models.size();
		modelColNames = new String[numModels][];
		modelParams = new double[numModels][][];
		arrayMatrix = null;
		DMatrixName = "";

		for (int i = 0; i < numModels; i++)
		{
			modelColNames[i] = ModelsUtil.createModelColumns(models.get(i));
		}
	}

	public ModelParameters(ArrayMatrixData inD, Vector<String> models)
	{
		this(models);

		arrayMatrix = inD;
		DMatrixName = arrayMatrix.getName();
	}

	public void setIdentifiers(String[] ids)
	{
		identifiers = ids;
		int n = identifiers.length;
		idsHash = new Hashtable<String, Integer>(n + n / 4);

		for (int i = 0; i < n; i++)
		{
			idsHash.put(identifiers[i], Integer.valueOf(i));
		}
	}

	public String[] getIdentifiers()
	{
		return identifiers;
	}

	public void setDoses(double[] doses)
	{
		this.doses = doses;
	}

	public void setDataMatrix(ArrayMatrixData inD)
	{
		this.arrayMatrix = inD;
		this.DMatrixName = inD.getName();
	}

	public void setDataMatrixName(String inName)
	{
		this.DMatrixName = inName;
	}

	public void addModel(String model)
	{
		models.add(model);
	}

	public Vector<String> getModels()
	{
		return models;
	}

	public void setModelParameters(int modelIdx, double[][] parameters)
	{
		modelParams[modelIdx] = parameters;
	}

	public String getDataMatrixName()
	{
		return DMatrixName;
	}

	public int identifierIndex(String id)
	{
		return idsHash.get(id).intValue();
	}

	public Vector<String> modelNames()
	{
		return models;
	}

	public String[] idNames()
	{
		return identifiers;
	}

	public double[] getDoses()
	{
		return doses;
	}

	/**
	 * build and return a vector matching the old Vector<double[]>() public Vector
	 * <double[]> getResponseVector(){ Vector<double[]> ret = new Vector<double[]>();
	 * 
	 * int len = identifiers.length;
	 * 
	 * for(int i = 0; i < len; i++){ int row = arrayMatrix.getArrayAnnotation().indexOfProbe(identifiers[i]);
	 * ret.add(arrayMatrix.getResponses()[row]); }
	 * 
	 * return ret; }
	 */

	public double[][][] getModelParams()
	{
		return modelParams;
	}

	public double[] getResponses(String probe)
	{
		/*
		 * if(arrayMatrix == null){ return null; }
		 * 
		 * int row = arrayMatrix.getArrayAnnotation().indexOfProbe(probe); int columns =
		 * arrayMatrix.getResponses()[row].length; double[] ret = new double[columns];
		 * 
		 * for(int i = 0; i < columns; i++){ ret[i] = arrayMatrix.getResponses()[row][i]; }
		 * 
		 * return ret;
		 */
		return arrayMatrix.getResponses(probe);
	}

	public double[] getParameters(String model, String id)
	{
		double[] params = null;
		int m = models.indexOf(model);

		if (m >= 0)
		{
			int i = identifierIndex(id);

			if (i >= 0)
			{
				params = modelParams[m][i]; // (double[])
			}
		}

		return params;
	}

	public double[] getParameters(int modelIdx, int idIdx)
	{
		return modelParams[modelIdx][idIdx];
	}

	public String[] getModelColumns(String model)
	{
		int m = models.indexOf(model);

		if (m >= 0)
		{
			return modelColNames[m];
		}
		else
		{
			return null;
		}
	}

	/**
	 * Write models and parameters to file
	 */
	public void writeToFile(File file, boolean append)
	{
		String newline = "\n", tab = "\t";

		try
		{
			FileWriter out = new FileWriter(file, append);
			out.write("Parameters" + newline);

			if (identifiers != null && models != null)
			{
				for (int i = 0; i < numModels; i++)
				{
					out.write("Model: " + models.get(i) + newline);

					if (modelParams[i] != null)
					{
						for (int j = 0; j < modelParams[i].length; j++)
						{
							double[] params = modelParams[i][j];

							if (params != null)
							{
								out.write(identifiers[j]);

								for (int k = 0; k < params.length; k++)
								{
									out.write(tab + params[k]);
								}

								out.write(newline);
							}
						}
					}
				}
			}

			out.close();
		}
		catch (IOException e)
		{
			// String title = "Write to File";
			// ExceptionDialog.showError(null, title, e);
		}
	}
}
