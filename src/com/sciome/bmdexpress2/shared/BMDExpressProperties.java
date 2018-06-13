package com.sciome.bmdexpress2.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sciome.bmdexpress2.mvp.model.category.CategoryInput;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAInput;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenInput;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendInput;
import com.sciome.bmdexpress2.mvp.model.stat.BMDInput;
import com.sciome.filter.DataFilter;
import com.sciome.filter.DataFilterPack;

public class BMDExpressProperties
{

	private PropertiesParser			propertiesParser;
	private int							locX, locY, sizeX, sizeY, precision;
	private String						user, imgName, logoName, projectName, updateURL, httpKEGG, proxySet,
			proxyHost, proxyPort, endpoint, sqlservice, timeoutMilliseconds, powerEXE, polyEXE, hillEXE,
			powerVersion, polyVersion, hillVersion, exponentialEXE, exponentialVersion, Rscript,
			pathwayFilterScript, projectPath, expressionPath, exportPath, definedPath;
	private boolean						useWS, usePrecision, useJNI, ctrldown, projectChanged, autoUpdate,
			isWindows, hideTable, hideFilter, hideCharts, applyFilter;

	// boolean to be set if the console version is running
	private boolean						isConsole			= false;

	private File						propertyFile;

	private static BMDExpressProperties	instance			= null;

	private Map<String, DataFilterPack>	dataFilterPackMap	= new HashMap<>();

	private Properties					versionProperties	= new Properties();

	private WilliamsTrendInput			williamsInput;

	private OriogenInput				oriogenInput;

	private OneWayANOVAInput			oneWayInput;

	private BMDInput					bmdInput;

	private CategoryInput				categoryInput;

	private String						processInformation;

	protected BMDExpressProperties()
	{

		propertyFile = new File(
				BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "preferences");
		loadVersionProperties();

		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "preferences",
				"/preferences", false, false);

		loadProperties();
		readPreferences();
		loadDefaultFilters();

		processInformation = ManagementFactory.getRuntimeMXBean().getName().replaceAll("[^a-zA-Z0-9]", "");
		try
		{
			loadInputs();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getNiceFileAppendage()
	{
		return processInformation + System.currentTimeMillis();
	}

	public String getNextTempFile(String dir, String baseName, String ext)
	{
		String fname = processInformation + baseName + String.valueOf(Math.abs(RandomUtils.nextInt()));
		File theFile = new File(dir + File.separator + fname + ext);
		while (theFile.exists())
		{
			fname = processInformation + baseName + String.valueOf(Math.abs(RandomUtils.nextInt()));
			theFile = new File(dir + File.separator + fname + ext);
		}

		return fname;

	}

	private void loadInputs() throws JsonGenerationException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();

		File williamsInputFile = new File(
				BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "williamsInput.json");
		File oneWayInputFile = new File(
				BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "oneWayInput.json");
		File oriogenInputFile = new File(
				BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "oriogenInput.json");
		File bmdInputFile = new File(
				BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "bmdInput.json");
		File categoryInputFile = new File(
				BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "categoryInput.json");

		if (williamsInputFile.exists())
		{
			williamsInput = mapper.readValue(williamsInputFile, WilliamsTrendInput.class);
		}
		else
		{
			williamsInput = new WilliamsTrendInput();
			mapper.writerWithDefaultPrettyPrinter().writeValue(williamsInputFile, williamsInput);
		}
		if (oneWayInputFile.exists())
		{
			oneWayInput = mapper.readValue(oneWayInputFile, OneWayANOVAInput.class);
		}
		else
		{
			oneWayInput = new OneWayANOVAInput();
			mapper.writerWithDefaultPrettyPrinter().writeValue(oneWayInputFile, oneWayInput);
		}
		if (oriogenInputFile.exists())
		{
			oriogenInput = mapper.readValue(oriogenInputFile, OriogenInput.class);
		}
		else
		{
			oriogenInput = new OriogenInput();
			mapper.writerWithDefaultPrettyPrinter().writeValue(oriogenInputFile, oriogenInput);
		}
		if (bmdInputFile.exists())
		{
			bmdInput = mapper.readValue(bmdInputFile, BMDInput.class);
		}
		else
		{
			bmdInput = new BMDInput();
			mapper.writerWithDefaultPrettyPrinter().writeValue(bmdInputFile, bmdInput);
		}
		if (categoryInputFile.exists())
		{
			categoryInput = mapper.readValue(categoryInputFile, CategoryInput.class);
		}
		else
		{
			categoryInput = new CategoryInput();
			mapper.writerWithDefaultPrettyPrinter().writeValue(categoryInputFile, categoryInput);
		}
	}

