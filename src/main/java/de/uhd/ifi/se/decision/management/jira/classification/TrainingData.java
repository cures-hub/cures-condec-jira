package de.uhd.ifi.se.decision.management.jira.classification;

import static java.lang.Math.round;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class TrainingData {
	public String[] sentences;
	public String[] relevantSentences;
	public int[] labelsIsRelevant;
	public int[] labelsKnowledgeType;

	public TrainingData(Instances trainingData) {
		sentences = new String[trainingData.size()];
		labelsIsRelevant = new int[trainingData.size()];
		fillFromWekaInstances(trainingData);
	}

	public void fillFromWekaInstances(Instances trainingData) {
		Attribute sentenceAttr = trainingData.attribute("sentence");
		List<String> relevantSentencesList = new ArrayList<>();
		List<Integer> labelsKnowledgeTypeList = new ArrayList<>();
		double[] indecesWithRelevantSentences = new double[trainingData.size()];
		for (int i = 0; i < trainingData.size(); i++) {
			Instance currInstance = trainingData.get(i);
			// last attribute is the sentence that needs to be classified
			sentences[i] = currInstance.stringValue(sentenceAttr);

			int isRelevant = 0;
			// Integer fineGrainedLabel = -1;
			// iterate over the binary attributes for each possible class
			for (int j = 0; j < currInstance.numAttributes() - 1; j++) {
				if (round(currInstance.value(j)) == 1) {
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
