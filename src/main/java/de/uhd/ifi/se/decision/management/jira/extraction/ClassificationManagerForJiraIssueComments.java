package de.uhd.ifi.se.decision.management.jira.extraction;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;

import de.uhd.ifi.se.decision.management.jira.extraction.impl.CommentSplitterImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DecisionKnowledgeClassifierImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfComment;
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
		List<PartOfComment> sentences = new ArrayList<PartOfComment>();
		List<Comment> comments = getComments(issue);
		for (Comment comment : comments) {
			List<PartOfComment> sentencesOfComment = new CommentSplitterImpl().getSentences(comment);
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

	public List<PartOfComment> classifySentencesBinary(List<PartOfComment> sentences) {
		if (sentences == null) {
			return new ArrayList<PartOfComment>();
		}
		List<String> stringsToBeClassified = getStringsForBinaryClassification(sentences);
		List<Boolean> classificationResult = classifier.makeBinaryPredictions(stringsToBeClassified);
		updateSentencesWithBinaryClassificationResult(classificationResult, sentences);
		return sentences;
	}

	private List<String> getStringsForBinaryClassification(List<PartOfComment> sentences) {
		List<String> stringsToBeClassified = new ArrayList<String>();
		for (PartOfComment sentence : sentences) {
			if (isSentenceQualifiedForBinaryClassification(sentence)) {
				stringsToBeClassified.add(sentence.getDescription());
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
	private static boolean isSentenceQualifiedForBinaryClassification(PartOfComment sentence) {
		return !sentence.isValidated() && sentence.isPlainText();
	}

	private List<PartOfComment> updateSentencesWithBinaryClassificationResult(List<Boolean> classificationResult,
			List<PartOfComment> sentences) {
		if (classificationResult.size() == 0) {
			return sentences;
		}
		int i = 0;
		for (PartOfComment sentence : sentences) {
			if (isSentenceQualifiedForBinaryClassification(sentence)) {
				sentence.setRelevant(classificationResult.get(i));
				sentence.setValidated(false);
				JiraIssueCommentPersistenceManager.updateInDatabase(sentence);
				i++;
			}
		}
		return sentences;
	}

	public List<PartOfComment> classifySentencesFineGrained(List<PartOfComment> sentences) {
		if (sentences == null) {
			return new ArrayList<PartOfComment>();
		}
		List<String> stringsToBeClassified = getStringsForFineGrainedClassification(sentences);
		List<KnowledgeType> classificationResult = this.classifier.makeFineGrainedPredictions(stringsToBeClassified);
		updateSentencesWithFineGrainedClassificationResult(classificationResult, sentences);
		return sentences;
	}

	private List<String> getStringsForFineGrainedClassification(List<PartOfComment> sentences) {
		List<String> stringsToBeClassified = new ArrayList<String>();
		for (PartOfComment sentence : sentences) {
			if (isSentenceQualifiedForFineGrainedClassification(sentence)) {
				stringsToBeClassified.add(sentence.getDescription());
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
	private static boolean isSentenceQualifiedForFineGrainedClassification(PartOfComment sentence) {
		return sentence.isRelevant() && isSentenceQualifiedForBinaryClassification(sentence);
	}

	private List<PartOfComment> updateSentencesWithFineGrainedClassificationResult(List<KnowledgeType> classificationResult,
			List<PartOfComment> sentences) {
		JiraIssueCommentPersistenceManager persistenceManager = new JiraIssueCommentPersistenceManager("");
		int i = 0;
		for (PartOfComment sentence : sentences) {
			if (isSentenceQualifiedForFineGrainedClassification(sentence)) {
				sentence.setType(classificationResult.get(i));
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
