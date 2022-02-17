/*
 * FileHillFit.java
 * Created 11/2/2006
 *
 */

package com.sciome.bmdexpress2.util.bmds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.NumberManager;

/**
 * Fit the does response data to power model
 */
public class FileHillFit extends FileFitBase
{
	private String hillEXE, dPath;
	private int[] intParams;
	private final int SIX = 6;
	private final int outMax = 10;
	private final double minDouble = -9999;
	private final String newline = "\n";
	private final String space1 = " ";

	private final String[] FLAGS = { "Wald Confidence Interval", "Asymptotic Correlation Matrix",
			"Likelihoods of Interest", "Tests of Interest", "beta_", "fitted ", "BMD = ", "BMDL = ",
			"BMDU = " };

	public FileHillFit(int killTime, String tmpFolder)
	{
		super(killTime);
		this.hillEXE = BMDExpressProperties.getInstance().getHillEXE(tmpFolder);
		if (tmpFolder != null && !tmpFolder.equals(""))
			this.dPath = tmpFolder;
		else
			this.dPath = BMDExpressConstants.getInstance().TEMP_FOLDER;
	}

	public void setAdverseDirection(int i)
	{
		intParams[2] = i;
	}

	public void setResitictPoly(int r)
	{
		intParams[5] = r;
	}

	@Override
	public double[] fitModel(String name, ModelInputParameters inputParameters, float[] inputX,
			float[] inputY)
	{
		File infile = createDataFile(name, inputParameters, inputX, inputY);
		double[] outputs = NumberManager.initDoubles(outMax, minDouble);

		if (infile != null)
		{
			// System.out.println("Pathf = " + infile.getPath());
			executeModel(hillEXE, infile.getPath());// infile.getAbsolutePath());
			File outFile = readOutputs(name, outputs);
			infile.delete();
			if (outFile != null && outFile.exists())
				outFile.delete();
			try
			{
				(new File(dPath, name + "_hill.002")).delete();
			}
			catch (Exception e)
			{}
			try
			{
				(new File(dPath, name + "-hill.log")).delete();
			}
			catch (Exception e)
			{}
			try
			{
				(new File(dPath, name + "_hill-Hil.log")).delete();
			}
			catch (Exception e)
			{}
		}

		return outputs;
	}

	private File createDataFile(String name, ModelInputParameters inputParameters, float[] inputX,
			float[] inputY)
	{
		StringBuilder bf = new StringBuilder();
		bf.append("Hill" + newline);
		bf.append("BMDS MODEL RUN" + newline);
		bf.append(name + newline);;
		bf.append("Hill" + newline);

		bf.append(inputParameters.getInputType() + space1);
		bf.append(inputParameters.getObservations() + space1);
		bf.append(inputParameters.getAdversDirection() + newline);

		bf.append(inputParameters.getIterations() + space1);
		bf.append(inputParameters.getRelFuncConvergence() + space1);
		bf.append(inputParameters.getParamConvergence() + space1);
		bf.append(inputParameters.getBmdlCalculation() + space1);
		bf.append(inputParameters.getRestrictHill() + space1);
		bf.append(inputParameters.getBmdCalculation() + space1);
		bf.append(inputParameters.getAppend() + space1);
		bf.append(inputParameters.getSmooth() + newline);// 1.00E-08 1.00E-08 0 1 1 0 0

		double bmrLevel = inputParameters.getBmrLevel();
		int bmrType = inputParameters.getBmrType();

		bf.append(bmrType + space1);
		bf.append(bmrLevel + space1);
		bf.append(inputParameters.getConstantVariance() + space1);
		bf.append(inputParameters.getConfidence() + newline);

		bf.append(inputParameters.getAlpha() + space1);
		bf.append(minDouble + space1);
		bf.append(inputParameters.getIntercept() + space1);
		bf.append(inputParameters.getV() + space1);
		bf.append(inputParameters.getN() + space1);
		bf.append(inputParameters.getK() + newline);

		bf.append(inputParameters.getInitialParams() + newline);

		bf.append(inputParameters.getAlpha() + space1);
		bf.append(inputParameters.getRho() + space1);
		bf.append(inputParameters.getIntercept() + space1);
		bf.append(inputParameters.getV() + space1);
		bf.append(inputParameters.getN() + space1);
		bf.append(inputParameters.getK() + newline);

		bf.append("DOSE RESPONSE" + newline);

		for (int i = 0; i < inputX.length; i++)
		{
			bf.append(inputX[i] + space1 + inputY[i] + newline);
		}

		try
		{
			File file = new File(dPath, name + "_hill.(d)");
			// System.out.println(file.getAbsolutePath());
			PrintWriter out = new PrintWriter(new FileWriter(file, false));
			out.write(bf.toString());
			out.close();

			return file;
		}
		catch (IOException ie)
		{
			System.out.println("Write to file problem: " + ie);
		}

		return null;
	}

