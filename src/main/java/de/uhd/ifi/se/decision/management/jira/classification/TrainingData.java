package de.uhd.ifi.se.decision.management.jira.classification;

import java.util.ArrayList;
import java.util.List;

import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.vector.StringVector;

public class TrainingData {
	public String[] sentences;
	public String[] relevantSentences;
	public int[] labelsIsRelevant;
	public int[] labelsKnowledgeType;

	public TrainingData(DataFrame trainingData) {
		StringVector sentencesVec = trainingData.stringVector("sentence");
		sentences = sentencesVec.toArray();
		labelsIsRelevant = new int[trainingData.size()];
		fillFromWekaInstances(trainingData);
	}

	public void fillFromWekaInstances(DataFrame trainingData) {
		List<String> relevantSentencesList = new ArrayList<>();
		List<Integer> labelsKnowledgeTypeList = new ArrayList<>();
		double[] indecesWithRelevantSentences = new double[trainingData.size()];
		for (int i = 0; i < trainingData.size(); i++) {
			Tuple currInstance = trainingData.get(i);

			int isRelevant = 0;
			// iterate over the binary attributes for each possible class
			for (int j = 0; j < currInstance.length() - 1; j++) {
				if (currInstance.getInt(j) == 1) {
					isRelevant = 1;
					indecesWithRelevantSentences[i] = 1;
					relevantSentencesList.add(sentences[i]);
					labelsKnowledgeTypeList.add(j);
				}
			}
			labelsIsRelevant[i] = isRelevant;
		}

		relevantSentences = relevantSentencesList.toArray(new String[relevantSentencesList.size()]);
		labelsKnowledgeType = labelsKnowledgeTypeList.stream().mapToInt(i -> i).toArray();
	}
}
