package com.sciome.bmdexpress2.commandline;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;

public class DeleteRunner
{
	BMDProject project = new BMDProject();

	public void analyze(String inputBM2, String analysisGroup, String analysisName)
	{
		if (new File(inputBM2).exists())
		{
			try
			{
				FileInputStream fileIn = new FileInputStream(new File(inputBM2));
				BufferedInputStream bIn = new BufferedInputStream(fileIn, 1024 * 2000);

				ObjectInputStream in = new ObjectInputStream(bIn);
				project = (BMDProject) in.readObject();
				in.close();
				fileIn.close();
			}
			catch (IOException i)
			{
				i.printStackTrace();
			}
			catch (ClassNotFoundException c)
			{
				c.printStackTrace();
			}
		}
		
		if(analysisGroup.equals(BMDExpressCommandLine.EXPRESSION)) {
			Iterator<DoseResponseExperiment> it = project.getDoseResponseExperiments().iterator();
			while(it.hasNext()) {
				DoseResponseExperiment curr = it.next();
				if(curr.getName().equals(analysisName)) {
					it.remove();
					break;
				}
			}
		} else if(analysisGroup.equals(BMDExpressCommandLine.ONE_WAY_ANOVA)) {
			Iterator<OneWayANOVAResults> it = project.getOneWayANOVAResults().iterator();
			while(it.hasNext()) {
				OneWayANOVAResults curr = it.next();
				if(curr.getName().equals(analysisName)) {
					it.remove();
					break;
				}
			}
		} else if(analysisGroup.equals(BMDExpressCommandLine.ORIOGEN)) {
			Iterator<OriogenResults> it = project.getOriogenResults().iterator();
			while(it.hasNext()) {
				OriogenResults curr = it.next();
				if(curr.getName().equals(analysisName)) {
					it.remove();
					break;
				}
			}
		} else if(analysisGroup.equals(BMDExpressCommandLine.WILLIAMS)) {
			Iterator<WilliamsTrendResults> it = project.getWilliamsTrendResults().iterator();
			while(it.hasNext()) {
				WilliamsTrendResults curr = it.next();
				if(curr.getName().equals(analysisName)) {
					it.remove();
					break;
				}
			}
		} else if(analysisGroup.equals(BMDExpressCommandLine.BMD_ANALYSIS)) {
			Iterator<BMDResult> it = project.getbMDResult().iterator();
			while(it.hasNext()) {
				BMDResult curr = it.next();
				if(curr.getName().equals(analysisName)) {
					it.remove();
					break;
				}
			}
		} else if(analysisGroup.equals(BMDExpressCommandLine.CATEGORICAL)) {
			Iterator<CategoryAnalysisResults> it = project.getCategoryAnalysisResults().iterator();
			while(it.hasNext()) {
				CategoryAnalysisResults curr = it.next();
				if(curr.getName().equals(analysisName)) {
					it.remove();
					break;
				}
			}
		}
		
		try
		{
			File selectedFile = new File(inputBM2);
			FileOutputStream fileOut = new FileOutputStream(selectedFile);

			int bufferSize = 2000 * 1024; // make it a 2mb buffer
			BufferedOutputStream bout = new BufferedOutputStream(fileOut, bufferSize);
			ObjectOutputStream out = new ObjectOutputStream(bout);
			project.setName(selectedFile.getName());
			out.writeObject(project);
			out.close();
			fileOut.close();
		}
		catch (IOException i)
		{
			i.printStackTrace();
		}
		
		System.out.println("delete");
		System.out.println(inputBM2 + " " + analysisGroup + " " + analysisName);
	}
}
