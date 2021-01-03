package de.uhd.ifi.se.decision.management.jira.classification;

import java.util.ArrayList;
import java.util.List;

public class PreprocessedData {

	public List<double[]> preprocessedSentences;
	public int[] updatedLabels;

	public PreprocessedData(int size) {
		preprocessedSentences = new ArrayList<>();
		updatedLabels = new int[size];
	}

}
