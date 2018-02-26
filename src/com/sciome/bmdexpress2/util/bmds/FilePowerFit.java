/*
 * FilePowerFit.java
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

import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.NumberManager;

/**
 * Fit the does response data to power model
 */
public class FilePowerFit extends FileFitBase
{
	private String			powerEXE, dPath;
	private int[]			intParams;

	private final int		maxParams	= 9;
	private final int		SIX			= 6;
	private final double	minDouble	= -9999;
	private final String	newline		= "\n";
	private final String	space1		= " ";

	private final String[]	FLAGS		= { "Parameter Estimates", "Likelihoods of Interest",
			"Tests of Interest", "control", "slope", "power", "fitted ", "BMD = ", "BMDL = ", "BMDU = " };

	public FilePowerFit(int killTime)
	{
		super(killTime);
		this.powerEXE = BMDExpressProperties.getInstance().getPowerEXE();
		this.dPath = BMDExpressConstants.getInstance().TEMP_FOLDER;
	}

	public void setAdverseDirection(int i)
	{
		intParams[2] = i;
	}

	public void setRestrictPower(int r)
	{
		intParams[5] = r;
	}

	@Override
	public double[] fitModel(String name, ModelInputParameters inputParameters, float[] inputX,
			float[] inputY)
	{
		String fileName = name + "_power";
		File infile = createDataFile(fileName, inputParameters, inputX, inputY);
		double[] outputs = NumberManager.initDoubles(maxParams, minDouble);

		if (infile != null)
		{
			executeModel(powerEXE, infile.getPath());// infile.getAbsolutePath());
			File outFile = readOutputs(fileName, outputs);
			infile.delete();
			if (outFile != null && outFile.exists())
				outFile.delete();
			try
			{
				(new File(dPath, fileName + ".002")).delete();
			}
			catch (Exception e)
			{}
			try
			{
				(new File(dPath, fileName + "-power.log")).delete();
			}
			catch (Exception e)
			{}
			try
			{
				(new File(dPath, fileName + "-pow.log")).delete();
			}
			catch (Exception e)
			{}
		}

		return outputs;
	}

