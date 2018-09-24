package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import meka.classifiers.multilabel.LC;
import weka.core.Instance;

public class FineGrainedClassifierMock extends LC{
	
	private int size;

	public FineGrainedClassifierMock(int size) {
		this.size = size;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1610038094819950132L;
	
	@Override
	public double[] distributionForInstance(Instance i) {
		double[] array = new double[size];
		array[size -1 ]= 1.0;
		return array;
		
	}

}
