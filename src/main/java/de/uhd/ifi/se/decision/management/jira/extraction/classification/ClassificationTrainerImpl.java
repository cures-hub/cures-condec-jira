package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;

import meka.classifiers.multilabel.LC;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.*;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClassificationTrainerImpl implements ClassificationTrainer {

	private static String projectKey;
	private static Instances structure;
	private List<Sentence> mekaTrainData;

	public ClassificationTrainerImpl(String projectKey){
		this.projectKey = projectKey;
		JiraIssueCommentPersistenceManager manager = new JiraIssueCommentPersistenceManager(projectKey);
		mekaTrainData = manager.getListOfUserValidatedSentneces(projectKey);
	}


	@Override
	public void train() {
		try {
			LC binaryRelevance = new LC();
			FilteredClassifier fc = new FilteredClassifier();
			fc.setFilter(getSTWV());
			fc.setClassifier(new NaiveBayesMultinomial());
			binaryRelevance.setClassifier(fc);

			evaluatTraining(binaryRelevance);

			binaryRelevance.buildClassifier(structure);
			weka.core.SerializationHelper.write(System.getProperty("user.home")+"/newBr.model", binaryRelevance);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void evaluatTraining(LC binaryRelevance) throws Exception {
		Evaluation rate = new Evaluation(structure);
		Random seed = new Random(1);
		Instances datarandom = new Instances(structure); datarandom.randomize(seed);

		int folds = 10; datarandom.stratify(folds);
		rate.crossValidateModel(binaryRelevance, structure, folds, seed);
		System.out.println(rate.toSummaryString());
		System.out.println("Structure num classes: "+structure.numClasses());
	}

	private static Tokenizer getTokenizer() throws Exception {
		Tokenizer t = new NGramTokenizer();
		String[] options = weka.core.Utils.splitOptions(
				"weka.core.tokenizers.NGramTokenizer -max 3 -min 1 -delimiters \" \\r\\n\\t.,;:\\'\\\"()?!\"");
		t.setOptions(options);
		return t;
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
		data.setClassIndex(data.numAttributes() -1);
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
