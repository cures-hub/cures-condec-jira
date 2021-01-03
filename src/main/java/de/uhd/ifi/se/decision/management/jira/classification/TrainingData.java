package de.uhd.ifi.se.decision.management.jira.classification;

import static java.lang.Math.round;

import weka.core.Instance;
import weka.core.Instances;

public class TrainingData {
	public String[] sentences;
	public String[] relevantSentences;
	public int[] labelsIsRelevant;
	public int[] labelsKnowledgeType;

	public TrainingData(Instances trainingData) {
		sentences = new String[trainingData.size()];
		relevantSentences = new String[trainingData.size()];
		labelsIsRelevant = new int[trainingData.size()];
		labelsKnowledgeType = new int[trainingData.size()];
		fillFromWekaInstances(trainingData);
	}

	public void fillFromWekaInstances(Instances trainingData) {
		int z = 0;
		for (int i = 0; i < trainingData.size(); i++) {
			Instance currInstance = trainingData.get(i);
			// last attribute is the sentence that needs to be classified
			sentences[i] = currInstance.stringValue(currInstance.numAttributes() - 1);

			int isRelevant = 0;
			// Integer fineGrainedLabel = -1;
			// iterate over the binary attributes for each possible class
			for (int j = 0; j < currInstance.numAttributes() - 1; j++) {
				if (round(currInstance.value(j)) == 1) {
					isRelevant = 1;
					labelsKnowledgeType[z] = j;
					relevantSentences[z] = sentences[i];
					z++;
				}
			}
			labelsIsRelevant[i] = isRelevant;
		}
	}
}
