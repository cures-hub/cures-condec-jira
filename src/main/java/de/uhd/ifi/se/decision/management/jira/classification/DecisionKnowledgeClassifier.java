package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;

/**
 * Tries to identify decision knowledge in natural language texts using a binary
 * and fine grained supervised classifier.
 */
public class DecisionKnowledgeClassifier {
	private static final Logger LOGGER = LoggerFactory.getLogger(DecisionKnowledgeClassifier.class);

	private BinaryClassifier binaryClassifier;
	private FineGrainedClassifier fineGrainedClassifier;

	/**
	 * @issue What is the best place to store the supervised text classifier related
	 *        data?
	 * @decision Store the data for the text classifier in
	 *           JiraHome/data/condec-plugin/classifier!
	 * @pro Similar as for the git repositories.
	 */
	public static String CLASSIFIER_DIRECTORY = ComponentGetter.PLUGIN_HOME + "classifier" + File.separator;

	public static DecisionKnowledgeClassifier instance;

	private DecisionKnowledgeClassifier() {
		FileManager.copyDefaultTrainingDataToClassifierDirectory();
		Preprocessor.copyDefaultPreprocessingDataToFile();
		binaryClassifier = new BinaryClassifier();
		fineGrainedClassifier = new FineGrainedClassifier(5);
	}

	public static DecisionKnowledgeClassifier getInstance() {
		if (instance == null) {
			instance = new DecisionKnowledgeClassifier();
		}
		return instance;
	}

	/**
	 * @param values
	 *            array of prediction results.
	 * @return mode, i.e. the most frequent value in the array.
	 */
	public static int mode(int[] values) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int value : values) {
			Integer count = map.get(value);
			map.put(value, count != null ? count + 1 : 1);
		}
		int popular = Collections.max(map.entrySet(), new Comparator<Map.Entry<Integer, Integer>>() {
			@Override
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		}).getKey();
		return popular;
	}

	public BinaryClassifier getBinaryClassifier() {
		return binaryClassifier;
	}

	public FineGrainedClassifier getFineGrainedClassifier() {
		return fineGrainedClassifier;
	}

	/**
	 * @return whether or not the classifier is currently training.
	 */
	public boolean isTraining() {
		return fineGrainedClassifier.isCurrentlyTraining() || binaryClassifier.isCurrentlyTraining();
	}

	/**
	 * @return whether or not the classifier was trained.
	 */
	public boolean isTrained() {
		return binaryClassifier.isModelTrained() && fineGrainedClassifier.isModelTrained();
	}
}
