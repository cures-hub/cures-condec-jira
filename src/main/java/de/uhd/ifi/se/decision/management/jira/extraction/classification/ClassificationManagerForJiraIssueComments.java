package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;

import de.uhd.ifi.se.decision.management.jira.extraction.CommentSplitter;
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

	public void classifyAllCommentsOfJiraIssue(Issue issue) {
		if (issue == null) {
			return;
		}
		List<Sentence> sentences = new ArrayList<Sentence>();
		List<Comment> comments = getComments(issue);
		for (Comment comment : comments) {
			List<Sentence> sentencesOfComment = new CommentSplitter().getSentences(comment);
			sentences.addAll(sentencesOfComment);
		}
		classifySentencesBinary(sentences);
		classifySentencesFineGrained(sentences);
	}

	public static List<Comment> getComments(Issue issue) {
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		if (issue != null && commentManager.getComments(issue) != null) {
			return commentManager.getComments(issue);
		}
		return new ArrayList<Comment>();
	}

	public List<Sentence> classifySentencesBinary(List<Sentence> sentences) {
		if (sentences == null) {
			return new ArrayList<Sentence>();
		}
		List<String> stringsToBeClassified = getStringsForBinaryClassification(sentences);
		List<Boolean> classificationResult = classifier.makeBinaryPredictions(stringsToBeClassified);
		updateSentencesWithBinaryClassificationResult(classificationResult, sentences);
		return sentences;
	}

	private List<String> getStringsForBinaryClassification(List<Sentence> sentences) {
		List<String> stringsToBeClassified = new ArrayList<String>();
		for (Sentence sentence : sentences) {
			if (isSentenceQualifiedForBinaryClassification(sentence)) {
				stringsToBeClassified.add(sentence.getBody());
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

	private List<Sentence> updateSentencesWithBinaryClassificationResult(List<Boolean> classificationResult,
			List<Sentence> sentences) {
		int i = 0;
		for (Sentence sentence : sentences) {
			if (isSentenceQualifiedForBinaryClassification(sentence)) {
				sentence.setRelevant(classificationResult.get(i));
				sentence.setValidated(false);
				JiraIssueCommentPersistenceManager.updateInDatabase(sentence);
				i++;
			}
		}
		return sentences;
	}

	public List<Sentence> classifySentencesFineGrained(List<Sentence> sentences) {
		if (sentences == null) {
			return new ArrayList<Sentence>();
		}
		List<String> stringsToBeClassified = getStringsForFineGrainedClassification(sentences);
		List<KnowledgeType> classificationResult = this.classifier.makeFineGrainedPredictions(stringsToBeClassified);
		updateSentencesWithFineGrainedClassificationResult(classificationResult, sentences);
		return sentences;
	}

	private List<String> getStringsForFineGrainedClassification(List<Sentence> sentences) {
		List<String> stringsToBeClassified = new ArrayList<String>();
		for (Sentence sentence : sentences) {
			if (isSentenceQualifiedForFineGrainedClassification(sentence)) {
				stringsToBeClassified.add(sentence.getBody());
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

	private List<Sentence> updateSentencesWithFineGrainedClassificationResult(List<KnowledgeType> classificationResult,
			List<Sentence> sentences) {
		JiraIssueCommentPersistenceManager persistenceManager = new JiraIssueCommentPersistenceManager("");
		int i = 0;
		for (Sentence sentence : sentences) {
			if (isSentenceQualifiedForFineGrainedClassification(sentence)) {
				sentence.setType(classificationResult.get(i));
				System.out.println(sentence.getTypeAsString());
				sentence.setSummary(null);
				sentence.setValidated(false);
				persistenceManager.updateDecisionKnowledgeElement(sentence, null);
				i++;
			}
		}
		return sentences;
	}

	public DecisionKnowledgeClassifier getClassifier() {
		if (this.classifier == null) {
			this.classifier = new DecisionKnowledgeClassifierImpl();
		}
		return this.classifier;
	}
}