	public void saveWilliamsInput(WilliamsTrendInput input)
	{
		File williamsInputFile = new File(
				BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "williamsInput.json");
		ObjectMapper mapper = new ObjectMapper();
		this.williamsInput = input;
		try
		{
			mapper.writerWithDefaultPrettyPrinter().writeValue(williamsInputFile, williamsInput);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveOriogenInput(OriogenInput input)
	{
		File oriogenInputFile = new File(
				BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "oriogenInput.json");
		ObjectMapper mapper = new ObjectMapper();
		this.oriogenInput = input;
		try
		{
			mapper.writerWithDefaultPrettyPrinter().writeValue(oriogenInputFile, oriogenInput);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveOneWayANOVAInput(OneWayANOVAInput input)
	{
		File oneWayInputFile = new File(
				BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "oneWayInput.json");
		ObjectMapper mapper = new ObjectMapper();
		this.oneWayInput = input;
		try
		{
			mapper.writerWithDefaultPrettyPrinter().writeValue(oneWayInputFile, oneWayInput);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveBMDInput(BMDInput input)
	{
		File bmdInputFile = new File(
				BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "bmdInput.json");
		ObjectMapper mapper = new ObjectMapper();
		this.bmdInput = input;
		try
		{
			mapper.writerWithDefaultPrettyPrinter().writeValue(bmdInputFile, bmdInput);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveCategoryInput(CategoryInput input)
	{
		File categoryInputFile = new File(
				BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "categoryInput.json");
		ObjectMapper mapper = new ObjectMapper();
		this.categoryInput = input;
		try
		{
			mapper.writerWithDefaultPrettyPrinter().writeValue(categoryInputFile, categoryInput);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void loadDefaultFilters()
	{
		// BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + name + ".DEFAULTFILTER"),

		for (File file : new File(BMDExpressConstants.getInstance().BMDBASEPATH).listFiles())
		{
			if (file.getName().endsWith(".DEFAULTFILTER"))
			{
				try
				{
					String name = file.getName().replaceAll(".DEFAULTFILTER", "");
					dataFilterPackMap.put(name, this.readDefaultFilter(name));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	public static BMDExpressProperties getInstance()
	{
		if (instance == null)
		{
			instance = new BMDExpressProperties();
		}
		return instance;
	}

	private void loadProperties()
	{
		propertiesParser = new PropertiesParser(propertyFile);
	}

	private void readPreferences()
	{

		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "PathwayFilter.R", "/PathwayFilter.R", false, false);

		// copy all the bmds model files.
		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "hill", "/hill", true, true);
		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "hill.exe", "/hill.exe", true, true);
		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "hill_mac", "/hill_mac", true, true);

		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "poly", "/poly", true, true);
		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "poly.exe", "/poly.exe", true, true);
		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "poly_mac", "/poly_mac", true, true);

		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "power", "/power", true, true);
		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "power.exe", "/power.exe", true, true);
		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "power_mac", "/power_mac", true, true);

		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "exponential", "/exponential", true, true);
		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "exponential.exe", "/exponential.exe", true, true);
		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "exponential_mac", "/exponential_mac", true, true);

		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "libgcc_s_dw2-1.dll", "/libgcc_s_dw2-1.dll", true, true);
		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "libgfortran-3.dll", "/libgfortran-3.dll", true, true);
		checkLocalFiles(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + "libquadmath-0.dll", "/libquadmath-0.dll", true, true);

