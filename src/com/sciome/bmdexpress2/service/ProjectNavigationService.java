package com.sciome.bmdexpress2.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.probe.Probe;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.model.refgene.EntrezGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.HillResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.serviceInterface.IProjectNavigationService;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.bmdexpress2.util.annotation.FileAnnotation;

public class ProjectNavigationService implements IProjectNavigationService{

	private final int	MAX_FILES_FOR_MULTI_EXPORT	= 10;
	
	@SuppressWarnings("unchecked")
	public void assignArrayAnnotations(ChipInfo chipInfo, List<DoseResponseExperiment> experiments,
			FileAnnotation fileAnnotation)
	{

		// set up the analysis information
		for (DoseResponseExperiment doseResponseExperiment : experiments)
		{
			AnalysisInfo analysisInfo = new AnalysisInfo();
			List<String> notes = new ArrayList<>();

			if (chipInfo == null)
			{
				notes.add("Chip: Generic");
				chipInfo = new ChipInfo();
				chipInfo.setName("Generic");
				chipInfo.setSpecies("Generic");
				chipInfo.setProvider("Generic");
				chipInfo.setId("Generic");

			}
			else
			{
				notes.add("Chip: " + chipInfo.getGeoName());
				notes.add("Provider: " + chipInfo.getProvider());
			}
			notes.add("Log Transformation: " + doseResponseExperiment.getLogTransformation());
			notes.add("BMDExpress2 Version: " + BMDExpressProperties.getInstance().getVersion());
			notes.add("Timestamp: " + BMDExpressProperties.getInstance().getTimeStamp());
			analysisInfo.setNotes(notes);
			doseResponseExperiment.setAnalysisInfo(analysisInfo);

			doseResponseExperiment.setChip(chipInfo);

			// try to avoid storing duplicate genes.
			Map<String, ReferenceGene> refCache = new HashMap<>();
			List<ReferenceGeneAnnotation> referenceGeneAnnotations = new ArrayList<>();
			// if there is no chip selected, the set it as Generic and load empty
			// referencegeneannotation
			if (chipInfo.getName().equals("Generic"))
			{
				doseResponseExperiment.setReferenceGeneAnnotations(referenceGeneAnnotations);
				continue;
			}
			fileAnnotation.setChip(chipInfo.getGeoID());
			fileAnnotation.arrayProbesGenes();
			fileAnnotation.arrayGenesSymbols();

			fileAnnotation.getGene2ProbeHash();

			Hashtable<String, Vector> probesToGene = fileAnnotation.getProbe2GeneHash();
			Hashtable<String, String> geneSymbolHash = fileAnnotation.getGene2SymbolHash();

			try
			{

				// let's create referenceGeneAnnotations
				for (ProbeResponse probeResponse : doseResponseExperiment.getProbeResponses())
				{
					Probe probe = probeResponse.getProbe();
					Vector<String> genes = probesToGene.get(probe.getId());
					ReferenceGeneAnnotation referenceGeneAnnotation = new ReferenceGeneAnnotation();
					List<ReferenceGene> referenceGenes = new ArrayList<>();
					if (genes == null)
						continue;
					for (String gene : genes)
					{
						ReferenceGene refGene = refCache.get(gene);
						if (refGene == null)
						{
							refGene = new EntrezGene();
							refGene.setId(gene);
							refGene.setGeneSymbol(geneSymbolHash.get(gene));
							refCache.put(gene, refGene);
						}
						referenceGenes.add(refGene);
					}
					referenceGeneAnnotation.setReferenceGenes(referenceGenes);
					referenceGeneAnnotation.setProbe(probe);

					referenceGeneAnnotations.add(referenceGeneAnnotation);
				}

				doseResponseExperiment.setReferenceGeneAnnotations(referenceGeneAnnotations);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}
	
	@Override
	public String exportMultipleFiles(Map<String, Set<BMDExpressAnalysisDataSet>> header2Rows, File selectedFile) {
		if (header2Rows.keySet().size() > MAX_FILES_FOR_MULTI_EXPORT)
		{
			BMDExpressEventBus.getInstance().post(new ShowErrorEvent(
					"There are too many distinct data sets being created due to varying column headers.  There are "
							+ header2Rows.keySet().size()
							+ " files to be created but there can only be a maximum of "
							+ MAX_FILES_FOR_MULTI_EXPORT
							+ ".  Please reduce the number of distinct datasets that you wish to export."));
			return "";
		}
		String filesCreateString = "The following file was created: ";
		if (header2Rows.keySet().size() > 1)
			filesCreateString = "The following files were created (please be aware the that multiple files were generated due to varying column headers : ";
	
		String fileName = selectedFile.getAbsolutePath();
		String fileNameWOExtension = fileName.replaceAll("\\.txt$", "");
		List<String> filesThatWereCreated = new ArrayList<>();
		int i = 0;
		for (String key : header2Rows.keySet())
		{
			BufferedWriter writer = null;
			i++;
			try
			{
				// if there are datasets with multiple headers, then we need to create separate files for each
				if (header2Rows.keySet().size() > 1)
					selectedFile = new File(fileNameWOExtension + "-" + i + ".txt");
				writer = new BufferedWriter(new FileWriter(selectedFile), 1024 * 2000);
				Set<BMDExpressAnalysisDataSet> dataSets = header2Rows.get(key);
				filesThatWereCreated.add(selectedFile.getName());
				boolean started = false;
				for (BMDExpressAnalysisDataSet dataSet : dataSets)
				{
					if (dataSet instanceof BMDExpressAnalysisDataSet)
					{
						if (!started) // this will only allow the unique header to be written once.
						{
							// this ensures the row data is filled.
							List<String> header = dataSet.getColumnHeader();
							// write the type of data being exported.
							// write the header.
							writer.write("Analysis\t");
							writer.write(String.join("\t", header) + "\n");
						}
						writer.write(exportBMDExpressAnalysisDataSet(dataSet, true));
					}
					else if (dataSet instanceof DoseResponseExperiment)
					{
						writer.write(getExperimentToWrite((DoseResponseExperiment) dataSet, true));
					}
					started = true;
				}
				writer.close();
	
			}
			catch (IOException e)
			{
				BMDExpressEventBus.getInstance().post(new ShowErrorEvent(
						"There are too many distinct data sets being created due to varying column headers.  There are "
								+ header2Rows.keySet().size()
								+ " files to be created but there can only be a maximum of "
								+ MAX_FILES_FOR_MULTI_EXPORT
								+ ".  Please reduce the number of distinct datasets that you wish to export."));
				e.printStackTrace();
			}
	
		}
		filesCreateString += String.join(",", filesThatWereCreated);
		return filesCreateString;
	}
	
	public void exportBMDExpressAnalysisDataSet(BMDExpressAnalysisDataSet bmdResults, File selectedFile)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile), 1024 * 2000);
			writer.write(String.join("\n", bmdResults.getAnalysisInfo().getNotes()));
			writer.write("\n");
			writer.write(String.join("\t", bmdResults.getColumnHeader()) + "\n");
			writer.write(exportBMDExpressAnalysisDataSet(bmdResults, false));
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	private String exportBMDExpressAnalysisDataSet(BMDExpressAnalysisDataSet bmdResults, boolean prepend)
	{
		StringBuffer sb = new StringBuffer();

		for (BMDExpressAnalysisRow result : bmdResults.getAnalysisRows())
		{
			if (prepend)
				sb.append(bmdResults.getName() + "\t");
			sb.append(joinRowData(result.getRow(), "\t") + "\n");
		}
		return sb.toString();
	}
	
