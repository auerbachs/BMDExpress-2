package com.sciome.bmdexpress2.shared;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import com.sciome.commons.math.httk.model.CompoundTable;

public class CompoundTableLoader
{

	private static CompoundTableLoader	instance	= null;
	private CompoundTable				compoundTable;

	protected CompoundTableLoader()
	{
		compoundTable = CompoundTable.getInstance();
		// table.loadCombined();
		try
		{
			InputStream fileInputStream = new FileInputStream(
					"./chem_phys_data_combined_with_ice_opera.tsv.gz");
			GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
			compoundTable.loadFromStream(gzipInputStream);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public CompoundTable getCompoundTable()
	{
		return compoundTable;
	}

	public static CompoundTableLoader getInstance()
	{
		if (instance == null)
		{
			instance = new CompoundTableLoader();
		}
		return instance;
	}

}
