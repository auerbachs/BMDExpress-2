package com.sciome.bmdexpress2.shared;

import java.io.File;

public class BMDExpressConstants
{

	private static BMDExpressConstants	instance		= null;

	public final String					TEMP_FOLDER;

	// annotation
	public final String					TITLE;
	public final String					BASEDIR;

	public final String					ANNOTDIR;
	public final String					ARRAYDIR;
	public final String					GODIR;
	public final String					PATHWAYDIR;

	public final String					MICROARRAYGZ;
	public final String					TAB;
	public final String					NEWLINE;

	public final String[]				PROVIDERS		= { "Affymetrix", "Agilent" };;

	public final String[]				TYPES			= { "Update Annotations From Web",
			"Update Annotations From File" };;

	public final String[]				ANNOTFILES		= { "probe2gene.gz", "genes2gos.gz",
			"genes2symbols.gz" };;

	// Gene Ontology update
	public final String					GOFILENAME;

	// used for pathways update
	public final String[]				PATHWAYDBS		= { "KEGG", "BioCarta" };

	public final String[]				PATHWAYDIRS		= { "pathway", "organisms" };

	public final String[]				KEGGFILES		= { "taxonomy.KEGG", "map_title.tab.gz",
			"_gene_map.tab.gz" };

	// From ArrayInfo
	public final String					ANNOTATION_BASE_PATH;

	public final String[]				TITLES			= { "Automatic Update - Array Annotation",
			"Automatic Update - KEGG Pathways" };

	public final String[]				GO_CATEGORIES	= { "universal", "biological_process",
			"molecular_function", "cellular_component" };
	public final String[]				GO_SHORTS		= { "ALL", "BP", "MF", "CC" };

	public final String					BMDBASEPATH;

	public final String					BMDBASEPATH_LIB;

	public final String					COMMA			= ",";
	public final String					SEMICOLON		= ";";
	public final String					GO_WEB			= "http://amigo.geneontology.org/amigo/term/";
	public final String					PATHWAY_WEB		= "http://reactome.org/content/detail/";
	public final String					NTP_WWW			= "http://ntp.niehs.nih.gov/";
	public final String					SCIOM_WWW		= "http://www.sciome.com";
	public final String					EPA_WWW			= "https://www.epa.gov/";
	public final String					TUTORIAL_URL	= "https://github.com/auerbachs/BMDExpress-2.0/wiki/";

	public final String					HC_WWW			= "http://www.hc-sc.gc.ca/index-eng.php";

	protected BMDExpressConstants()
	{

		BMDBASEPATH = System.getProperty("user.home") + File.separator + "bmdexpress2";
		if (!new File(BMDBASEPATH).exists())
		{
			new File(BMDBASEPATH).mkdir();
		}
		TEMP_FOLDER = BMDBASEPATH + File.separator + "tmp" + File.separator;
		if (!new File(TEMP_FOLDER).exists())
		{
			new File(TEMP_FOLDER).mkdir();
		}

		BMDBASEPATH_LIB = BMDBASEPATH + File.separator + "lib";
		if (!new File(BMDBASEPATH_LIB).exists())
		{
			new File(BMDBASEPATH_LIB).mkdir();
		}

		TITLE = "Automatic Annotation Update";
		BASEDIR = "data";

		ANNOTDIR = "annotations";
		ARRAYDIR = "arrays";
		GODIR = "go";
		PATHWAYDIR = "pathway";

		MICROARRAYGZ = "microarray.gz";
		TAB = "\t";
		NEWLINE = "\n";

		// Gene Ontology update
		GOFILENAME = "gotermlevel.gz";

		// From ArrayInfo
		ANNOTATION_BASE_PATH = BMDBASEPATH + File.separator + "data" + File.separator + "annotations";

	}

	protected BMDExpressConstants(String basePath)
	{

		BMDBASEPATH = basePath;
		if (!new File(BMDBASEPATH).exists())
		{
			new File(BMDBASEPATH).mkdir();
		}
		TEMP_FOLDER = BMDBASEPATH + File.separator + "tmp" + File.separator;
		if (!new File(TEMP_FOLDER).exists())
		{
			new File(TEMP_FOLDER).mkdir();
		}

		BMDBASEPATH_LIB = BMDBASEPATH + File.separator + "lib";
		if (!new File(BMDBASEPATH_LIB).exists())
		{
			new File(BMDBASEPATH_LIB).mkdir();
		}

		TITLE = "Automatic Annotation Update";
		BASEDIR = "data";

		ANNOTDIR = "annotations";
		ARRAYDIR = "arrays";
		GODIR = "go";
		PATHWAYDIR = "pathway";

		MICROARRAYGZ = "microarray.gz";
		TAB = "\t";
		NEWLINE = "\n";

		// Gene Ontology update
		GOFILENAME = "gotermlevel.gz";

		// From ArrayInfo
		ANNOTATION_BASE_PATH = BMDBASEPATH + File.separator + "data" + File.separator + "annotations";

	}

	public static BMDExpressConstants getInstance()
	{
		if (instance == null)
		{
			instance = new BMDExpressConstants();
		}
		return instance;
	}

	public static BMDExpressConstants getInstance(String basePath)
	{
		if (instance == null)
		{
			instance = new BMDExpressConstants(basePath);
		}
		return instance;
	}
}
