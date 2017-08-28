package com.sciome.bmdexpress2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ciit.data.ArrayMatrixData;
import org.ciit.data.BMDMatrixData;
import org.ciit.data.ModelParameters;
import org.ciit.data.WSMatrixData;
import org.ciit.io.ProjectReader;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.category.AdverseDirectionEnum;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.category.DefinedCategoryAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.GOAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.PathwayAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.ReferenceGeneProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.category.identifier.CategoryIdentifier;
import com.sciome.bmdexpress2.mvp.model.category.identifier.GOCategoryIdentifier;
import com.sciome.bmdexpress2.mvp.model.category.identifier.GenericCategoryIdentifier;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.probe.Probe;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.model.refgene.CustomGene;
import com.sciome.bmdexpress2.mvp.model.refgene.EntrezGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ChiSquareResult;
import com.sciome.bmdexpress2.mvp.model.stat.HillResult;
import com.sciome.bmdexpress2.mvp.model.stat.PolyResult;
import com.sciome.bmdexpress2.mvp.model.stat.PowerResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.util.annotation.FileAnnotation;
import com.sciome.bmdexpress2.util.categoryanalysis.ProbeGeneMaps;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.CategoryMapBase;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.GOTermMap;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.GenesPathways;
import com.sciome.bmdexpress2.util.prefilter.FoldChange;
import com.sciome.bmdexpress2.util.stat.FishersExact;

public class ConversionUtil
{

	private Map<String, DoseResponseExperiment>	dEMap				= new HashMap<>();
	// key will be experimentname__probeid
	private Map<String, ProbeResponse>			probeResponseMap	= new HashMap<>();

	// key will be experimentname__probeid__model
	private Map<String, StatResult>				statResultMap		= new HashMap<>();

	// key will be experimentname__bmdresultname__probeid
	private Map<String, ProbeStatResult>		probeStatResultMap	= new HashMap<>();

	// key will be experimentname__bmdresultname
	private Map<String, BMDResult>				bmdResultMap		= new HashMap<>();

	protected ConversionUtil()
	{
		// Exists only to defeat instantiation.
	}

	public BMDProject convertOldToNew(ProjectReader bmdProjectReader)
	{
		BMDProject bmdProject = new BMDProject();
		try
		{
			bmdProject.setName("Imported Project");
			// party time
			Vector<WSMatrixData> matrixData = bmdProjectReader.getVectMData();

			for (WSMatrixData mData : matrixData)
			{
				if (mData instanceof ArrayMatrixData)
				{
					// load dose response data
					DoseResponseExperiment de = loadDoseResponseData((ArrayMatrixData) mData);
					bmdProject.getDoseResponseExperiments().add(de);
				}
				else if (mData instanceof BMDMatrixData)
				{
					// load bmdresult
					BMDResult bmdResult = loadBMDResult((BMDMatrixData) mData);
					bmdProject.getbMDResult().add(bmdResult);
				}
				else if (mData instanceof WSMatrixData && mData.getType() == 3)
				{
					// load category result
					CategoryAnalysisResults cResults = loadCategoryAnalysisResults(mData);
					bmdProject.getCategoryAnalysisResults().add(cResults);
				}
				else if (mData instanceof WSMatrixData && mData.getType() == 1)
				{
					// load oneway anova results
					OneWayANOVAResults oneWayResult = loadOneWayResults(mData);
					bmdProject.getOneWayANOVAResults().add(oneWayResult);
				}

			}
		}
		catch (Exception e)

		{
			e.printStackTrace();
		}

		return bmdProject;
	}

