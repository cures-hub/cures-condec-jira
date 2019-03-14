package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import meka.classifiers.multilabel.LC;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.*;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		structure = buildDatasetForMeka(mekaTrainData);
	}

	private static StringToWordVector getSTWV() throws Exception {
		StringToWordVector stwv = new StringToWordVector();
		stwv.setLowerCaseTokens(true);
		stwv.setIDFTransform(true);
		stwv.setTFTransform(true);
		stwv.setTokenizer(getTokenizer());
		stwv.setWordsToKeep(1000000);
		return stwv;
	}

	private static Tokenizer getTokenizer() throws Exception {
		Tokenizer t = new NGramTokenizer();
		String[] options = weka.core.Utils.splitOptions(
				"weka.core.tokenizers.NGramTokenizer -max 3 -min 1 -delimiters \" \\r\\n\\t.,;:\\'\\\"()?!\"");
		t.setOptions(options);
		return t;
	}

	public static Instances buildDatasetForMeka(List<Sentence> trainSentences){

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

			DenseInstance newInstance = new DenseInstance(6);
			newInstance.setValue(0,0);
			newInstance.setValue(1,0);
			newInstance.setValue(2,0);
			newInstance.setValue(3,0);
			newInstance.setValue(4,1);
			newInstance.setValue(attributeText, trainSentence.getTextFromComment());
			data.add(newInstance);


		}

		return data;
	}

	private static Attribute getAttribute(String name) {
		ArrayList<String> rationaleAttribute = new ArrayList<String>();
		rationaleAttribute.add("0");
		rationaleAttribute.add("1");
		return new Attribute(name, rationaleAttribute);
	}
}
