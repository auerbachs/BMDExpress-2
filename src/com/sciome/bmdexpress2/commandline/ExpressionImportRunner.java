package com.sciome.bmdexpress2.commandline;

import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.LogTransformationEnum;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.service.ProjectNavigationService;
import com.sciome.bmdexpress2.util.ExperimentFileUtil;
import com.sciome.bmdexpress2.util.annotation.FileAnnotation;


public class ExpressionImportRunner
{

	public DoseResponseExperiment runExpressionImport(File file, String chipID, String outputName,
			LogTransformationEnum logtransformation)
	{
		ProjectNavigationService service = new ProjectNavigationService();

		DoseResponseExperiment doseResponseExperiment = ExperimentFileUtil.getInstance().readFile(file);
		doseResponseExperiment.setLogTransformation(logtransformation);

		Hashtable<String, Integer> probeHash = new Hashtable<>();
		for (ProbeResponse probeResponse : doseResponseExperiment.getProbeResponses())
			probeHash.put(probeResponse.getProbe().getId(), 1);
		FileAnnotation ann = new FileAnnotation();
		ann.setProbesHash(probeHash);
		ann.readArraysInfo();

		ChipInfo chipInfo = ann.getChip(chipID);
		service.assignArrayAnnotations(chipInfo, Arrays.asList(doseResponseExperiment), ann);

		doseResponseExperiment.setName(outputName);
		return doseResponseExperiment;
	}
}
