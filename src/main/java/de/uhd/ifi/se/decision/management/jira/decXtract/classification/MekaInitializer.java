package de.uhd.ifi.se.decision.management.jira.decXtract.classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.decXtract.model.Comment;
import de.uhd.ifi.se.decision.management.jira.decXtract.model.Rationale;
import de.uhd.ifi.se.decision.management.jira.decXtract.model.Sentence;
import meka.classifiers.multilabel.LC;
import meka.core.MLUtils;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.functions.SGD;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Debug.Random;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class MekaInitializer {

	public static Instances readDataFromARFF(String path) {
		BufferedReader reader = null;
		Instances structure = null;
		try {
			reader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			System.err.println("File: " + path + " not found");
			return null;
		}
		try {
			structure = new Instances(reader);
		} catch (IOException e) {
			System.err.println("File: " + path + " not readable");
			return null;
		}
		return structure;
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

	private static Attribute getAttribute(String name) {
		ArrayList<String> rationaleAttribute = new ArrayList<String>();
		rationaleAttribute.add("0");
		rationaleAttribute.add("1");
		return new Attribute(name, rationaleAttribute);
	}

	private static Instances buildDataset(List<Comment> commentsList) {
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

		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (sentence.isRelevant()) {
					DenseInstance newInstance = new DenseInstance(6);
					// To avoid misunderstandings: For unknown reason, if you watch into newInstance
					// here, the string is replaced by a number. If you watch it later when
					// predicting classes, it's shown correctly
					newInstance.setValue(attributeText, sentence.getBody());
					data.add(newInstance);
				}
			}
		}
		return data;
	}

	public static void doSth(List<Comment> commentsList) throws Exception {
		Instances structure = buildDataset(commentsList);

		structure.setClassIndex(5);
		// MLUtils.prepareData(structure);

		LC binaryRelevance = null;

		String path = ComponentGetter.getUrlOfClassifierFolder() + "br.model";
		InputStream is = new URL(path).openStream();
		binaryRelevance = (LC) weka.core.SerializationHelper.read(is);


		// Classify string instances
		List<double[]> results = new ArrayList<double[]>();
		for (int n = 0; n < structure.size(); n++) {
			Instance test = structure.get(n);
			results.add(binaryRelevance.distributionForInstance(test));

		}
		// Write classification results back to sentence objects
		int i = 0;
		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (sentence.isRelevant()) {
					sentence.setClassification(Rationale.transferRationaleList(results.get(i)));
					i++;

				}
			}
		}

	}

	private static void train() {
		/*
		 * binaryRelevance = new LC(); FilteredClassifier fc = new FilteredClassifier();
		 * fc.setFilter(getSTWV()); fc.setClassifier(new NaiveBayesMultinomial());
		 * binaryRelevance.setClassifier(fc);
		 *
		 *
		 * Evaluation rate = new Evaluation(structure); Random seed = new Random(1);
		 * Instances datarandom = new Instances(structure); datarandom.randomize(seed);
		 *
		 * int folds = 10; datarandom.stratify(folds);
		 * rate.crossValidateModel(binaryRelevance, structure, folds, seed);
		 *
		 * binaryRelevance.buildClassifier(structure);
		 * weka.core.SerializationHelper.write("./br.model", binaryRelevance);
		 *
		 * System.out.println(rate.toSummaryString());
		 * System.out.println("Structure num classes: "+structure.numClasses());
		 *
		 * for(int i = 0; i < structure.numClasses(); i++)
		 * {System.out.println(rate.fMeasure(i));}
		 */
	}

}
