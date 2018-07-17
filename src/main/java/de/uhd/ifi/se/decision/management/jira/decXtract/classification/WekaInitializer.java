package de.uhd.ifi.se.decision.management.jira.decXtract.classification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.h2.store.Data;
import org.radeox.util.logging.SystemOutLogger;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import de.uhd.ifi.se.decision.management.jira.decXtract.model.Comment;
import de.uhd.ifi.se.decision.management.jira.decXtract.model.Sentence;
import sun.rmi.runtime.NewThreadAction;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.Debug.Random;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class WekaInitializer {

	private static FilteredClassifier fc;

	public static ArrayList<Comment> predict(ArrayList<Comment> commentsList) {

		ArrayList<Double> areRelevant = new ArrayList<Double>();

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

	private static Instances tryToReadFromStrings(ArrayList<Comment> commentsList) {
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

	public static void init(ArrayList<Comment> commentsList) throws Exception {
		// System.out.println("bin hier: "+System.getProperty("user.dir"));
		fc = new FilteredClassifier();
		fc = (FilteredClassifier) weka.core.SerializationHelper.read("./fc.model");
	}

}
