package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.atlassian.jira.web.component.CreateIssueWebComponent;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.persistance.ActiveObjectsManager;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class WekaInitializer {

	private static FilteredClassifier fc;

	public static List<Comment> predict(List<Comment> commentsList) throws Exception {
		init();

		List<Double> areRelevant = new ArrayList<Double>();

		Instances data;
		try {

			data = retrieveDataFromCommentStrings(commentsList);

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
				if (!sentence.isTagged()) {
					sentence.setRelevant(areRelevant.get(i));
					sentence.isTagged(true);
					i++;
				}
			}
		}
		return commentsList;
	}

	private static ArrayList<Attribute> createWekaAttributes() {
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

		return wekaAttributes;
	}

	private static Instances retrieveDataFromCommentStrings(List<Comment> commentsList) {

		// commentsList = removeDataWhichIsAlreadyDefinedInAo(commentsList);

		ArrayList<Attribute> wekaAttributes = createWekaAttributes();
		Instances data = new Instances("sentences", wekaAttributes, 1000000);

		data.setClassIndex(data.numAttributes() - 1);
		// TODO: use data from active objects
		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (!sentence.isTagged()) {
					DenseInstance newInstance = new DenseInstance(2);
					newInstance.setValue(wekaAttributes.get(0), sentence.getBody());
					data.add(newInstance);
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

}
