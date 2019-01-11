package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.JiraIssueComment;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class ClassificationManagerForCommentSentences {

	private DecisionKnowledgeClassifier classifier;

	/**
	 * The knowledge types need to be present in the weka classifier. They do not
	 * relate to tags like [Issue].
	 */
	private final String[] knowledgeTypes = { "isAlternative", "isPro", "isCon", "isDecision", "isIssue" };

	public List<JiraIssueComment> classifySentenceBinary(List<JiraIssueComment> commentsList) {
		if (commentsList == null) {
			return new ArrayList<JiraIssueComment>();
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

	public List<JiraIssueComment> classifySentenceFineGrained(List<JiraIssueComment> commentsList) {
		if (commentsList == null) {
			return new ArrayList<JiraIssueComment>();
		}
		Instances data = createDatasetForfineGrainedClassification(commentsList);

		if (data.isEmpty()) {
			return loadSentencesFineGrainedKnowledgeTypesFromActiveObjects(commentsList);
		}
		if (this.classifier == null) {
			this.classifier = new DecisionKnowledgeClassifier();
		}
		List<double[]> classificationResult = this.classifier.makeFineGrainedPredictions(data);

		// Write classification results back to sentence objects
		int i = 0;

		for (JiraIssueComment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (isSentenceQualifiedForFineGrainedClassification(sentence)) {
					sentence.setType(classificationResult.get(i));
					System.out.println(sentence.getTypeAsString());
					sentence.setSummary(null);
					sentence.setValidated(true);
					new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(sentence, null);
					i++;
				} else if (sentence.isRelevant() && sentence.isTaggedFineGrained() && sentence.isPlainText()) {
					Sentence aosentence = (Sentence) new JiraIssueCommentPersistenceManager("")
							.getDecisionKnowledgeElement(sentence.getId());
					sentence.setType(aosentence.getType());
				}
			}
		}

		return commentsList;
	}

	private List<JiraIssueComment> loadSentencesFineGrainedKnowledgeTypesFromActiveObjects(List<JiraIssueComment> commentsList) {
		for (JiraIssueComment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (sentence.isRelevant() && sentence.isTaggedFineGrained()) {
					Sentence aosentence = (Sentence) new JiraIssueCommentPersistenceManager("")
							.getDecisionKnowledgeElement(sentence.getId());
					sentence.setType(aosentence.getType());
				}
			}
		}
		return commentsList;
	}

	private List<JiraIssueComment> matchBinaryClassificationBackOnData(List<Double> classificationResult,
			List<JiraIssueComment> commentsList) {
		int i = 0;
		for (JiraIssueComment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (isSentenceQualifiedForBinaryClassification(sentence)) {
					sentence.setRelevant(classificationResult.get(i));
					JiraIssueCommentPersistenceManager.updateInDatabase(sentence);
					sentence.setValidated(true);
					i++;
				}
			}
		}
		return commentsList;
	}

	public List<JiraIssueComment> writeDataFromActiveObjectsToSentences(List<JiraIssueComment> commentsList) {
		for (JiraIssueComment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				Sentence aoSentence = (Sentence) new JiraIssueCommentPersistenceManager("")
						.getDecisionKnowledgeElement(sentence.getId());
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

	private Instances createDatasetForBinaryClassification(List<JiraIssueComment> commentsList) {
		ArrayList<Attribute> wekaAttributes = createBinaryAttributes();
		Instances data = new Instances("sentences", wekaAttributes, 1000000);

		data.setClassIndex(data.numAttributes() - 1);
		for (JiraIssueComment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (isSentenceQualifiedForBinaryClassification(sentence)) {
					data.add(createInstance(2, wekaAttributes, sentence));
				}
			}
		}
		return data;
	}

	private Instances createDatasetForfineGrainedClassification(List<JiraIssueComment> commentsList) {
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

		for (JiraIssueComment comment : commentsList) {
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
	 *            qualified if it's plain text, and not yet tagged.
	 * @return boolean identifier
	 */
	private static boolean isSentenceQualifiedForBinaryClassification(Sentence sentence) {
		return !sentence.isValidated() && sentence.isPlainText();
	}

	private static boolean isSentenceQualifiedForFineGrainedClassification(Sentence sentence) {
		return sentence.isRelevant() && !sentence.isTaggedFineGrained() && sentence.isPlainText();
	}

	public DecisionKnowledgeClassifier getClassifier() {
		if (this.classifier == null) {
			this.classifier = new DecisionKnowledgeClassifier();
		}
		return this.classifier;
	}

}