		locX = propertiesParser.getPropertyInt("location.x");
		locY = propertiesParser.getPropertyInt("location.y");
		sizeX = propertiesParser.getPropertyInt("size.x");
		sizeY = propertiesParser.getPropertyInt("size.y");
		precision = propertiesParser.getPropertyInt("decimal.number");
		user = propertiesParser.getProperty("user");
		imgName = propertiesParser.getProperty("icon.image");
		logoName = propertiesParser.getProperty("logo.image");
		proxySet = propertiesParser.getProperty("http.proxySet");
		proxyHost = propertiesParser.getProperty("http.proxyHost");
		proxyPort = propertiesParser.getProperty("http.proxyPort");
		updateURL = propertiesParser.getProperty("http.update");
		httpKEGG = propertiesParser.getProperty("http.KEGG");
		endpoint = propertiesParser.getProperty("ws.endpoint");
		sqlservice = propertiesParser.getProperty("ws.sqlservice");
		timeoutMilliseconds = propertiesParser.getProperty("timeout.milliseconds");
		useWS = propertiesParser.getPropertyBoolean("web.services");
		usePrecision = propertiesParser.getPropertyBoolean("use.precision");
		useJNI = propertiesParser.getPropertyBoolean("JNI.enabled");
		autoUpdate = propertiesParser.getPropertyBoolean("auto.update");
		Rscript = propertiesParser.getProperty("Rscript");
		pathwayFilterScript = propertiesParser.getProperty("pathway.filter");

		projectPath = propertiesParser.getProperty("file.projectpath");
		expressionPath = propertiesParser.getProperty("file.expressionpath");
		exportPath = propertiesParser.getProperty("file.exportpath");
		definedPath = propertiesParser.getProperty("file.definedpath");

		hideTable = propertiesParser.getPropertyBoolean("hidetable");
		hideCharts = propertiesParser.getPropertyBoolean("hidecharts");
		hideFilter = propertiesParser.getPropertyBoolean("hidefilter");
		applyFilter = propertiesParser.getPropertyBoolean("applyfilter");

		String powerEXEName = "power.exe";
		String polyEXEName = "poly.exe";
		String hillEXEName = "hill.exe";
		String expEXEName = "exponential.exe";

		powerEXE = BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib" + File.separator
				+ powerEXEName;
		polyEXE = BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib" + File.separator
				+ polyEXEName;
		hillEXE = BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib" + File.separator
				+ hillEXEName;
		exponentialEXE = BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "lib"
				+ File.separator + expEXEName;
		checkOperatingSystem();

		if (projectPath == null || projectPath.equals(""))
		{
			projectPath = System.getProperty("user.home");
		}

		if (expressionPath == null || expressionPath.equals(""))
		{
			expressionPath = System.getProperty("user.home");
		}

		if (exportPath == null || exportPath.equals(""))
		{
			exportPath = System.getProperty("user.home");
		}