	private BMDResult loadBMDResult(BMDMatrixData mData)
	{

		BMDResult results = new BMDResult();

		results.setName(mData.getName());
		String source = mData.getSource();

		// associate correct dose response experiment to it
		DoseResponseExperiment dE = dEMap.get(source);
		results.setDoseResponseExperiment(dE);
		List<ProbeStatResult> probeStatResults = new ArrayList<>();
		results.setProbeStatResults(probeStatResults);

		// load the data
		ModelParameters mP = mData.getMP();
		int index1 = 0;
		for (Object[] row : mData.getData())
		{
			try
			{
				ProbeStatResult result = new ProbeStatResult();
				ProbeResponse pR = probeResponseMap.get(source + "__" + mP.getIdentifiers()[index1]);

				probeStatResultMap.put(source + "__" + mData.getName() + "__" + pR.getProbe().getId(),
						result);
				result.setProbeResponse(pR);
				probeStatResults.add(result);

				List<StatResult> statResults = new ArrayList<>();
				result.setStatResults(statResults);

				int i = 0;
				for (double[][] modelParams : mP.getModelParams())
				{
					String identifier = mP.getIdentifiers()[index1];
					String model = mP.getModels().get(i);
					StatResult statResult = null;
					if (model.toLowerCase().contains("hill"))
					{
						statResult = new HillResult();
						statResultMap.put(dE.getName() + "__" + identifier + "__hill", statResult);
					}
					else if (model.toLowerCase().contains("power"))
					{
						statResult = new PowerResult();
						statResultMap.put(dE.getName() + "__" + identifier + "__power", statResult);
					}
					else if (model.toLowerCase().contains("linear"))
					{
						statResult = new PolyResult();
						((PolyResult) statResult).setDegree(1);
						statResultMap.put(dE.getName() + "__" + identifier + "__linear", statResult);
					}
					else if (model.toLowerCase().contains("polynomial 2"))
					{
						statResult = new PolyResult();
						((PolyResult) statResult).setDegree(2);
						statResultMap.put(dE.getName() + "__" + identifier + "__poly2", statResult);
					}
					else if (model.toLowerCase().contains("polynomial 3"))
					{
						statResult = new PolyResult();
						((PolyResult) statResult).setDegree(3);
						statResultMap.put(dE.getName() + "__" + identifier + "__poly3", statResult);
					}
					else if (model.toLowerCase().contains("polynomial 4"))
					{
						statResult = new PolyResult();
						((PolyResult) statResult).setDegree(4);
						statResultMap.put(dE.getName() + "__" + identifier + "__poly4", statResult);
					}

					if (((String) row[row.length - 2]).toLowerCase().contains("hill")
							&& statResult instanceof HillResult)
					{
						result.setBestStatResult(statResult);
					}
					else if (((String) row[row.length - 2]).toLowerCase().contains("power")
							&& statResult instanceof PowerResult)
					{
						result.setBestStatResult(statResult);
					}
					else if (((String) row[row.length - 2]).toLowerCase().contains("linear")
							&& statResult instanceof PolyResult && ((PolyResult) statResult).getDegree() == 1)
					{
						result.setBestStatResult(statResult);
					}
					else if (((String) row[row.length - 2]).toLowerCase().contains("polynomial 2")
							&& statResult instanceof PolyResult && ((PolyResult) statResult).getDegree() == 2)
					{
						result.setBestStatResult(statResult);
					}
					else if (((String) row[row.length - 2]).toLowerCase().contains("polynomial 3")
							&& statResult instanceof PolyResult && ((PolyResult) statResult).getDegree() == 3)
					{
						result.setBestStatResult(statResult);
					}
					else if (((String) row[row.length - 2]).toLowerCase().contains("polynomial 4")
							&& statResult instanceof PolyResult && ((PolyResult) statResult).getDegree() == 4)
					{
						result.setBestStatResult(statResult);
					}

					if (((String) mData.getColumnNames()[row.length - 3]).toLowerCase().contains("best poly")
							&& statResult instanceof PolyResult && ((Integer) row[row.length - 3])
									.intValue() == ((PolyResult) statResult).getDegree())
					{
						result.setBestPolyStatResult(statResult);
					}

					double[] theParams = modelParams[index1];
					statResult.setBMD(theParams[0]);
					statResult.setBMDL(theParams[1]);
					statResult.setFitPValue(theParams[2]);
					statResult.setFitLogLikelihood(theParams[3]);
					statResult.setAIC(theParams[4]);

					int direction = 1;

					if (theParams[6] < 0)
					{
						direction = -1;
					}

					statResult.setCurveParameters(Arrays.copyOfRange(theParams, 5, theParams.length));
					statResult.setAdverseDirection((short) direction);

					if (statResult instanceof HillResult)
					{
						((HillResult) statResult).setkFlag((short) ((Integer) row[6]).intValue());
					}
					statResults.add(statResult);
					i++;
				}

				Pattern p = Pattern.compile("-?\\d+");

				int k = -1;
				int pValueIndex = 0;
				List<ChiSquareResult> chiResults = new ArrayList<>();
				for (String columnName : mData.getColumnNames())
				{
					k++;
					if (!columnName.contains("ChiSquare"))
						continue;
					Matcher m = p.matcher(columnName);
					List<Integer> degreeList = new ArrayList<>();
					while (m.find())
					{
						degreeList.add(Integer.valueOf(m.group()));
					}

					if (degreeList.size() != 2)
						continue;

					if (columnName.contains("pValue"))
					{
						chiResults.get(pValueIndex).setpValue((Double) row[k]);
						pValueIndex++;
					}
					else
					{
						ChiSquareResult chi = new ChiSquareResult();

						chi.setDegree1(degreeList.get(0));
						chi.setDegree2(degreeList.get(1));
						chi.setValue((Double) row[k]);
						chiResults.add(chi);
					}

				}
				if (result.getBestPolyStatResult() != null)
					result.setChiSquaredResults(chiResults);

				index1++;

			}
			catch (Exception e)

			{
				e.printStackTrace();
			}
		}

		// load the data, use column header to figure out which model is being read

		// load up some chisquare data if poly

		// use the string to figure out best model

		// load analysis notes

		// load analysis notes
		AnalysisInfo anInf = new AnalysisInfo();
		List<String> notes = new ArrayList<>();
		anInf.setNotes(notes);
		results.setAnalysisInfo(anInf);
		for (String note : mData.getNote().split("\n"))
			notes.add(note);

		bmdResultMap.put(source + "__" + mData.getName(), results);

		return results;

	}

