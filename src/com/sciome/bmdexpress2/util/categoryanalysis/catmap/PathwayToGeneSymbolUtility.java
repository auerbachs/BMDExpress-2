package com.sciome.bmdexpress2.util.categoryanalysis.catmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.annotation.FileInfo;
import com.sciome.bmdexpress2.util.annotation.URLUtils;

/*
 * create a mapping from to pathway to geneset
 * for easy geneset grabbing
 */
public class PathwayToGeneSymbolUtility
{

	private static PathwayToGeneSymbolUtility		instance				= null;
	private Map<String, Map<String, Set<String>>>	dbToPathwayToGeneSet	= new HashMap<>();
	private String									geoID					= "";

	public static PathwayToGeneSymbolUtility getInstance()
	{
		if (instance == null)
		{
			instance = new PathwayToGeneSymbolUtility();
		}
		return instance;
	}

	public PathwayToGeneSymbolUtility()
	{

	}

	public Map<String, Map<String, Set<String>>> getdbToPathwaytoGeneSet(
			DoseResponseExperiment doseResponseExperiment)
	{
		// no need to compute if it is current
		if (doseResponseExperiment.getChip() == null || doseResponseExperiment.getChip().getGeoID() == null)
			return dbToPathwayToGeneSet;
		if (dbToPathwayToGeneSet != null && doseResponseExperiment.getChip().getGeoID().equals(geoID))
			return dbToPathwayToGeneSet;

		this.geoID = doseResponseExperiment.getChip().getGeoID();
		dbToPathwayToGeneSet.clear();
		Map<String, String> gene2Symbol = new HashMap<>();
		for (ReferenceGeneAnnotation refAnn : doseResponseExperiment.getReferenceGeneAnnotations())
			for (ReferenceGene rg : refAnn.getReferenceGenes())
				gene2Symbol.put(rg.getId(), rg.getGeneSymbol());

		fillGOTerms(doseResponseExperiment.getChip(), gene2Symbol);

		fillREACTOMETerms(doseResponseExperiment.getChip(), gene2Symbol);

		return dbToPathwayToGeneSet;
	}

	private void fillGOTerms(ChipInfo chipInfo, Map<String, String> gene2Symbol)
	{
		Map<String, String> goTermToDescriptionMap = new HashMap<>();
		String fName = "gotermlevel.gz";
		String http = BMDExpressProperties.getInstance().getUpdateURL() + "/go/" + fName;
		// System.out.println("URL: " + http);
		goTermToDescriptionMap = new Hashtable<String, String>();

		dbToPathwayToGeneSet.put("GO", new HashMap<>());
		Map<String, Set<String>> goToGeneMap = dbToPathwayToGeneSet.get("GO");

		try
		{
			String filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
					+ GOTermMap.folders[0] + File.separator;
			File inFile = checkDownload(http, filePath, fName);

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));
			for (String line : IOUtils.readLines(reader))
			{
				String[] theLine = line.split("\\t");
				if (theLine.length < 3)
					continue;
				if (theLine[0].equals("all"))
					continue;
				goTermToDescriptionMap.put(theLine[0], theLine[2]);

			}
			reader.close();

			fName = "genes2gos.gz";
			http = BMDExpressProperties.getInstance().getUpdateURL() + "/arrays/" + chipInfo.getProvider()
					+ "/" + chipInfo.getGeoID() + "/" + fName;
			// System.out.println("URL: " + http);
			filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
					+ GOTermMap.folders[1] + File.separator + chipInfo.getProvider() + File.separator
					+ chipInfo.getGeoID() + File.separator;
			inFile = checkDownload(http, filePath, fName);

			reader = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));

			for (String line : IOUtils.readLines(reader))
			{
				String[] theLine = line.split(";");
				String geneId = theLine[0];
				if (theLine.length == 1)
					continue;
				String[] goTerms = theLine[1].split("\\t");
				for (String gotTerm : goTerms)
				{
					String goCat = goTermToDescriptionMap.get(gotTerm.split(",")[0].trim());
					if (goCat == null || goCat.equals("all"))
						continue;
					if (!goToGeneMap.containsKey(goCat))
						goToGeneMap.put(goCat, new HashSet<>());
					if (gene2Symbol.containsKey(geneId))
						goToGeneMap.get(goCat).add(gene2Symbol.get(geneId));
				}

			}

			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void fillREACTOMETerms(ChipInfo chipInfo, Map<String, String> gene2Symbol)
	{

		dbToPathwayToGeneSet.put("REACTOME", new HashMap<>());
		Map<String, Set<String>> reactomeToGeneMap = dbToPathwayToGeneSet.get("REACTOME");
		String species = chipInfo.getSpecies();
		String fName = BMDExpressConstants.getInstance().KEGGFILES[1];
		String relativePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + File.separator + "REACTOME"
				+ File.separator;

		String httpPath = BMDExpressProperties.getInstance().getUpdateURL()
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + "/" + "REACTOME" + "/" + fName;
		File inFile = checkDownload(httpPath, relativePath, fName);
		Map<String, String> titleHash = new HashMap<String, String>();
		try
		{
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));

			for (String line : IOUtils.readLines(reader))
			{
				String[] values = line.split(BMDExpressConstants.getInstance().TAB);

				String pathwayID = values[0];
				if (values != null && values.length > 1)
					titleHash.put(pathwayID, values[1]);
			}
			reader.close();
		}
		catch (IOException e)
		{
			// System.out.println("Read preferences problem: " + e);
			e.printStackTrace();
		}

		fName = BMDExpressConstants.getInstance().KEGGFILES[0];
		relativePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + File.separator;

		httpPath = BMDExpressProperties.getInstance().getUpdateURL() + "/"
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + "/" + fName;
		inFile = checkDownload(httpPath, relativePath, fName);
		String organismCode = null;

		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
			for (String line : IOUtils.readLines(reader))
			{
				String[] array = line.split(BMDExpressConstants.getInstance().TAB);

				if (array[0].startsWith(species) || species.startsWith(array[0]))
				{
					organismCode = array[1];
					break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		fName = organismCode + BMDExpressConstants.getInstance().KEGGFILES[2];
		relativePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + File.separator + "REACTOME"
				+ File.separator + BMDExpressConstants.getInstance().PATHWAYDIRS[1] + File.separator
				+ organismCode + File.separator;
		httpPath = BMDExpressProperties.getInstance().getUpdateURL()
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + "/" + "REACTOME" + "/"
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[1] + "/" + organismCode + "/" + fName;

		inFile = checkDownload(httpPath, relativePath, fName);

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
					if (geneMaps.length < 2)
						continue;

					String gene = geneMaps[0];

					for (String pathwayId : geneMaps[1].split("\\s+"))
					{
						String pathway = titleHash.get(pathwayId);
						if (!reactomeToGeneMap.containsKey(pathway))
							reactomeToGeneMap.put(pathway, new HashSet<>());

						if (gene2Symbol.containsKey(gene))
							reactomeToGeneMap.get(pathway).add(gene2Symbol.get(gene));

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

	}

	protected File checkDownload(String http, String path, String fName)
	{
		File inFile = new File(path);

		if (!inFile.exists())
		{
			inFile.mkdirs();
		}

		inFile = new File(inFile.getAbsolutePath(), fName);
		if (inFile.exists())
			return inFile;

		FileInfo fInfo = URLUtils.download(http, inFile);

		if (fInfo.getLastModified() > 0)
		{
			inFile.setLastModified(fInfo.getLastModified());
		}

		return inFile;
	}

}
