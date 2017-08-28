package com.sciome.bmdexpress2.commandline;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sciome.bmdexpress2.mvp.model.BMDProject;

public class ExportRunner
{

	public void analyze(String inputBM2, String outputFile, String outputFormat, String analysisGroup,
			String analysisName)
	{
		System.out.println("export");
		System.out.println(
				inputBM2 + " " + outputFile + " " + outputFormat + " " + analysisGroup + " " + analysisName);

	}

	public void exportToJson(BMDProject project, String jsonExportFileName) throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();

		/**
		 * To make the JSON String pretty use the below code
		 */
		File testFile = new File(jsonExportFileName);
		mapper.writerWithDefaultPrettyPrinter().writeValue(testFile, project);

	}
}