	private OneWayANOVAResults loadOneWayResults(WSMatrixData mData)
	{
		OneWayANOVAResults results = new OneWayANOVAResults();
		List<OneWayANOVAResult> oneWayResults = new ArrayList<>();
		results.setOneWayANOVAResults(oneWayResults);
		results.setName(mData.getName());
		String source = mData.getSource();

		// associate correct dose response experiment to it
		DoseResponseExperiment dE = dEMap.get(source);
		results.setDoseResponseExperiement(dE);

		// load the data
		for (Object[] row : mData.getData())
		{
			try
			{
				OneWayANOVAResult result = new OneWayANOVAResult();
				ProbeResponse pR = probeResponseMap.get(source + "__" + row[0]);
				result.setProbeResponse(pR);
				if (row[5] != null)
					result.setAdjustedPValue((Double) row[5]);
				result.setDegreesOfFreedomOne((short) ((Integer) row[1]).intValue());
				result.setDegreesOfFreedomTwo((short) ((Integer) row[2]).intValue());
				result.setfValue((Double) row[3]);
				result.setpValue((Double) row[4]);
				oneWayResults.add(result);
			}
			catch (Exception e)

			{
				e.printStackTrace();
			}

		}

		int resultSize = oneWayResults.size();

		FoldChange foldChange = new FoldChange(dE.getTreatments(), true, 2);
		for (int i = 0; i < resultSize; i++)
		{
			Float bestFoldChange = foldChange
					.getBestFoldChangeValue(oneWayResults.get(i).getProbeResponse().getResponses());
			oneWayResults.get(i).setBestFoldChange(bestFoldChange);

		}

		// load analysis notes
		AnalysisInfo anInf = new AnalysisInfo();
		List<String> notes = new ArrayList<>();
		anInf.setNotes(notes);
		results.setAnalysisInfo(anInf);
		for (String note : mData.getNote().split("\n"))
			notes.add(note);

		return results;
	}

