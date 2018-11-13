package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class ClassificationManagerForCommentSentences {

	private DecisionKnowledgeClassifier classifier;
	// TODO: Update knowledge types if classifer changes
	/** The knowledge types need to be present in the weka classifer. They do not relate to tags like [Issue]  */
	private final String[] knowledgeTypes = { "isAlternative", "isPro", "isCon", "isDecision", "isIssue" };

	public List<Comment> classifySentenceBinary(List<Comment> commentsList) {
		if(commentsList == null) {
			return new ArrayList<Comment>();
		}
		Instances data = createDatasetForBinaryClassification(commentsList);

		List<Double> classificationResult;
		if (!data.isEmpty()) {
			if (this.classifier == null) {
				this.classifier = new DecisionKnowledgeClassifier();
			}
			classificationResult = classifier.makeBinaryPredictions(data);
			commentsList = matchBinaryClassificationBackOnData(classificationResult, commentsList);
		} else {
			commentsList = writeDataFromActiveObjectsToSentences(commentsList);
		}

		return commentsList;
	}

	public List<Comment> classifySentenceFineGrained(List<Comment> commentsList) {
		if(commentsList == null) {
			return new ArrayList<Comment>();
		}
		Instances data = createDatasetForfineGrainedClassification(commentsList);

		if (data.isEmpty()) {
			return loadSentencesFineGrainedKnowledgeTypesFromActiveObjects(commentsList);
		}
		if (this.classifier == null) {
			this.classifier = new DecisionKnowledgeClassifier();
		}
		List<double[]> classificationResult = this.classifier.classifySentencesFineGrained(data);

		// Write classification results back to sentence objects
		int i = 0;

		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (isSentenceQualifiedForFineGrainedClassification(sentence)) {
					sentence.setKnowledgeType(classificationResult.get(i));
					ActiveObjectsManager.setSentenceKnowledgeType(sentence);

					sentence.setTaggedFineGrained(true);
					i++;
				} else if (sentence.isRelevant() && sentence.isTaggedFineGrained() && sentence.isPlainText()) {
					Sentence aosentence = (Sentence) ActiveObjectsManager.getElementFromAO(sentence.getId());
					sentence.setKnowledgeTypeString(aosentence.getKnowledgeTypeString());
				}
			}
		}

		return commentsList;
	}

	private List<Comment> loadSentencesFineGrainedKnowledgeTypesFromActiveObjects(List<Comment> commentsList) {
		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (sentence.isRelevant() && sentence.isTaggedFineGrained()) {
					Sentence aosentence = (Sentence) ActiveObjectsManager.getElementFromAO(sentence.getId());
					sentence.setKnowledgeTypeString(aosentence.getKnowledgeTypeString());
				}
			}
		}
		return commentsList;
	}

	private List<Comment> matchBinaryClassificationBackOnData(List<Double> classificationResult,
			List<Comment> commentsList) {
		int i = 0;
		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (isSentenceQualifiedForBinaryClassification(sentence)) {
					sentence.setRelevant(classificationResult.get(i));
					ActiveObjectsManager.setIsRelevantIntoAo(sentence.getId(), sentence.isRelevant());
					sentence.setTagged(true);
					i++;
				}
			}
		}
		return commentsList;
	}

	public List<Comment> writeDataFromActiveObjectsToSentences(List<Comment> commentsList) {
		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				Sentence aoSentence = (Sentence) ActiveObjectsManager.getElementFromAO(sentence.getId());
				sentence.setRelevant(aoSentence.isRelevant());
			}
		}
		return commentsList;
	}

	private ArrayList<String> createClassAttributeList() {
		// Declare Class value with {0,1} as possible values
		ArrayList<String> relevantAttribute = new ArrayList<String>();
		relevantAttribute.add("0");
		relevantAttribute.add("1");
		return relevantAttribute;
	}

	private ArrayList<Attribute> createBinaryAttributes() {
		ArrayList<Attribute> wekaAttributes = new ArrayList<Attribute>();

		wekaAttributes.add(new Attribute("sentence", (List<String>) null));
		wekaAttributes.add(new Attribute("isRelevant", createClassAttributeList()));

		return wekaAttributes;
	}

	private DenseInstance createInstance(int size, ArrayList<Attribute> wekaAttributes, Sentence sentence) {
		DenseInstance newInstance = new DenseInstance(size);
		newInstance.setValue(wekaAttributes.get(0), sentence.getBody());
		return newInstance;
	}

	private Instances createDatasetForBinaryClassification(List<Comment> commentsList) {
		ArrayList<Attribute> wekaAttributes = createBinaryAttributes();
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

	private Instances createDatasetForfineGrainedClassification(List<Comment> commentsList) {
		ArrayList<Attribute> wekaAttributes = new ArrayList<Attribute>();

		// Declare Class value with {0,1} as possible values
		for (int i = 0; i < knowledgeTypes.length; i++) {
			wekaAttributes.add(new Attribute(knowledgeTypes[i], createClassAttributeList(), i));
		}

		// Declare text attribute to hold the message (free form text)
		Attribute attributeText = new Attribute("sentence", (List<String>) null, 5);

		// Declare the feature vector
		wekaAttributes.add(attributeText);
		Instances data = new Instances("sentences: -C 5 ", wekaAttributes, 1000000);

		for (Comment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (isSentenceQualifiedForFineGrainedClassification(sentence)) {
					Instance newInstance = new DenseInstance(6);
					newInstance.setValue(attributeText, sentence.getBody());
					data.add(newInstance);
				}
			}
		}
		return data;
	}

	/**
	 * @param sentence
	 *            Sentence to check if its qualified for classification. It is
	 *            qualified if its plain text, and not yet tagged.
	 * @return boolean identifier
	 */
	private static boolean isSentenceQualifiedForBinaryClassification(Sentence sentence) {
		return !sentence.isTagged() && sentence.isPlainText();
	}

	private static boolean isSentenceQualifiedForFineGrainedClassification(Sentence sentence) {
		return sentence.isRelevant() && !sentence.isTaggedFineGrained() && sentence.isPlainText()
				&& !sentence.isTaggedManually();
	}

	public DecisionKnowledgeClassifier getClassifier() {
		if (this.classifier == null) {
			this.classifier = new DecisionKnowledgeClassifier();
		}
		return this.classifier;
	}

}
