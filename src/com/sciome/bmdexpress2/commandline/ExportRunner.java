package com.sciome.bmdexpress2.commandline;

public class ExportRunner
{

	public void analyze(String inputBM2, String outputFile, String outputFormat, String analysisGroup,
			String analysisName)
	{
		System.out.println("export");
		System.out.println(
				inputBM2 + " " + outputFile + " " + outputFormat + " " + analysisGroup + " " + analysisName);

	}
}