	private CategoryAnalysisResults loadCategoryAnalysisResults(WSMatrixData mData)
	{
		// figure out if this is go, kegg, or custom
		CategoryAnalysisResults results = new CategoryAnalysisResults();
		results.setName(mData.getName());
		String source = mData.getSource();
		String workSource = mData.getWorkSource();
		int catType = 3;
		if (mData.getColumnNames()[0].contains("GO"))
			catType = 1;
		else if (mData.getColumnNames()[0].contains("KEGG"))
			catType = 2;

		int curCol = 0;
		int genesUpColumn = 0;
		int genesDownColumn = 0;
		int genesConflictColumn = 0;
		for (int c = 0; c < mData.getColumnNames().length; c++)
		{
			if (mData.getColumnNames()[c].toLowerCase().contains("genes up list")
					|| mData.getColumnNames()[c].toLowerCase().contains("items up list"))
			{
				genesUpColumn = c;
			}
			if (mData.getColumnNames()[c].toLowerCase().contains("genes down list")
					|| mData.getColumnNames()[c].toLowerCase().contains("items down list"))
			{
				genesDownColumn = c;
			}
			if (mData.getColumnNames()[c].toLowerCase().contains("conflicting probe"))
			{
				genesConflictColumn = c;
			}
		}

		// let's glean all the gene ids and probe ids.
		// first get the first column
		int genecolumn = 0;
		for (String column : mData.getColumnNames())
		{
			if (column.toLowerCase().contains("gene ids") || column.toLowerCase().contains("item ids"))
				break;
			genecolumn++;
		}
		Map<String, Set<String>> geneProbeMap = new HashMap<>();

		for (Object[] row : mData.getData())
		{
			if (row[genecolumn] == null || row[genecolumn + 1] == null)
				continue;
			String genes = row[genecolumn].toString();
			String probes = row[genecolumn + 1].toString();

			String[] geneArray = genes.split(";");
			String[] probeArray = probes.split(";");
			for (int i = 0; i < geneArray.length; i++)
			{
				if (!geneProbeMap.containsKey(geneArray[i]))
					geneProbeMap.put(geneArray[i], new HashSet<>());

				if (!geneProbeMap.get(geneArray[i]).contains(probeArray[i]))
					geneProbeMap.get(geneArray[i]).add(probeArray[i]);
			}
		}

		// make a reference gene map
		Map<String, ReferenceGene> referenceGeneMap = new HashMap<>();
		if (catType == 3)
		{
			for (String key : geneProbeMap.keySet())
			{
				ReferenceGene refGene = new CustomGene();
				refGene.setId(key);
				referenceGeneMap.put(key, refGene);
			}
		}
		else
		{
			for (ReferenceGeneAnnotation referenceAnnotation : dEMap.get(source)
					.getReferenceGeneAnnotations())
			{
				for (ReferenceGene referenceGene : referenceAnnotation.getReferenceGenes())
				{
					referenceGeneMap.put(referenceGene.getId(), referenceGene);
				}
			}
		}

		List<CategoryAnalysisResult> catResultList = new ArrayList<>();
		for (Object[] row : mData.getData())
		{
			try
			{
				@SuppressWarnings("unused")
				int numSigGenes = 0;
				CategoryAnalysisResult catResult = null;
				CategoryIdentifier theID = null;
				if (catType == 1)
				{
					catResult = new GOAnalysisResult();
					theID = new GOCategoryIdentifier();
					theID.setId(row[0].toString());
					theID.setTitle(row[2].toString());
					((GOCategoryIdentifier) theID).setGoLevel(row[1].toString());
					curCol = 3;
				}
				else if (catType == 2)
				{
					catResult = new PathwayAnalysisResult();
					theID = new GenericCategoryIdentifier();
					theID.setId((String) row[0]);
					theID.setTitle((String) row[1]);
					curCol = 2;
				}
				else
				{
					catResult = new DefinedCategoryAnalysisResult();
					theID = new GenericCategoryIdentifier();
					theID.setId((String) row[0]);
					theID.setTitle((String) row[1]);
					curCol = 2;
				}
				catResult.setCategoryIdentifier(theID);

				catResult.setGeneAllCount((Integer) row[curCol++]);
				catResult.setGeneCountSignificantANOVA((Integer) row[curCol++]);
				numSigGenes = catResult.getGeneCountSignificantANOVA();
				if (mData.getColumnNames()[curCol].toLowerCase().contains("with bmd <= highest"))
				{
					catResult.setGenesWithBMDLessEqualHighDose((Integer) row[curCol++]);
					numSigGenes = catResult.getGenesWithBMDLessEqualHighDose();
				}
				if (mData.getColumnNames()[curCol].toLowerCase().contains("with bmd p-value"))
				{
					catResult.setGenesWithBMDpValueGreaterEqualValue((Integer) row[curCol++]);
					numSigGenes = catResult.getGenesWithBMDpValueGreaterEqualValue();
				}
				catResult.setPercentage(((Number) row[curCol++]).doubleValue());

				String geneIDs = "";
				if (row[curCol] != null)
					geneIDs = row[curCol].toString();
				List<ReferenceGeneProbeStatResult> rgPs = new ArrayList<>();
				List<String> genesDown = new ArrayList<>();
				List<String> genesUp = new ArrayList<>();
				List<String> genesConflict = new ArrayList<>();
				List<Double> genesConflictVal = new ArrayList<>();
				if (row[genesUpColumn] != null && !row[genesUpColumn].toString().equals(""))
				{
					for (String geneID : row[genesUpColumn].toString().split(";"))
					{
						genesUp.add(geneID);
					}
				}

				if (row[genesDownColumn] != null && !row[genesDownColumn].toString().equals(""))
				{
					for (String geneID : row[genesDownColumn].toString().split(";"))
					{
						genesDown.add(geneID);
					}
				}
				if (genesConflictColumn > 0 && row[genesConflictColumn] != null
						&& !row[genesConflictColumn].toString().equals(""))
				{
					for (String field : row[genesConflictColumn].toString().split(";"))
					{
						String geneID = field.replaceAll("\\(.*\\)", "").trim();
						genesConflict.add(geneID);
						String strVal = field.replaceAll("^.*\\(", "").replaceAll("\\).*$", "").trim();
						try
						{
							// I've noticed alot of values with multiple decimal points.
							// just put null in place of.
							genesConflictVal
									.add(Double.valueOf(strVal.replace("0.0.", "0.").replace("0.00.", "0.")));
						}
						catch (Exception e)
						{

							e.printStackTrace();
							genesConflictVal.add(null);
						}
					}
				}

				if (geneIDs != null && !geneIDs.equals(""))
				{
					for (String geneID : geneIDs.split(";"))
					{
						ReferenceGeneProbeStatResult rgP = new ReferenceGeneProbeStatResult();
						rgPs.add(rgP);
						List<ProbeStatResult> probeStatResults = new ArrayList<>();
						rgP.setReferenceGene(referenceGeneMap.get(geneID));

						for (String probeID : geneProbeMap.get(geneID))
						{
							for (String probe : probeID.split(","))
							{
								if (this.probeStatResultMap
										.get(source + "__" + workSource + "__" + probe) != null)
									probeStatResults.add(this.probeStatResultMap
											.get(source + "__" + workSource + "__" + probe));
							}
						}
						rgP.setProbeStatResults(probeStatResults);
						rgP.setAdverseDirection(AdverseDirectionEnum.CONFLICT);
						if (genesUp.contains(geneID))
						{
							rgP.setAdverseDirection(AdverseDirectionEnum.UP);
						}
						else if (genesDown.contains(geneID))
						{
							rgP.setAdverseDirection(AdverseDirectionEnum.DOWN);
						}

						int conflictIndex = genesConflict.indexOf(geneID);
						if (conflictIndex != -1)
						{
							rgP.setConflictMinCorrelation(genesConflictVal.get(conflictIndex));
						}

					}
				}
				catResult.setReferenceGeneProbeStatResults(rgPs);

				// skip geneIDs
				curCol++;
				// skip probeIDs
				curCol++;
				if (mData.getColumnNames()[curCol].toLowerCase().contains("genes with conf")
						|| mData.getColumnNames()[curCol].toLowerCase().contains("items with conf"))
				{
					if (row[curCol] == null)
						catResult.setGenesWithConflictingProbeSets("");
					else
						catResult.setGenesWithConflictingProbeSets((String) row[curCol]);
					curCol++;
				}
				catResult.setBmdMean((Double) row[curCol++]);
				catResult.setBmdMedian((Double) row[curCol++]);
				catResult.setBmdMinimum((Double) row[curCol++]);
				catResult.setBmdSD((Double) row[curCol++]);
				catResult.setBmdWMean((Double) row[curCol++]);
				catResult.setBmdWSD((Double) row[curCol++]);

				catResult.setBmdlMean((Double) row[curCol++]);
				catResult.setBmdlMedian((Double) row[curCol++]);
				catResult.setBmdlMinimum((Double) row[curCol++]);
				catResult.setBmdlSD((Double) row[curCol++]);
				catResult.setBmdlWMean((Double) row[curCol++]);
				catResult.setBmdlWSD((Double) row[curCol++]);

				try
				{
					catResult.setFifthPercentileIndex(((Number) row[curCol++]).doubleValue());
				}
				catch (Exception e)
				{}
				try
				{
					catResult.setBmdFifthPercentileTotalGenes(((Number) row[curCol++]).doubleValue());
				}
				catch (Exception e)
				{}
				try
				{
					catResult.setTenthPercentileIndex(((Number) row[curCol++]).doubleValue());
				}
				catch (Exception e)
				{}
				try
				{
					catResult.setBmdTenthPercentileTotalGenes(((Number) row[curCol++]).doubleValue());
				}
				catch (Exception e)
				{}

				// skip bmdlist
				curCol++;
				// skip bmdllist
				curCol++;

				// skip probes advers up
				curCol++;
				// skip probes adverse down
				curCol++;

				// skip genes advers up count
				curCol++;
				// skip genes up list
				curCol++;
				// skip genes up probes list
				curCol++;
				catResult.setGenesUpBMDMean((Double) row[curCol++]);
				catResult.setGenesUpBMDMedian((Double) row[curCol++]);
				catResult.setGenesUpBMDSD((Double) row[curCol++]);
				catResult.setGenesUpBMDLMean((Double) row[curCol++]);
				catResult.setGenesUpBMDLMedian((Double) row[curCol++]);
				catResult.setGenesUpBMDLSD((Double) row[curCol++]);

				// skip bmdlist up
				curCol++;
				// skip bmdllist up
				curCol++;

				// skip genes advers down count
				curCol++;
				// skip genes down list
				curCol++;
				// skip genes down probes list
				curCol++;
				catResult.setGenesDownBMDMean((Double) row[curCol++]);
				catResult.setGenesDownBMDMedian((Double) row[curCol++]);
				catResult.setGenesDownBMDSD((Double) row[curCol++]);
				catResult.setGenesDownBMDLMean((Double) row[curCol++]);
				catResult.setGenesDownBMDLMedian((Double) row[curCol++]);
				catResult.setGenesDownBMDLSD((Double) row[curCol++]);

				// skip bmdlist down
				curCol++;
				// skip bmdllist down
				curCol++;

				if (mData.getColumnNames()[curCol].toLowerCase().contains("genes with adverse"))
				{
					// skip all the conflicting stuff.
					curCol++;
					curCol++;
					curCol++;
					curCol++;
					curCol++;
				}

				catResultList.add(catResult);
			}
			catch (Exception e)
			{
				e.printStackTrace();

			}
		}

		catResultList.sort(new Comparator<CategoryAnalysisResult>() {

			@Override
			public int compare(CategoryAnalysisResult o1, CategoryAnalysisResult o2)
			{
				return o1.getCategoryIdentifier().getId().toLowerCase()
						.compareTo(o2.getCategoryIdentifier().getId().toLowerCase());
			}
		});

		// probeGeneMaps.readArraysInfo();

		CategoryMapBase catMap = null;

		if (catType == 2)
		{
			BMDResult bmdResult = bmdResultMap.get(source + "__" + workSource);
			ProbeGeneMaps probeGeneMaps = new ProbeGeneMaps(bmdResult);
			probeGeneMaps.readProbes(false);
			Hashtable<String, Integer> probeHash = new Hashtable<>();
			for (ProbeResponse probeResponse : bmdResult.getDoseResponseExperiment().getProbeResponses())
				probeHash.put(probeResponse.getProbe().getId(), 1);
			probeGeneMaps.setProbesHash(probeHash);
			probeGeneMaps.probeGeneMaping(bmdResult.getDoseResponseExperiment().getChip().getName(), true);
			catMap = new GenesPathways(probeGeneMaps, "KEGG");
		}
		else if (catType == 1)
		{
			BMDResult bmdResult = bmdResultMap.get(source + "__" + workSource);
			ProbeGeneMaps probeGeneMaps = new ProbeGeneMaps(bmdResult);
			probeGeneMaps.readProbes(false);
			Hashtable<String, Integer> probeHash = new Hashtable<>();
			for (ProbeResponse probeResponse : bmdResult.getDoseResponseExperiment().getProbeResponses())
				probeHash.put(probeResponse.getProbe().getId(), 1);
			probeGeneMaps.setProbesHash(probeHash);
			probeGeneMaps.probeGeneMaping(bmdResult.getDoseResponseExperiment().getChip().getName(), true);

			int goID = 0;
			if (mData.getNote().contains("biological_process"))
			{
				goID = 1;
			}
			else if (mData.getNote().contains("molecular_function"))
			{
				goID = 2;
			}
			else if (mData.getNote().contains("cellular_component"))
			{
				goID = 3;
			}
			catMap = new GOTermMap(probeGeneMaps, bmdResult.getDoseResponseExperiment().getChip(), goID);
		}

		// get total number of genes

		int allTotal = 0;

		// get total number of genes that are in this set that shows some relation here.
		int chgTotal = 0;
		if (catMap != null)
		{
			catMap.getAllGeneCount();
			catMap.getSubGeneCount();
		}
		for (CategoryAnalysisResult catResult : catResultList)
		{
			int numSigGenes = catResult.getGeneCountSignificantANOVA();
			if (catResult.getGenesWithBMDLessEqualHighDose() != null)
			{
				numSigGenes = catResult.getGenesWithBMDLessEqualHighDose();
			}
			if (catResult.getGenesWithBMDpValueGreaterEqualValue() != null)
			{
				numSigGenes = catResult.getGenesWithBMDpValueGreaterEqualValue();
			}
			if (catType == 1 && catResultList.size() > 0) // go analysis has the totals at the top of the
															// file.
			{

				double[] triple = fisherExactTest(numSigGenes, catResult.getGeneAllCount(), chgTotal,
						allTotal);
				catResult.setFishersExactLeftPValue(triple[0]);
				catResult.setFishersExactRightPValue(triple[1]);
				catResult.setFishersExactTwoTailPValue(triple[2]);
			}
			else if (catType == 2)
			{
				// need to read the pathway file to figure out total number of genes.

				// need to figure out which genes in referenceGeneMap are part of the
				// kegg pathway file.

				double[] triple = fisherExactTest(numSigGenes, catResult.getGeneAllCount(), chgTotal,
						allTotal);
				catResult.setFishersExactLeftPValue(triple[0]);
				catResult.setFishersExactRightPValue(triple[1]);
				catResult.setFishersExactTwoTailPValue(triple[2]);
			}
		}
		results.setCategoryAnalsyisResults(catResultList);
		// generate category identifier

		// figure out how to map the stat results to the data in relation to the reference genes

		// load analysis notes
		// load analysis notes
		AnalysisInfo anInf = new AnalysisInfo();
		List<String> notes = new ArrayList<>();
		anInf.setNotes(notes);
		results.setAnalysisInfo(anInf);
		for (String note : mData.getNote().split("\n"))
			notes.add(note);

		if (this.bmdResultMap.get(source + "__" + workSource) == null)
		{
			notes.add("Could not link to BMDResults because data is missing from BMD file");
			notes.add("Possibly missing source data: " + source);
			notes.add("Possibly missing work source data: " + workSource);
		}

		return results;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private DoseResponseExperiment loadDoseResponseData(ArrayMatrixData mData)
	{
		DoseResponseExperiment dE = new DoseResponseExperiment();
		dEMap.put(mData.getName(), dE);
		dE.setName(mData.getName());

		List<Treatment> treatments = new ArrayList<>();
		List<ReferenceGeneAnnotation> refGeneAnn = new ArrayList<>();
		List<ProbeResponse> probeResponses = new ArrayList<>();
		ChipInfo chipInfo = new ChipInfo();
		// try to avoid storing duplicate genes.
		Map<String, ReferenceGene> refCache = new HashMap<>();

		dE.setTreatments(treatments);
		dE.setReferenceGeneAnnotations(refGeneAnn);
		dE.setProbeResponses(probeResponses);

		AnalysisInfo anInf = new AnalysisInfo();
		List<String> notes = new ArrayList<>();
		anInf.setNotes(notes);
		dE.setAnalysisInfo(anInf);
		if (mData.getNote() != null)
		{

			for (String note : mData.getNote().split("\n"))
				notes.add(note);
		}

		// no data? get out of here.
		if (mData.getData().length < 2)
			return dE;
		String[] columnHeaders = mData.getColumnNames();
		for (int i = 1; i < columnHeaders.length; i++)

		{
			if (mData.getData()[0][i] instanceof Integer)
			{

			}
			Treatment treatment = new Treatment(columnHeaders[i],
					Float.valueOf((float) ((Number) mData.getData()[0][i]).doubleValue()));
			treatments.add(treatment);
		}

		int i = -1;
		for (Object[] row : mData.getData())
		{
			try
			{
				i++;
				if (i == 0)
					continue;
				int j = 0;
				ProbeResponse pR = new ProbeResponse();
				List<Float> responses = new ArrayList<>();

				probeResponses.add(pR);
				for (Object column : row)
				{
					if (j == 0)
					{
						Probe probe = new Probe();
						probe.setId((String) column);
						pR.setProbe(probe);
					}
					else
					{
						responses.add(Float.valueOf((float) ((Number) column).doubleValue()));
					}
					j++;
				}
				pR.setResponses(responses);

				probeResponseMap.put(dE.getName() + "__" + pR.getProbe().getId(), pR);
			}
			catch (Exception e)

			{
				e.printStackTrace();
			}

		}

		// read the chip and add the reference genes to it
		if (mData.getChip() != null)
		{
			Hashtable<String, Integer> probeHash = new Hashtable<>();
			for (ProbeResponse probeResponse : dE.getProbeResponses())
			{
				probeHash.put(probeResponse.getProbe().getId(), 1);
			}
			FileAnnotation fA = new FileAnnotation();
			fA.setProbesHash(probeHash);
			fA.readArraysInfo();
			fA.setChip(mData.getChip());
			chipInfo = fA.getChip(mData.getChip());
			dE.setChip(chipInfo);
			fA.arrayProbesGenes();
			fA.arrayGenesSymbols();

			fA.getGene2ProbeHash();

			Hashtable<String, Vector> probesToGene = fA.getProbe2GeneHash();
			Hashtable<String, String> geneSymbolHash = fA.getGene2SymbolHash();

			if (probesToGene != null)
			{
				// let's create referenceGeneAnnotations
				for (ProbeResponse probeResponse : dE.getProbeResponses())
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

					refGeneAnn.add(referenceGeneAnnotation);
				}

				dE.setReferenceGeneAnnotations(refGeneAnn);
			}
		}

		// load analysis notes

		return dE;

	}

	/**
	 *
	 * @return left, right and two-tail p-Values
	 */
	private double[] fisherExactTest(int sub, int chgTotal, int all, int allTotal)
	{
		int a = sub;
		int b = chgTotal - a;
		int c = all - a;
		int d = allTotal - a - b - c;
		double[] pValues = { 1, 1, 1 };

		if (a >= 0 && b >= 0 && c >= 0 && d >= 0)
		{
			FishersExact test = new FishersExact(a, b, c, d);
			pValues[0] = NumberManager.numberFormat(5, test.pLeft());
			pValues[1] = NumberManager.numberFormat(5, test.pRight());
			pValues[2] = NumberManager.numberFormat(5, test.twoTail());
		}

		return pValues;// NumberManager.numberFormat(4, pValues);
	}

}
