package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.JiraIssueComment;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;

/**
 * Class to classify the text in JIRA issue comments as either irrelevant in
 * terms of decision knowledge (binary classification) or - if relevant - into
 * decision knowledge elements with certain knowledge types (fine grained
 * classification).
 */
public class ClassificationManagerForJiraIssueComments {

	private DecisionKnowledgeClassifier classifier;

	public ClassificationManagerForJiraIssueComments() {
		this.classifier = new DecisionKnowledgeClassifierImpl();
	}

	public List<JiraIssueComment> classifySentenceBinary(List<JiraIssueComment> commentsList) {
		if (commentsList == null) {
			return new ArrayList<JiraIssueComment>();
		}
		List<String> stringsToBeClassified = getStringsForBinaryClassification(commentsList);

		List<Boolean> classificationResult;
		if (!stringsToBeClassified.isEmpty()) {
			classificationResult = classifier.makeBinaryPredictions(stringsToBeClassified);
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
		List<String> stringsToBeClassified = getStringsForFineGrainedClassification(commentsList);

		if (stringsToBeClassified.isEmpty()) {
			return loadSentencesFineGrainedKnowledgeTypesFromActiveObjects(commentsList);
		}
		List<KnowledgeType> classificationResult = this.classifier.makeFineGrainedPredictions(stringsToBeClassified);

		// Write classification results back to sentence objects
		int i = 0;

		for (JiraIssueComment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (isSentenceQualifiedForFineGrainedClassification(sentence)) {
					sentence.setType(classificationResult.get(i));
					System.out.println(sentence.getTypeAsString());
					sentence.setSummary(null);
					sentence.setValidated(false);
					new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(sentence, null);
					i++;
				} else if (sentence.isRelevant() && sentence.isTaggedFineGrained() && sentence.isPlainText()) {
					sentence = (Sentence) new JiraIssueCommentPersistenceManager("")
							.getDecisionKnowledgeElement(sentence.getId());
					sentence.setValidated(false);
				}
			}
		}

		return commentsList;
	}

	private List<JiraIssueComment> loadSentencesFineGrainedKnowledgeTypesFromActiveObjects(
			List<JiraIssueComment> commentsList) {
		for (JiraIssueComment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (sentence.isRelevant() && sentence.isTaggedFineGrained()) {
					sentence = (Sentence) new JiraIssueCommentPersistenceManager("")
							.getDecisionKnowledgeElement(sentence.getId());
				}
			}
		}
		return commentsList;
	}

	private List<JiraIssueComment> matchBinaryClassificationBackOnData(List<Boolean> classificationResult,
			List<JiraIssueComment> commentsList) {
		int i = 0;
		for (JiraIssueComment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (isSentenceQualifiedForBinaryClassification(sentence)) {
					sentence.setRelevant(classificationResult.get(i));
					sentence.setValidated(false);
					JiraIssueCommentPersistenceManager.updateInDatabase(sentence);
					i++;
				}
			}
		}
		return commentsList;
	}

	public List<JiraIssueComment> writeDataFromActiveObjectsToSentences(List<JiraIssueComment> commentsList) {
		for (JiraIssueComment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				sentence = (Sentence) new JiraIssueCommentPersistenceManager("")
						.getDecisionKnowledgeElement(sentence.getId());
			}
		}
		return commentsList;
	}

	private List<String> getStringsForBinaryClassification(List<JiraIssueComment> commentsList) {
		List<String> stringsToBeClassified = new ArrayList<String>();
		for (JiraIssueComment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (isSentenceQualifiedForBinaryClassification(sentence)) {
					stringsToBeClassified.add(sentence.getBody());
				}
			}
		}
		return stringsToBeClassified;
	}
	
	/**
	 * Determines whether a part of JIRA issue comment (substring) should be the
	 * input for binary classification. It is qualified if it's plain text, and if
	 * its type is not yet validated.
	 * 
	 * @param sentence
	 *            part of JIRA issue comment (substring) to check if qualified for
	 *            binary classification.
	 * @return true if the part of JIRA issue comment (substring) should be the
	 *         input for binary classification.
	 */
	private static boolean isSentenceQualifiedForBinaryClassification(Sentence sentence) {
		return !sentence.isValidated() && sentence.isPlainText();
	}

	private List<String> getStringsForFineGrainedClassification(List<JiraIssueComment> commentsList) {
		List<String> stringsToBeClassified = new ArrayList<String>();
		for (JiraIssueComment comment : commentsList) {
			for (Sentence sentence : comment.getSentences()) {
				if (isSentenceQualifiedForFineGrainedClassification(sentence)) {
					stringsToBeClassified.add(sentence.getBody());
				}
			}
		}
		return stringsToBeClassified;
	}

	/**
	 * Determines whether a part of JIRA issue comment (substring) should be the
	 * input for fine grained classification. It is qualified if it's plain text, if
	 * its type is not yet validated and if it's classified as relevant decision
	 * knowledge by the binary classifier.
	 * 
	 * @param sentence
	 *            part of JIRA issue comment (substring) to check if qualified for
	 *            fine grained classification.
	 * @return true if the part of JIRA issue comment (substring) should be the
	 *         input for fine grained classification.
	 */
	private static boolean isSentenceQualifiedForFineGrainedClassification(Sentence sentence) {
		return sentence.isRelevant() && isSentenceQualifiedForBinaryClassification(sentence);
	}

	public DecisionKnowledgeClassifier getClassifier() {
		if (this.classifier == null) {
			this.classifier = new DecisionKnowledgeClassifierImpl();
		}
		return this.classifier;
	}
}
