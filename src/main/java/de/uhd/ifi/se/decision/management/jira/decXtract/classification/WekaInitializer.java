package de.uhd.ifi.se.decision.management.jira.decXtract.classification;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decXtract.model.Comment;
import de.uhd.ifi.se.decision.management.jira.decXtract.model.Sentence;
import meka.classifiers.multilabel.LC;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class WekaInitializer {

	private static FilteredClassifier fc;

	private static LC binaryRelevance = null;

	public static List<Comment> predict(List<Comment> commentsList) {

		List<Double> areRelevant = new ArrayList<Double>();

		Instances data;
		try {

			data = tryToReadFromStrings(commentsList);
			for (int i = 0; i < data.numInstances(); i++) {
				data.get(i).setClassMissing();
				Double n = fc.classifyInstance(data.get(i));
				areRelevant.add(n);
			}
		} catch (Exception e) {
			System.err.println("Classification failed");
		}
		// Match classification back on data
		int i = 0;
		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				sentence.setRelevant(areRelevant.get(i));
				i++;
			}
		}
		return commentsList;
	}

	private static Instances tryToReadFromStrings(List<Comment> commentsList) {
		ArrayList<Attribute> wekaAttributes = new ArrayList<Attribute>();

		// Declare text attribute to hold the message (free form text)
		Attribute attributeText = new Attribute("sentence", (List<String>) null);

		// Declare the feature vector
		wekaAttributes = new ArrayList<Attribute>();
		wekaAttributes.add(attributeText);

		// Declare Class value with {0,1} as possible values
		ArrayList<String> relevantValues = new ArrayList<String>();
		relevantValues.add("0");
		relevantValues.add("1");
		Attribute isRelevant = new Attribute("isRelevant", relevantValues);
		wekaAttributes.add(isRelevant);

		Instances data = new Instances("sentences", wekaAttributes, 1000000);

		data.setClassIndex(data.numAttributes() - 1);

		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				DenseInstance newInstance = new DenseInstance(2);
				newInstance.setValue(wekaAttributes.get(0), sentence.getBody());
				data.add(newInstance);
			}
		}
		return data;
	}

	public static void init(List<Comment> commentsList) throws Exception {
		// System.out.println("bin hier: "+System.getProperty("user.dir"));
		fc = new FilteredClassifier();
		fc = (FilteredClassifier) weka.core.SerializationHelper.read("./fc.model");

		binaryRelevance = (LC) weka.core.SerializationHelper.read("./br.model");


	}

}
