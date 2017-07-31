package com.sciome.bmdexpress2.util.prefilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.PathwayFilterResult;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.PathWayFilterPValueEnum;
import com.sciome.bmdexpress2.util.FileIO;
import com.sciome.bmdexpress2.util.annotation.FileInfo;
import com.sciome.bmdexpress2.util.annotation.URLUtils;

/*
 * Path way filter analysis will associate probes and their responses to pathways and the
 * filter out the pathways that are not significant
 */
public class PathwayFilterAnalysis
{

	private DoseResponseExperiment	doseResponseExperiment;

	private Double					pCutOff;
	private Integer					iterations;
	private Double					alpha;
	private Integer					threads;
	private boolean					filterOutControlGenes;
	private IPrefilterProgress		filterCaller;
	private boolean					ignoreSingleTonDoseResponse;
	private Integer					minGenesPerPathway;
	private PathWayFilterPValueEnum	pathWayPValueEnum;

	public PathwayFilterAnalysis(Double pCutOff, Integer iterations, Double alpha, Integer threads,
			boolean filterOutControlGenes, boolean ignoreSingleTonDoseResponse, Integer minGenesPerPathway,
			PathWayFilterPValueEnum pathWayPValueEnum, IPrefilterProgress filterCaller)
	{
		this.pCutOff = pCutOff;
		this.iterations = iterations;
		this.alpha = alpha;
		this.threads = threads;
		this.filterOutControlGenes = filterOutControlGenes;
		this.filterCaller = filterCaller;
		this.ignoreSingleTonDoseResponse = ignoreSingleTonDoseResponse;
		this.minGenesPerPathway = minGenesPerPathway;
		this.pathWayPValueEnum = pathWayPValueEnum;
	}

