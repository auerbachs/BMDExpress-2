package com.sciome.bmdexpress2.util.categoryanalysis;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.bmdexpress2.util.MatrixData;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.DefinedCategoryFileParameters;

public class GeneLevelUtils
{

	public static DefinedCategoryFileParameters getProbeFileParameters(DoseResponseExperiment doseExperiment)
	{

		DefinedCategoryFileParameters definedCatFileParameters = new DefinedCategoryFileParameters();
		Vector<String[]> vecData = new Vector<String[]>();
		Map<String, List<String>> probe2Gene = getProbeToGene(doseExperiment);
		for (ReferenceGeneAnnotation refGeneAnn : doseExperiment.getReferenceGeneAnnotations())
		{
			for (ReferenceGene refGene : refGeneAnn.getReferenceGenes())
			{
				String[] row = { refGeneAnn.getProbe().getId(), refGene.getId() };
				vecData.add(row);
			}
		}

		MatrixData matrix = new MatrixData("Probe2Genes");
		matrix.setColumnNames(new String[] { "1", "2" });
		matrix.setData(vecData);
		matrix.setHasHeaders(false);

		definedCatFileParameters.setFileName("Annotated Genes");
		definedCatFileParameters.setMatrixData(matrix);
		definedCatFileParameters.setUsedColumns(new int[] { 0, 1 });
		return definedCatFileParameters;
	}

	public static DefinedCategoryFileParameters getCategoryFileParameters(
			DoseResponseExperiment doseExperiment)
	{

		DefinedCategoryFileParameters definedCatFileParameters = new DefinedCategoryFileParameters();
		Vector<String[]> vecData = new Vector<String[]>();
		for (ReferenceGeneAnnotation refGeneAnn : doseExperiment.getReferenceGeneAnnotations())
		{
			for (ReferenceGene refGene : refGeneAnn.getReferenceGenes())
			{
				String[] row = { refGene.getId(), refGene.getGeneSymbol(), refGene.getId() };
				vecData.add(row);
			}
		}
		MatrixData matrix = new MatrixData("Genes2Categories");
		matrix.setColumnNames(new String[] { "1", "2", "3" });
		matrix.setData(vecData);
		matrix.setHasHeaders(false);

		definedCatFileParameters.setFileName("Annotated Genes");
		definedCatFileParameters.setMatrixData(matrix);
		definedCatFileParameters.setUsedColumns(new int[] { 0, 1, 2 });
		return definedCatFileParameters;
	}

	private static Map<String, List<String>> getProbeToGene(DoseResponseExperiment de)
	{
		return null;
	}
}
