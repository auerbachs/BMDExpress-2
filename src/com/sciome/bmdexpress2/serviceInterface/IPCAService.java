package com.sciome.bmdexpress2.serviceInterface;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.pca.PCAResults;

public interface IPCAService {
	public PCAResults calculatePCA(DoseResponseExperiment doseResponseExperiment);
}