	private File createDataFile(String fileName, ModelInputParameters inputParameters, float[] inputX,
			float[] inputY)
	{
		StringBuilder bf = new StringBuilder();
		bf.append("Power" + newline);
		bf.append("BMDS MODEL RUN" + newline);
		// bf.append(name + newline);
		// bf.append(name + newline);
		bf.append(fileName + ".(d)" + newline);;
		bf.append(fileName + ".out" + newline);

		bf.append(inputParameters.getInputType() + space1);
		bf.append(inputParameters.getObservations() + space1);
		bf.append(inputParameters.getAdversDirection() + newline);

		bf.append(inputParameters.getIterations() + space1);
		bf.append(inputParameters.getRelFuncConvergence() + space1);
		bf.append(inputParameters.getParamConvergence() + space1);
		bf.append(inputParameters.getBmdlCalculation() + space1);
		bf.append(inputParameters.getRestirctPower() + space1);
		bf.append(inputParameters.getBmdCalculation() + space1);
		bf.append(inputParameters.getAppend() + space1);
		bf.append(inputParameters.getSmooth() + newline);

		bf.append(inputParameters.getBmrType() + space1);
		bf.append(inputParameters.getBmrLevel() + space1);
		bf.append(inputParameters.getConstantVariance() + space1);
		bf.append(inputParameters.getConfidence() + newline);

		bf.append(inputParameters.getAlpha() + space1);
		bf.append(inputParameters.getRho() + space1);
		bf.append(inputParameters.getControl() + space1);
		bf.append(inputParameters.getSlope() + space1);
		bf.append(inputParameters.getPower() + newline);

		bf.append(inputParameters.getInitialParams() + newline);

		bf.append(inputParameters.getAlpha() + space1);
		bf.append(minDouble + space1);

		bf.append(inputParameters.getControl() + space1);
		bf.append(inputParameters.getSlope() + space1);
		bf.append(inputParameters.getPower() + newline);

		bf.append("DOSE RESPONSE" + newline);

		for (int i = 0; i < inputX.length; i++)
		{
			bf.append(inputX[i] + space1 + inputY[i] + newline);
		}

		try
		{
			File file = new File(dPath, fileName + ".(d)");
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

	private File readOutputs(String fileName, double[] outputs)
	{
		if (!success)
			return new File(dPath, fileName + ".out");
		try
		{
			File file = new File(dPath, fileName + ".out");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			int status = -1;

			try
			{
				while ((line = br.readLine()) != null)
				{
					line = line.trim();

					if (line.equals(FLAGS[0]))
					{
						status = 0;
					}
					else if (line.equals(FLAGS[1]))
					{
						status = 1;
					}
					else if (line.equals(FLAGS[2]))
					{
						status = 2;
					}
					else if (status == 0)
					{
						if (line.startsWith(FLAGS[3]))
						{ // control
							line = line.substring(FLAGS[3].length()).trim();
							String[] array = line.split(" ");

							if (array.length > 0)
							{
								String control = array[0].trim();
								outputs[SIX] = NumberManager.parseDouble(control, minDouble);
							}
						}
						else if (line.startsWith(FLAGS[4]))
						{ // slope
							line = line.substring(FLAGS[4].length()).trim();
							String[] array = line.split(" ");

							if (array.length > 0)
							{
								String slope = array[0].trim();
								outputs[SIX + 1] = NumberManager.parseDouble(slope, minDouble);
							}
						}
						else if (line.startsWith(FLAGS[5]))
						{ // power
							line = line.substring(FLAGS[5].length()).trim();
							String[] array = line.split(" ");

							if (array.length > 0)
							{
								String power = array[0].trim();
								outputs[SIX + 2] = NumberManager.parseDouble(power, minDouble);
							}
						}
					}
					else if (status == 1 && line.startsWith(FLAGS[6]))
					{
						int idx1 = line.indexOf(FLAGS[6]) + FLAGS[6].length();
						int idx2 = line.indexOf(" ", idx1);
						line = line.substring(idx2).trim();
						idx1 = line.indexOf(" ");
						idx2 = line.lastIndexOf(" ");

						String llh = line.substring(0, idx1).trim();
						String aic = line.substring(idx2).trim();

						outputs[4] = NumberManager.parseDouble(llh, minDouble);
						outputs[5] = NumberManager.parseDouble(aic, minDouble);
					}
					else if (status == 2 && line.startsWith("Test 4"))
					{
						String[] array = line.split(" ");

						if (array.length > 0)
						{
							String p = array[array.length - 1];// line.substring(line.lastIndexOf("
																// ")).trim();

							if (p.startsWith("<"))
							{
								p = p.replace('<', '0');
								p = p.replaceFirst("1", "09"); // because of '<'
							}

							status = 3;
							outputs[3] = NumberManager.parseDouble(p, minDouble);
						}
					}
					else if (line.startsWith(FLAGS[7]))
					{
						String[] array = line.split(" ");

						if (array.length > 0)
						{
							String bmd = array[array.length - 1];// line.substring(line.indexOf(FLAGS[6]) +
																	// FLAGS[6].length()).trim();
							outputs[0] = NumberManager.parseDouble(bmd, minDouble);
						}
					}
					else if (line.startsWith(FLAGS[8]))
					{
						String[] array = line.split(" ");

						if (array.length > 0)
						{
							String bmdl = array[array.length - 1];// line.substring(line.indexOf(FLAGS[7]) +
																	// FLAGS[7].length()).trim();
							outputs[1] = NumberManager.parseDouble(bmdl, minDouble);
						}
					}
					else if (line.startsWith(FLAGS[9]))
					{
						String[] array = line.split(" ");
						if (array.length > 0)
						{
							String bmdu = array[array.length - 1];
							outputs[2] = NumberManager.parseDouble(bmdu, minDouble);
						}

					}
				}
				fr.close();
			}
			catch (IOException e)
			{
				System.out.println("Read file problem: " + e);
			}

			return file;
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Input file problem: " + e);
			return null;
		}
	}

}
