package de.uhd.ifi.se.decision.management.jira.extraction.classification;



import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import meka.classifiers.multilabel.LC;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.*;
import weka.core.Debug.Random;
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

	public ClassificationTrainerImpl(Map<KnowledgeType, String> trainMap) throws Exception {

		binaryRelevance = new LC();
		FilteredClassifier fc = new FilteredClassifier();
		pathToBinaryModel = ComponentGetter.getUrlOfClassifierFolder() + "fc.model";
		pathToFineGrainedModel = ComponentGetter.getUrlOfClassifierFolder() + "br.model";

		List commentsList = (List) trainMap.values();
		structure = new Instances(buildDataset(commentsList));
	}


	@Override
	public void train() {

		binaryRelevance = new LC();
		fc = new FilteredClassifier();
		try {
			fc.setFilter(getSTWV());
		} catch (Exception e) {
			e.printStackTrace();
		}
		fc.setClassifier(new NaiveBayesMultinomial());
		binaryRelevance.setClassifier(fc);

		try {
			Evaluation rate = new Evaluation(structure);
			Random seed = new Random(1);
			Instances datarandom = new Instances(structure);
			datarandom.randomize(seed);

			int folds = 10; datarandom.stratify(folds);
			rate.crossValidateModel(binaryRelevance, structure, folds, seed);
			binaryRelevance.buildClassifier(structure);
			SerializationHelper.write(pathToFineGrainedModel, binaryRelevance);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private static Instances buildDataset(List<String> commentsList) throws Exception {
		ArrayList<Attribute> wekaAttributes = new ArrayList<Attribute>();

		// Declare Class value with {0,1} as possible values
		wekaAttributes.add(getAttribute("isIssue"));
		wekaAttributes.add(getAttribute("isDecision"));
		wekaAttributes.add(getAttribute("isAlternative"));
		wekaAttributes.add(getAttribute("isPro"));
		wekaAttributes.add(getAttribute("isCon"));

		// Declare text attribute to hold the message (free form text)
		Attribute attributeText = new Attribute("sentence", (List<String>) null);

		// Declare the feature vector
		wekaAttributes.add(attributeText);

		Instances data = new Instances("sentences -C 5 ", wekaAttributes, 1000000);

		for (String comment : commentsList) {


			DenseInstance newInstance = new DenseInstance(6);
			// To avoid misunderstandings: For unknown reason, if you watch into newInstance
			// here, the string is replaced by a number. If you watch it later when
			// predicting classes, it's shown correctly
			newInstance.setValue(attributeText, comment);
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
