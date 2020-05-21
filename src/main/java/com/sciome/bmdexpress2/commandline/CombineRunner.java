package com.sciome.bmdexpress2.commandline;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.util.ProjectUtilities;

public class CombineRunner
{
	BMDProject project = new BMDProject();

	public void combine(String outputFileName, List<String> asList) throws Exception
	{

		File projectFile = new File(outputFileName);

		// if project exists, then don't overwrite it. but instead load it
		// and append the other projects to it.
		if (projectFile.exists())
		{
			System.out.println("Combine bm2 files: project file already exists: " + outputFileName);
			System.out.println("appending projects");
			FileInputStream fileIn = new FileInputStream(outputFileName);
			BufferedInputStream bIn = new BufferedInputStream(fileIn, 1024 * 2000);

			ObjectInputStream in = new ObjectInputStream(bIn);
			project = (BMDProject) in.readObject();
			in.close();
			fileIn.close();
		}
		for (String selectedFile : asList)
		{
			System.out.println("Combine bm2 files: adding " + selectedFile);
			BMDProject newProject = null;

			FileInputStream fileIn = new FileInputStream(selectedFile);
			BufferedInputStream bIn = new BufferedInputStream(fileIn, 1024 * 2000);

			ObjectInputStream in = new ObjectInputStream(bIn);
			newProject = (BMDProject) in.readObject();
			in.close();
			fileIn.close();
			ProjectUtilities.addProjectToProject(project, newProject);

		}

		File selectedFile = new File(outputFileName);
		FileOutputStream fileOut = new FileOutputStream(selectedFile);

		int bufferSize = 2000 * 1024; // make it a 2mb buffer
		BufferedOutputStream bout = new BufferedOutputStream(fileOut, bufferSize);
		ObjectOutputStream out = new ObjectOutputStream(bout);
		project.setName(selectedFile.getName());
		out.writeObject(project);
		out.close();
		fileOut.close();
	}
}
