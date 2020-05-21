package com.sciome.bmdexpress2.util.categoryanalysis.defined;

import com.sciome.bmdexpress2.util.MatrixData;

public class DefinedCategoryFileParameters
{
	private MatrixData	matrixData;
	private String		fileName;
	private int[]		usedColumns;

	public MatrixData getMatrixData()
	{
		return matrixData;
	}

	public void setMatrixData(MatrixData matrixData)
	{
		this.matrixData = matrixData;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public int[] getUsedColumns()
	{
		return usedColumns;
	}

	public void setUsedColumns(int[] usedColumns)
	{
		this.usedColumns = usedColumns;
	}

}
