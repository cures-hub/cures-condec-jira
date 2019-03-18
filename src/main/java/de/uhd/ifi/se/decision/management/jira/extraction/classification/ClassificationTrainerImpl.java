package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import meka.classifiers.multilabel.LC;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.*;

import java.util.ArrayList;
import java.util.List;

public class ClassificationTrainerImpl implements ClassificationTrainer {


	private static LC binaryRelevance;
	private static FilteredClassifier fc;
	private static String pathToBinaryModel;
	private static String pathToFineGrainedModel;
	private static Instances structure;
	private List<Sentence> mekaTrainData;

	public ClassificationTrainerImpl(String projectKey){
		JiraIssueCommentPersistenceManager manager = new JiraIssueCommentPersistenceManager(projectKey);
		mekaTrainData = manager.getListOfUserValidatedSentneces(projectKey);
	}


	@Override
	public void train() {
		try {
			//structure = buildDatasetForMeka(mekaTrainData);
			LC fineGrainedClassifier = new LC();
			fineGrainedClassifier.buildClassifier(structure);
			System.out.println(fineGrainedClassifier.getModel());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Instances buildDatasetForMeka(List<Sentence> trainSentences){

		ArrayList<Attribute> wekaAttributes = new ArrayList<Attribute>();

		// Declare Class value with {0,1} as possible values
		wekaAttributes.add(getAttribute("isAlternative"));
		wekaAttributes.add(getAttribute("isPro"));
		wekaAttributes.add(getAttribute("isCon"));
		wekaAttributes.add(getAttribute("isDecision"));
		wekaAttributes.add(getAttribute("isIssue"));

		// Declare text attribute to hold the message (free form text)
		Attribute attributeText = new Attribute("sentence", (List<String>) null);

		// Declare the feature vector
		wekaAttributes.add(attributeText);

		Instances data = new Instances("sentences -C 5 ", wekaAttributes, 1000000);

		for (Sentence trainSentence : trainSentences) {
			data.add(createTrainData(trainSentence,attributeText));
		}
		structure =data;
		return data;
	}

	private DenseInstance createTrainData(Sentence sentence,Attribute attributeText){
		DenseInstance newInstance = new DenseInstance(6);
		newInstance.setValue(0,0);
		newInstance.setValue(1,0);
		newInstance.setValue(2,0);
		newInstance.setValue(3,0);
		newInstance.setValue(4,0);
		if(sentence.getType() == KnowledgeType.ALTERNATIVE){
			newInstance.setValue(0,1);
		}
		if(sentence.getType() == KnowledgeType.PRO){
			newInstance.setValue(1,1);
		}
		if(sentence.getType() == KnowledgeType.CON){
			newInstance.setValue(2,1);
		}
		if(sentence.getType() == KnowledgeType.DECISION){
			newInstance.setValue(3,1);
		}
		if(sentence.getType() == KnowledgeType.ISSUE){
			newInstance.setValue(4,1);
		}
		newInstance.setValue(attributeText, sentence.getTextFromComment());
		return newInstance;
	}

	private static Attribute getAttribute(String name) {
		ArrayList<String> rationaleAttribute = new ArrayList<String>();
		rationaleAttribute.add("0");
		rationaleAttribute.add("1");
		return new Attribute(name, rationaleAttribute);
	}
}