		if (definedPath == null || definedPath.equals(""))
		{
			definedPath = System.getProperty("user.home");
		}

	}

	/*
	 * Check to see if the local file exists. If not, copy it to the local file from the resource path
	 */
	private void checkLocalFiles(String localFile, String resourcePath, boolean makeExecutable,
			boolean forceCopy)
	{
		// if (!new File(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "preferences")
		// .exists())
		if (!new File(localFile).exists() || forceCopy)
		{
			// copy the default preferences file to the bmdexpress2 home directory
			InputStream is = this.getClass().getResourceAsStream(resourcePath);
			FileOutputStream fos = null;
			try
			{
				fos = new FileOutputStream(localFile);
				byte[] buf = new byte[2048];
				int r = is.read(buf);
				while (r != -1)
				{
					fos.write(buf, 0, r);
					r = is.read(buf);
				}
				fos.close();
			}
			catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		if (makeExecutable)
		{
			new File(localFile).setExecutable(true);
		}
	}

	private void checkOperatingSystem()
	{
		String os = System.getProperty("os.name");
		isWindows = false;
		// if (os.startsWith("Linux")) { // for linux OS
		if (os.toLowerCase().contains("mac"))
		{ // for linux or Unix OS
			powerEXE = powerEXE.replace(".exe", "_mac");
			polyEXE = polyEXE.replace(".exe", "_mac");
			hillEXE = hillEXE.replace(".exe", "_mac");
			exponentialEXE = exponentialEXE.replace(".exe", "_mac");
		}
		else if (!os.toLowerCase().startsWith("windows"))
		{ // for linux or Unix OS
			powerEXE = truncateExecutable(powerEXE);
			polyEXE = truncateExecutable(polyEXE);
			hillEXE = truncateExecutable(hillEXE);
			exponentialEXE = truncateExecutable(exponentialEXE);
		}
		else
		{
			// RScript is part of installation
			if (Rscript.equals(""))
			{
				File file = new File(".");
				Rscript = file.getAbsolutePath() + File.separator + "R-3.2.1" + File.separator + "bin"
						+ File.separator + "Rscript.exe";
			}
			this.isWindows = true;
		}

		polyVersion = getModelVersion(polyEXE);
		powerVersion = getModelVersion(powerEXE);
		hillVersion = getModelVersion(hillEXE);
		exponentialVersion = getModelVersion(exponentialEXE);
	}

	public boolean isWindows()
	{
		return isWindows;
	}

	public void setWindows(boolean isWindows)
	{
		this.isWindows = isWindows;
	}

	private String truncateExecutable(String executable)
	{
		if (executable != null && executable.endsWith(".exe"))
		{
			executable = executable.substring(0, executable.length() - 4);
		}

		return executable;
	}

	public PropertiesParser getPropertiesParser()
	{
		return propertiesParser;
	}

	public void setPropertiesParser(PropertiesParser propertiesParser)
	{
		this.propertiesParser = propertiesParser;
	}

	public int getLocX()
	{
		return locX;
	}

	public void setLocX(int locX)
	{
		this.locX = locX;
		propertiesParser.setProperty("location.x", locX);
	}

	public int getLocY()
	{
		return locY;
	}

	public void setLocY(int locY)
	{
		this.locY = locY;
		propertiesParser.setProperty("location.y", locY);
	}

	public int getSizeX()
	{
		return sizeX;
	}

	public void setSizeX(int sizeX)
	{
		this.sizeX = sizeX;
		propertiesParser.setProperty("size.x", sizeX);
	}

	public int getSizeY()
	{
		return sizeY;
	}

	public void setSizeY(int sizeY)
	{
		this.sizeY = sizeY;
		propertiesParser.setProperty("size.y", sizeY);
	}

	public int getPrecision()
	{
		return precision;
	}

	public void setPrecision(int precision)
	{
		this.precision = precision;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getImgName()
	{
		return imgName;
	}

	public void setImgName(String imgName)
	{
		this.imgName = imgName;
	}

	public String getLogoName()
	{
		return logoName;
	}

	public void setLogoName(String logoName)
	{
		this.logoName = logoName;
	}

	public String getProjectName()
	{
		return projectName;
	}

	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}

	public String getUpdateURL()
	{
		return updateURL;
	}

	public void setUpdateURL(String updateURL)
	{
		this.updateURL = updateURL;
	}

	public String getHttpKEGG()
	{
		return httpKEGG;
	}

	public void setHttpKEGG(String httpKEGG)
	{
		this.httpKEGG = httpKEGG;
	}

	public String getProxySet()
	{
		return proxySet;
	}

	public void setProxySet(String proxySet)
	{
		this.proxySet = proxySet;
	}

	public String getProxyHost()
	{
		return proxyHost;
	}

	public void setProxyHost(String proxyHost)
	{
		this.proxyHost = proxyHost;
	}

	public String getProxyPort()
	{
		return proxyPort;
	}

	public void setProxyPort(String proxyPort)
	{
		this.proxyPort = proxyPort;
	}

	public String getEndpoint()
	{
		return endpoint;
	}

	public void setEndpoint(String endpoint)
	{
		this.endpoint = endpoint;
	}

	public String getSqlservice()
	{
		return sqlservice;
	}

	public void setSqlservice(String sqlservice)
	{
		this.sqlservice = sqlservice;
	}

	public String getTimeoutMilliseconds()
	{
		return timeoutMilliseconds;
	}

	public void setTimeoutMilliseconds(String timeoutMilliseconds)
	{
		this.timeoutMilliseconds = timeoutMilliseconds;
	}

	public String getPowerEXE()
	{
		return powerEXE;
	}

	public void setPowerEXE(String powerEXE)
	{
		this.powerEXE = powerEXE;
	}

	public String getPolyEXE()
	{
		return polyEXE;
	}

	public void setPolyEXE(String polyEXE)
	{
		this.polyEXE = polyEXE;
	}

	public String getHillEXE()
	{
		return hillEXE;
	}

	public void setHillEXE(String hillEXE)
	{
		this.hillEXE = hillEXE;
	}

	public String getExponentialEXE()
	{
		return exponentialEXE;
	}

	public void setExponentialEXE(String exponentialEXE)
	{
		this.exponentialEXE = exponentialEXE;
	}

	public boolean isUseWS()
	{
		return useWS;
	}

	public void setUseWS(boolean useWS)
	{
		this.useWS = useWS;
	}

	public boolean isUsePrecision()
	{
		return usePrecision;
	}

	public void setUsePrecision(boolean usePrecision)
	{
		this.usePrecision = usePrecision;
	}

	public boolean isUseJNI()
	{
		return useJNI;
	}

	public void setUseJNI(boolean useJNI)
	{
		this.useJNI = useJNI;
	}

	public boolean isCtrldown()
	{
		return ctrldown;
	}

	public void setCtrldown(boolean ctrldown)
	{
		this.ctrldown = ctrldown;
	}

	public boolean isProjectChanged()
	{
		return projectChanged;
	}

	public void setProjectChanged(boolean projectChanged)
	{
		this.projectChanged = projectChanged;
	}

	public boolean isAutoUpdate()
	{
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate)
	{
		this.autoUpdate = autoUpdate;
	}

	public String getRscript()
	{
		return Rscript;
	}

	public void setRscript(String rscript)
	{
		Rscript = rscript;
		propertiesParser.setProperty("Rscript", Rscript);
		propertiesParser.writeFile();
	}

	public String getProjectPath()
	{
		return projectPath;
	}

	public void setProjectPath(String projectPath)
	{
		this.projectPath = projectPath;
		propertiesParser.setProperty("file.projectpath", projectPath);
	}

	public String getExpressionPath()
	{
		return expressionPath;
	}

	public void setExpressionPath(String expressionPath)
	{
		this.expressionPath = expressionPath;
		propertiesParser.setProperty("file.expressionpath", expressionPath);
	}

	public String getExportPath()
	{
		return exportPath;
	}

	public void setExportPath(String exportPath)
	{
		this.exportPath = exportPath;
		propertiesParser.setProperty("file.exportpath", exportPath);
	}

	public String getDefinedPath()
	{
		return definedPath;
	}

	public void setDefinedPath(String definedPath)
	{
		this.definedPath = definedPath;
		propertiesParser.setProperty("file.definedpath", definedPath);
	}

	public String getPathwayFilterScript()
	{
		return pathwayFilterScript;
	}

	public void setPathwayFilterScript(String pathwayFilterScript)
	{
		this.pathwayFilterScript = pathwayFilterScript;
	}

	public String getPowerVersion()
	{
		return powerVersion;
	}

	public void setPowerVersion(String powerVersion)
	{
		this.powerVersion = powerVersion;
	}

	public String getPolyVersion()
	{
		return polyVersion;
	}

	public void setPolyVersion(String polyVersion)
	{
		this.polyVersion = polyVersion;
	}

	public String getHillVersion()
	{
		return hillVersion;
	}

	public void setHillVersion(String hillVersion)
	{
		this.hillVersion = hillVersion;
	}

	public boolean isHideTable()
	{
		return hideTable;
	}

	public void setHideTable(boolean hideTable)
	{
		this.hideTable = hideTable;
		propertiesParser.setProperty("hidetable", String.valueOf(hideTable));
	}

	public boolean isHideFilter()
	{
		return hideFilter;
	}

	public void setHideFilter(boolean hideFilter)
	{
		this.hideFilter = hideFilter;
		propertiesParser.setProperty("hidefilter", String.valueOf(hideFilter));
	}

	public boolean isHideCharts()
	{
		return hideCharts;
	}

	public void setHideCharts(boolean hideVisualization)
	{
		this.hideCharts = hideVisualization;
		propertiesParser.setProperty("hidecharts", String.valueOf(hideCharts));
	}

	public boolean isApplyFilter()
	{
		return applyFilter;
	}

	public void setApplyFilter(boolean applyFilter)
	{
		this.applyFilter = applyFilter;
		propertiesParser.setProperty("applyfilter", String.valueOf(applyFilter));
	}

	public void save()
	{
		propertiesParser.writeFile();
	}

	public DataFilterPack getDataFilterPackMap(String key)
	{
		return dataFilterPackMap.get(key);
	}

	public String getVersion()
	{
		return "BMDExpress " + versionProperties.getProperty("bmdexpress2.version") + " BETA";
	}

	public void putDataFilterPackMap(String key, DataFilterPack pack)
	{
		// so we don't persist references to large datasets, let's make a copy of this.
		DataFilterPack packCopy = pack.copy();
		dataFilterPackMap.put(key, packCopy);
		// this.saveDefaultFilter(key);
	}

	public String getTimeStamp()
	{
		String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm.ss").format(new Date());

		return timeStamp;
	}

	private String getModelVersion(String modelExe)
	{
		try
		{
			// Use a ProcessBuilder
			ProcessBuilder pb = new ProcessBuilder(modelExe, "-v");

			Process p = pb.start();
			int r = p.waitFor(); // Let the process finish.
			InputStream is = p.getErrorStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String output = "";
			String line = null;
			while ((line = br.readLine()) != null)
			{
				output += line;
			}

			if (r == 0)
			{
				return output;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	private void loadVersionProperties()
	{
		InputStream in = this.getClass().getResourceAsStream("/version.properties");
		try
		{
			versionProperties.load(in);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public String getLicense()
	{

		try
		{
			InputStream in = this.getClass().getResourceAsStream("/license.txt");
			return IOUtils.toString(in, StandardCharsets.UTF_8.name());

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public String getExponentialVersion()
	{
		return exponentialVersion;
	}

	public WilliamsTrendInput getWilliamsInput()
	{
		return williamsInput;
	}

	public OriogenInput getOriogenInput()
	{
		return oriogenInput;
	}

	public OneWayANOVAInput getOneWayInput()
	{
		return oneWayInput;
	}

	public BMDInput getBmdInput()
	{
		return bmdInput;
	}

	public CategoryInput getCategoryInput()
	{
		return categoryInput;
	}

	public void updateFilter(String name, List<String> values)
	{
		try
		{
			PrintWriter writer = new PrintWriter(
					BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + name + ".FILTER",
					"UTF-8");
			writer.println(String.join("\n", values));
			writer.close();
		}
		catch (IOException e)
		{
			// do something
			e.printStackTrace();
		}

	}

	public void saveDefaultFilter(String name)
	{
		try
		{
			ObjectMapper mapper = new ObjectMapper();

			/**
			 * To make the JSON String pretty use the below code
			 */
			mapper.writerWithDefaultPrettyPrinter().writeValue(new File(
					BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + name + ".DEFAULTFILTER"),
					dataFilterPackMap.get(name));

		}
		catch (IOException e)
		{
			// do something
			e.printStackTrace();
		}

	}

	public DataFilterPack readDefaultFilter(String name)
	{
		try
		{
			File defaultFilterFile = new File(
					BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + name + ".DEFAULTFILTER");
			if (!defaultFilterFile.exists())
				return null;

			ObjectMapper mapper = new ObjectMapper();
			DataFilterPack filterpack = mapper.readValue(defaultFilterFile, DataFilterPack.class);
			return filterpack;

		}
		catch (IOException e)
		{
			// do something
			e.printStackTrace();
		}
		return null;

	}

	public List<String> getFilters(String name)
	{
		List<String> values = new ArrayList<>();
		Set<String> valueSet = new HashSet<>();
		if (!(new File(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + name + ".FILTER")
				.exists()))
			return values;
		try
		{
			valueSet.addAll(Files.readAllLines((new File(
					BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + name + ".FILTER")
							.toPath())));

			DataFilterPack dfp = getDataFilterPackMap(name);
			if (dfp != null && dfp.getDataFilters() != null)
				for (DataFilter df : dfp.getDataFilters())
					valueSet.add(df.getKey());

			values.addAll(valueSet);
			Collections.sort(values);
			return values;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return values;

	}

	public String getVersionFromFile()
	{

		File vFile = new File(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "version");

		byte[] encoded;
		String version = "";
		try
		{
			encoded = Files.readAllBytes(vFile.toPath());
			version = new String(encoded, Charset.defaultCharset());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return version.trim();

	}

	public String getVersionInfo()
	{

		File vFile = new File(this.getClass().getResource("/VersionInfo.txt").getPath());
		InputStream is = this.getClass().getResourceAsStream("/VersionInfo.txt");
		String versioninfo = getStringFromInputStream(is);
		return versioninfo.trim();

	}

	public void writeVersion(String version)
	{
		File vFile = new File(BMDExpressConstants.getInstance().BMDBASEPATH + File.separator + "version");
		try (PrintWriter out = new PrintWriter(vFile))
		{
			out.println(version);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getStringFromInputStream(InputStream is)
	{

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try
		{
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null)
				sb.append(line);

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

	public boolean isConsole()
	{
		return isConsole;
	}

	public void setIsConsole(boolean is)
	{
		isConsole = is;
	}

}
