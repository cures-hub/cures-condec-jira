package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class WekaInitializer {

	private static FilteredClassifier fc;

	public static List<Comment> classifySentencesBinary(List<Comment> commentsList) throws Exception { 
		init();

		Instances data = createDataset(commentsList);
		//if data is empty, all instances have already been classified and are stored in AO
		if (!data.isEmpty()) {
			return makePredictions(data, commentsList);
		} else {
			return writeDataFromActiveObjectsToSentences(commentsList);
		}
	}

	private static List<Comment> makePredictions(Instances data, List<Comment> commentsList) {
		List<Double> areRelevant = new ArrayList<Double>();

		try {
			for (int i = 0; i < data.numInstances(); i++) {
				data.get(i).setClassMissing();
				Double n = fc.classifyInstance(data.get(i));
				areRelevant.add(n);
			}
		} catch (Exception e) {
			System.err.println("Binary Classification failed");
			return commentsList;
		}
		// Match classification back on data
		int i = 0;
		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (isSentenceQualifiedForBinaryClassification(sentence)) {
					sentence.setRelevant(areRelevant.get(i));
					ActiveObjectsManager.setIsRelevantIntoAo(sentence.getActiveObjectId(), sentence.isRelevant());
					sentence.isTagged(true);
					i++;
				}
			}
		}
		return commentsList;
	}

	private static List<Comment> writeDataFromActiveObjectsToSentences(List<Comment> commentsList) {
		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				sentence.setRelevant(
						ActiveObjectsManager.getElementFromAO(sentence.getActiveObjectId()).getIsRelevant());
			}
		}
		return commentsList;
	}

	private static ArrayList<String> createClassAttribute() {
		// Declare Class value with {0,1} as possible values
		ArrayList<String> relevantAttribute = new ArrayList<String>();
		relevantAttribute.add("0");
		relevantAttribute.add("1");
		return relevantAttribute;
	}

	private static ArrayList<Attribute> createWekaAttributes() {
		ArrayList<Attribute> wekaAttributes = new ArrayList<Attribute>();

		// Declare text attribute to hold the message (free form text)
		wekaAttributes.add(new Attribute("sentence", (List<String>) null));
		wekaAttributes.add(new Attribute("isRelevant", createClassAttribute()));

		return wekaAttributes;
	}

	private static DenseInstance createInstance(int size, ArrayList<Attribute> wekaAttributes, Sentence sentence) {
		DenseInstance newInstance = new DenseInstance(size);
		newInstance.setValue(wekaAttributes.get(0), sentence.getBody());
		return newInstance;
	}

	private static Instances createDataset(List<Comment> commentsList) {
		ArrayList<Attribute> wekaAttributes = createWekaAttributes();
		Instances data = new Instances("sentences", wekaAttributes, 1000000);

		data.setClassIndex(data.numAttributes() - 1);
		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (isSentenceQualifiedForBinaryClassification(sentence)) {
					data.add(createInstance(2, wekaAttributes, sentence));
				}
			}
		}
		return data;
	}

	public static void init() throws Exception {
		fc = new FilteredClassifier();
		String path = ComponentGetter.getUrlOfClassifierFolder() + "fc.model";
		InputStream is = new URL(path).openStream();
		fc = (FilteredClassifier) weka.core.SerializationHelper.read(is);
	}
	
	
	/**
	 * @param sentence Sentence to check if its qualified for classification. It is qualified if its plain text, and not yet tagged.
	 * @return boolean identifier 
	 */
	private static boolean isSentenceQualifiedForBinaryClassification(Sentence sentence) {
		return !sentence.isTagged() && sentence.isPlanText();
	}

}