	public void exportDoseResponseExperiment(DoseResponseExperiment doseResponseExperiment, File selectedFile)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile), 1024 * 2000);
			writer.write(String.join("\n", doseResponseExperiment.getAnalysisInfo().getNotes()));
			writer.write("\n");
			writer.write(getExperimentToWrite(doseResponseExperiment, false));
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	public void exportBMDResultBestModel(BMDResult bmdResults, File selectedFile)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile), 1024 * 2000);
			writer.write(String.join("\n", bmdResults.getAnalysisInfo().getNotes()));
			writer.write("\n");

			boolean hasHill = false;
			for (ProbeStatResult result : bmdResults.getProbeStatResults())
			{
				if (result.getBestStatResult() != null && result.getBestStatResult() instanceof HillResult)
				{
					hasHill = true;
					break;
				}
			}

			writer.write("Probe Id\tBMDS Model\t");
			writer.write("\tGenes\tGene Symbols\t");
			writer.write("BMD\tBMDL\tBMDU\tfitPValue\tfitLogLikelihood\tAIC\tadverseDirection\t2BMD/BMDL");
			if (hasHill)
				writer.write("\tFlagged Hill");
			writer.write("\n");
			for (ProbeStatResult result : bmdResults.getProbeStatResults())
			{
				if (result.getBestStatResult() != null)
				{
					writer.write(result.getProbeResponse().getProbe().getId() + "\t"
							+ result.getBestStatResult() + "\t");
					writer.write("\t" + result.getGenes() + "\t" + result.getGeneSymbols() + "\t");
					writer.write(joinRowData(result.getBestStatResult().getRow(), "\t"));
					if (!(result.getBestStatResult() instanceof HillResult))
					{// add an extra column on account of hill's k-flag
						writer.write("\t");
					}
					writer.write("\n");
				}
				else
				{
					writer.write(result.getProbeResponse().getProbe().getId() + "\t" + "none");
					writer.write("\t" + result.getGenes() + "\t" + result.getGeneSymbols() + "\n");
				}
			}
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void exportModelParameters(BMDProject bmdProject)
	{
		File selectedFile = new File("/tmp/modelParams.txt");
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile), 1024 * 2000);

			for (BMDResult bmdResults : bmdProject.getbMDResult())
			{

				for (ProbeStatResult result : bmdResults.getProbeStatResults())
				{
					for (StatResult statResult : result.getStatResults())
					{
						writer.write(bmdResults.getName() + "\t"
								+ result.getProbeResponse().getProbe().getId() + "\t"
								+ result.getBestStatResult().toString() + "\t" + statResult.toString());
						double[] params = statResult.getCurveParameters();

						for (int i = 0; i < params.length; i++)
						{
							writer.write("\t" + params[i]);
						}

						for (String pname : statResult.getParametersNames())
						{
							writer.write("\t" + pname);
						}

						writer.write("\n");
					}
				}

			}

			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Object[][] showProbeToGeneMatrix(DoseResponseExperiment doseResponseExperiment)
	{

		Object[][] matrixData = null;
		if (doseResponseExperiment.getReferenceGeneAnnotations() != null)
		{
			matrixData = new Object[doseResponseExperiment.getReferenceGeneAnnotations().size()][];

			int i = 0;
			for (ReferenceGeneAnnotation refGeneAnnotation : doseResponseExperiment
					.getReferenceGeneAnnotations())
			{
				StringBuilder symbolBuilder = new StringBuilder();
				StringBuilder geneBuilder = new StringBuilder();
				String probeId = refGeneAnnotation.getProbe().getId();
				for (ReferenceGene refGene : refGeneAnnotation.getReferenceGenes())
				{
					if (symbolBuilder.length() > 0)
					{
						symbolBuilder.append(";");
						geneBuilder.append(";");
					}
					symbolBuilder.append(refGene.getGeneSymbol());
					geneBuilder.append(refGene.getId());
				}
				Object[] rowData = { probeId, geneBuilder.toString(), symbolBuilder.toString() };
				matrixData[i] = rowData;
				i++;
			}
		}
		else
			matrixData = new Object[0][];
		
		return matrixData;
	}
	
	public Object[][] showGenesToProbeMatrix(DoseResponseExperiment doseResponseExperiment)
	{

		Object[][] matrixData = null;
		if (doseResponseExperiment.getReferenceGeneAnnotations() != null)
		{
			Map<ReferenceGene, List<String>> geneProbeMap = new HashMap<>();

			for (ReferenceGeneAnnotation refGeneAnnotation : doseResponseExperiment
					.getReferenceGeneAnnotations())
			{
				for (ReferenceGene refGene : refGeneAnnotation.getReferenceGenes())
				{
					if (!geneProbeMap.containsKey(refGene))
					{
						geneProbeMap.put(refGene, new ArrayList<>());
					}
					geneProbeMap.get(refGene).add(refGeneAnnotation.getProbe().getId());
				}
			}
			matrixData = new Object[geneProbeMap.keySet().size()][];
			int i = 0;

			for (ReferenceGene refGeneKey : geneProbeMap.keySet())
			{
				Object rowData[] = { refGeneKey.getId(), refGeneKey.getGeneSymbol(),
						String.join(";", geneProbeMap.get(refGeneKey)) };
				matrixData[i] = rowData;
				i++;
			}
		}
		else
		{
			matrixData = new Object[0][];
		}
		return matrixData;
	}
	
	private String joinRowData(List<Object> datas, String delimiter)
	{
		StringBuffer bf = new StringBuffer();
		int i = 0;
		if (datas == null)
		{
			return "";
		}
		for (Object data : datas)
		{
			if (data != null)
			{
				bf.append(data);
			}

			if (i < datas.size())
			{
				bf.append(delimiter);
			}
		}

		return bf.toString();
	}
	
	private String getExperimentToWrite(DoseResponseExperiment doseResponseExperiment, boolean prependname)
	{
		StringBuffer sb = new StringBuffer();
		List<String> row = new ArrayList<>();
		row.add("Something");

		for (Treatment treatment : doseResponseExperiment.getTreatments())
		{
			row.add(treatment.getName());
		}
		if (prependname)
		{
			sb.append(doseResponseExperiment.getName() + "\t");
		}
		sb.append(String.join("\t", row) + "\n");
		row.clear();
		row.add("Doses");

		for (Treatment treatment : doseResponseExperiment.getTreatments())
		{
			row.add(String.valueOf(treatment.getDose()));
		}
		sb.append(String.join("\t", row) + "\n");

		for (ProbeResponse result : doseResponseExperiment.getProbeResponses())
		{
			row.clear();
			row.add(result.getProbe().getId());
			for (Float response : result.getResponses())
			{
				row.add(String.valueOf(response));
			}
			if (prependname)
			{
				sb.append(doseResponseExperiment.getName() + "\t");
			}
			sb.append(String.join("\t", row) + "\n");
		}

		return sb.toString();
	}
}