	private File readOutputs(String name, double[] outputs)
	{
		if (!success)
			return new File(dPath, name + "_hill.out");
		try
		{
			File file = new File(dPath, name + "_hill.out");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			int status = 0;
			Pattern patternI = Pattern.compile("intercept +(\\S+) +");// "intercept +(\\d+\\.?\\d*)");
			Pattern patternV = Pattern.compile(" v +(\\S+) +"); // " v +(-?\\d+\\.?\\d*)");
			Pattern patternN = Pattern.compile(" n +(\\S+) +"); // " n +(-?\\d+\\.?\\d*)");
			Pattern patternK = Pattern.compile(" k +(\\S+) +"); // " k +(-?\\d+\\.?\\d*)");

			try
			{
				while ((line = br.readLine()) != null)
				{
					if (line.indexOf(FLAGS[0]) > 0)
					{
						status = 1;
					}
					else if (line.indexOf(FLAGS[1]) > 0)
					{
						status = 2;
					}
					else if (line.indexOf(FLAGS[2]) > 0)
					{
						status = 3;
					}
					else if (line.indexOf(FLAGS[3]) > 0)
					{
						status = 4;
					}
					else if (status == 1)
					{// && line.indexOf(FLAGS[4]) > 0) {
						if (line.indexOf("intercept") >= 0)
						{
							Matcher matcher = patternI.matcher(line);

							if (matcher.find())
							{
								String intercept = matcher.group(1);
								// System.out.println("intercept: " + intercept);
								outputs[SIX] = NumberManager.parseDouble(intercept, minDouble);
							}
						}
						else if (line.indexOf(" v  ") > 0)
						{
							Matcher matcher = patternV.matcher(line);

							if (matcher.find())
							{
								String v = matcher.group(1);
								// System.out.println("v: " + v);
								outputs[SIX + 1] = NumberManager.parseDouble(v, minDouble);
							}
						}
						else if (line.indexOf(" n  ") > 0)
						{
							Matcher matcher = patternN.matcher(line);

							if (matcher.find())
							{
								String n = matcher.group(1);
								// System.out.println("n: " + n);
								outputs[SIX + 2] = NumberManager.parseDouble(n, minDouble);
							}
						}
						else if (line.indexOf(" k  ") > 0)
						{
							Matcher matcher = patternK.matcher(line);

							if (matcher.find())
							{
								String k = matcher.group(1);
								// System.out.println("k: " + k);
								outputs[SIX + 3] = NumberManager.parseDouble(k, minDouble);
							}
						}
					}
					else if (status == 3 && line.indexOf(FLAGS[5]) > 0)
					{
						int idx1 = line.indexOf(FLAGS[5]) + FLAGS[5].length();
						int idx2 = line.indexOf(" ", idx1);
						line = line.substring(idx2).trim();
						idx1 = line.indexOf(" ");
						idx2 = line.lastIndexOf(" ");

						String llh = line.substring(0, idx1).trim();
						String aic = line.substring(idx2).trim();

						outputs[4] = NumberManager.parseDouble(llh, minDouble);
						outputs[5] = NumberManager.parseDouble(aic, minDouble);

					}
					else if (status == 4 && line.indexOf("Test 4") > 0)
					{
						String p = line.substring(line.lastIndexOf(" ")).trim();
						if (p.startsWith("<"))
						{
							p = p.replace('<', '0');
							p = p.replaceFirst("1", "09");
						}

						status = 5;
						outputs[3] = NumberManager.parseDouble(p, minDouble);
					}

					else if (line.indexOf(FLAGS[6]) > 0)
					{
						String bmd = line.substring(line.indexOf(FLAGS[6]) + FLAGS[6].length()).trim();
						outputs[0] = NumberManager.parseDouble(bmd, minDouble);
					}
					else if (line.indexOf(FLAGS[7]) > 0)
					{
						String bmdl = line.substring(line.indexOf(FLAGS[7]) + FLAGS[7].length()).trim();
						outputs[1] = NumberManager.parseDouble(bmdl, minDouble);
					}
					else if (line.indexOf(FLAGS[8]) >= 0)
					{
						String bmdu = line.substring(line.indexOf(FLAGS[8]) + FLAGS[8].length()).trim();
						outputs[2] = NumberManager.parseDouble(bmdu, minDouble);
					}
				}
				fr.close();
			}
			catch (IOException e)
			{

			}

			return file;
		}
		catch (FileNotFoundException e)
		{

			return null;
		}
	}

}