	/*
	 * analyze the heck out of it.
	 */
	public List<PathwayFilterResult> analyzeDoseResponseData(IStatModelProcessable processableData,
			AnalysisInfo analysisInfo)
	{

		DoseResponseExperiment doseResponseExperiment = processableData
				.getProcessableDoseResponseExperiment();
		this.doseResponseExperiment = doseResponseExperiment;
		List<PathwayFilterResult> pathWayFilterResults = new ArrayList<>();

		Map<String, Integer> probeIndexMap = new HashMap<>();
		int i = 0;
		for (ProbeResponse probeResponse : processableData.getProcessableProbeResponses())
		{
			probeIndexMap.put(probeResponse.getProbe().getId(), i);
			i++;
		}
		// create gene to probe hashes
		Map<String, List<Integer>> geneProbeIndexMap = new HashMap<>();
		if (doseResponseExperiment.getReferenceGeneAnnotations() != null)
		{
			for (ReferenceGeneAnnotation refGeneAnnotation : doseResponseExperiment
					.getReferenceGeneAnnotations())
			{
				for (ReferenceGene refGene : refGeneAnnotation.getReferenceGenes())
				{
					if (!probeIndexMap.containsKey(refGeneAnnotation.getProbe().getId()))
					{
						continue;
					}
					if (!geneProbeIndexMap.containsKey(refGene.getId()))
					{
						List<Integer> probeIndexes = new ArrayList<>();
						geneProbeIndexMap.put(refGene.getId(), probeIndexes);
					}
					geneProbeIndexMap.get(refGene.getId())
							.add(probeIndexMap.get(refGeneAnnotation.getProbe().getId()));

				}

			}
		}

		Map<String, Set<Integer>> pathwayToProbeMap = fileGenes2Maps("KEGG", geneProbeIndexMap);

		/*
		 * first create the input files
		 */

		String theTime = String.valueOf(System.currentTimeMillis());

		String probeFile = "" + BMDExpressConstants.getInstance().TEMP_FOLDER + File.separator + theTime
				+ "probereponses.txt";
		String pathwayFile = "" + BMDExpressConstants.getInstance().TEMP_FOLDER + File.separator + theTime
				+ "pathwaytoprobe.txt";
		String outFile = "" + BMDExpressConstants.getInstance().TEMP_FOLDER + File.separator + theTime
				+ "pathwayfilteroutput.txt";
		String outputRedirectFile = "" + BMDExpressConstants.getInstance().TEMP_FOLDER + File.separator
				+ theTime + "pathwayoutput.txt";

		// create the pathway to probe data file

		try
		{
			FileWriter writer = new FileWriter(pathwayFile);
			Iterator it = pathwayToProbeMap.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<String, Set<Integer>> pair = (Map.Entry<String, Set<Integer>>) it.next();
				StringBuffer sb = new StringBuffer();
				sb.append("\"" + pair.getKey() + "\"\t\"");

				int index = 0;
				for (Integer probeIndex : pair.getValue())
				{
					if (index > 0)
						sb.append(",");
					sb.append(probeIndex + 1);
					index++;
				}
				sb.append("\"\n");

				writer.write(sb.toString());

			}

			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// create dose experiment data file

		try
		{
			FileWriter writer = new FileWriter(probeFile);
			List<String> row = new ArrayList<>();
			row.add("\"ID\"");
			Set<Integer> excludeIndexes = new HashSet<>();

			int currCount = 0;
			Float preveDose = -9999.0f;
			int index = 0;
			// set the excludeIndexes based on singleton dosepresonses
			if (ignoreSingleTonDoseResponse)
			{
				for (Treatment treatment : doseResponseExperiment.getTreatments())
				{
					if (!preveDose.equals(treatment.getDose()) && index > 0)
					{
						if (currCount == 1)
						{
							excludeIndexes.add(index - 1);
							analysisInfo.getNotes()
									.add("Excluded Singleton Dose Response for Dose: " + preveDose);
						}
						currCount = 0;
					}
					currCount++;
					preveDose = treatment.getDose();
					index++;
				}
			}
			index = 0;
			for (Treatment treatment : doseResponseExperiment.getTreatments())
			{
				if (!excludeIndexes.contains(index))
					row.add("\"" + treatment.getName() + "\"");
				index++;
			}
			writer.write(String.join("\t", row) + "\n");
			row.clear();
			row.add("\"Doses\"");
			index = 0;
			for (Treatment treatment : doseResponseExperiment.getTreatments())
			{
				if (!excludeIndexes.contains(index))
					row.add(String.valueOf(treatment.getDose()));
				index++;
			}
			writer.write(String.join("\t", row) + "\n");

			for (ProbeResponse result : processableData.getProcessableProbeResponses())
			{
				row.clear();
				row.add("\"" + result.getProbe().getId() + "\"");
				index = 0;
				for (Float response : result.getResponses())
				{
					if (!excludeIndexes.contains(index))
						row.add(String.valueOf(response));
					index++;
				}
				writer.write(String.join("\t", row) + "\n");
			}
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		/*
		 * run the Rscript
		 */

		PrintStream prtStrm = System.out;
		try
		{

			ProcessBuilder builder = new ProcessBuilder();

			builder.command(BMDExpressProperties.getInstance().getRscript(),
					"" + BMDExpressConstants.getInstance().BMDBASEPATH_LIB + File.separator
							+ BMDExpressProperties.getInstance().getPathwayFilterScript(),
					"" + probeFile + "", "" + pathwayFile + "", iterations.toString(), threads.toString(),
					alpha.toString(), outFile);
			builder.redirectErrorStream(true);
			builder.redirectOutput(new File(outputRedirectFile));
			Process process = builder.start();

			/*
			 * wait for the Rscript
			 */
			while (process.isAlive())
			{

				Thread.sleep(1000);

				Stream<String> line = Files.lines(new File(outputRedirectFile).toPath(),
						StandardCharsets.UTF_8);
				int linesCount = line.toArray().length;
				filterCaller.updateProgress("Iteration " + linesCount + "/" + iterations,
						(double) linesCount / (double) iterations);
				line.close();

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		/*
		 * read the results
		 */
		List<String> outputLines = new ArrayList<>();
		try
		{
			for (Object lineObject : Files.lines(new File(outFile).toPath(), StandardCharsets.UTF_8)
					.toArray())
			{
				outputLines.add(lineObject.toString());
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, PathwayFilterResult> probeToPathwayFilterResult = new HashMap<>();
		for (String outputLine : outputLines)
		{
			if (outputLine.contains("Pathway\t"))
				continue;
			String[] record = outputLine.split("\t");

			if (record.length < 8)
			{
				System.out.println("problem with record " + outputLine);
				continue;
			}
			String pathway = record[1];
			String pathwaySize = record[2];
			String es = record[3];
			String nes = record[4];
			String pValue = record[5];
			String adjPValue = record[6];
			String fwer = record[7];

			if (pathWayPValueEnum == PathWayFilterPValueEnum.UNADJUSTED && Double.valueOf(pValue) > pCutOff)
			{
				continue;
			}
			else if (pathWayPValueEnum == PathWayFilterPValueEnum.FDR && Double.valueOf(adjPValue) > pCutOff)
			{
				continue;
			}
			else if (pathWayPValueEnum == PathWayFilterPValueEnum.FWER && Double.valueOf(fwer) > pCutOff)
			{
				continue;
			}

			for (Integer probeIndex : pathwayToProbeMap.get(pathway))
			{
				ProbeResponse probeResponse = processableData.getProcessableProbeResponses().get(probeIndex);
				if (probeToPathwayFilterResult.containsKey(probeResponse.getProbe().getId()))
				{
					probeToPathwayFilterResult.get(probeResponse.getProbe().getId()).getPathways()
							.add(pathway);
					probeToPathwayFilterResult.get(probeResponse.getProbe().getId()).getpValues()
							.add(Double.valueOf(pValue));
					probeToPathwayFilterResult.get(probeResponse.getProbe().getId()).getFDRs()
							.add(Double.valueOf(adjPValue));
					probeToPathwayFilterResult.get(probeResponse.getProbe().getId()).getFWERs()
							.add(Double.valueOf(fwer));
				}
				else
				{
					PathwayFilterResult pathwayFilterResult = new PathwayFilterResult();
					pathwayFilterResult.setProbeResponse(probeResponse);
					pathwayFilterResult.setPathways(new ArrayList<>());
					pathwayFilterResult.getPathways().add(pathway);
					pathwayFilterResult.setpValues(new ArrayList<>());
					pathwayFilterResult.getpValues().add(Double.valueOf(pValue));

					pathwayFilterResult.setFDRs(new ArrayList<>());
					pathwayFilterResult.getFDRs().add(Double.valueOf(adjPValue));

					pathwayFilterResult.setFWERs(new ArrayList<>());
					pathwayFilterResult.getFWERs().add(Double.valueOf(fwer));

					probeToPathwayFilterResult.put(probeResponse.getProbe().getId(), pathwayFilterResult);
				}

			}
		}

		/*
		 * clean up the temp files.
		 */

		try
		{
			new File(probeFile).delete();
			new File(pathwayFile).delete();
			new File(outFile).delete();
			new File(outputRedirectFile).delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		/*
		 * put the results into the object.
		 */

		List<PathwayFilterResult> resultList = new ArrayList<>();

		for (PathwayFilterResult filter : probeToPathwayFilterResult.values())
		{
			resultList.add(filter);
		}

		return resultList;

	}

	private Map<String, Set<Integer>> fileGenes2Maps(String pathwayDb,
			Map<String, List<Integer>> geneProbeIndexMap)
	{
		String organismCode = readTaxonomyInfo();

		Map<String, Set<Integer>> pathwayProbeMap = new HashMap<>();
		String fName = organismCode + BMDExpressConstants.getInstance().KEGGFILES[2];
		String relativePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + File.separator + pathwayDb
				+ File.separator + BMDExpressConstants.getInstance().PATHWAYDIRS[1] + File.separator
				+ organismCode + File.separator;
		String httpPath = BMDExpressProperties.getInstance().getUpdateURL()
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + "/" + pathwayDb + "/"
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[1] + "/" + organismCode + "/" + fName;

		// make sure the file exists...if not then must needs be downloaded
		FileInfo fileInfo = URLUtils.checkDownload(httpPath, relativePath, fName);
		File inFile = fileInfo.getFile();

		if (inFile != null)
		{
			try
			{
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));
				String line;

				while ((line = reader.readLine()) != null)
				{
					String[] geneMaps = line.split(BMDExpressConstants.getInstance().TAB);

					if (geneMaps == null || geneMaps.length <= 1)
						continue;

					String[] maps = geneMaps[1].split(" ");

					if (maps == null)
						continue;
					if (!geneProbeIndexMap.containsKey(geneMaps[0]))
						continue;

					for (int j = 0; j < maps.length; j++)
					{
						if (!pathwayProbeMap.containsKey(pathwayDb + maps[j]))
						{
							pathwayProbeMap.put(pathwayDb + maps[j], new HashSet<>());
						}
						for (Integer probeIndex : geneProbeIndexMap.get(geneMaps[0]))
						{
							pathwayProbeMap.get(pathwayDb + maps[j]).add(probeIndex);
						}

					}
				}
				String[] keys = pathwayProbeMap.keySet().toArray(new String[pathwayProbeMap.keySet().size()]);
				for (String key : keys)
				{
					Set<Integer> probesSet = pathwayProbeMap.get(key);
					if (probesSet.size() <= minGenesPerPathway.intValue())
					{
						pathwayProbeMap.remove(key);
					}
				}
				reader.close();
			}
			catch (IOException e)
			{
				// System.out.println("Read preferences problem: " + e);
				e.printStackTrace();
			}
		}
		return pathwayProbeMap;

	}

	private String readTaxonomyInfo()
	{
		String fName = BMDExpressConstants.getInstance().KEGGFILES[0];
		String relativePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + File.separator;

		String httpPath = BMDExpressProperties.getInstance().getUpdateURL() + "/"
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + "/" + fName;
		FileInfo fileInfo = URLUtils.checkDownload(httpPath, relativePath, fName);
		File inFile = fileInfo.getFile();
		if (inFile != null)
		{
			Vector<String> input = FileIO.readVectorString(inFile);

			for (int i = 0; i < input.size(); i++)
			{
				String[] array = input.get(i).split(BMDExpressConstants.getInstance().TAB);

				if (doseResponseExperiment.getChip() != null)
				{
					if (array[0].startsWith(doseResponseExperiment.getChip().getSpecies())
							|| doseResponseExperiment.getChip().getSpecies().startsWith(array[0]))
					{
						return array[1];
					}
				}
			}
		}
		return "";
	}

}
