package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import java.util.Random;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;

public class BinaryClassifierMock extends FilteredClassifier {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8580149633853925051L;

	@Override
	public double classifyInstance(Instance i) {
		Random r = new Random();
		double randomValue =  r.nextDouble();
		return randomValue;
		
	}

}
