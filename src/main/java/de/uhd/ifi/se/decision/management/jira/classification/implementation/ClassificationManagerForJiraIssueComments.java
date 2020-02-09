package de.uhd.ifi.se.decision.management.jira.classification.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.TextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.TextSplitterImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;

/**
 * Class to classify the text in JIRA issue comments as either irrelevant in
 * terms of decision knowledge (binary classification) or - if relevant - into
 * decision knowledge elements with certain knowledge types (fine grained
 * classification).
 */
public class ClassificationManagerForJiraIssueComments {

	private OnlineFileTrainerImpl classifierTrainer;

	public ClassificationManagerForJiraIssueComments() {
		this.classifierTrainer = new OnlineFileTrainerImpl();
	}

	public void classifyAllCommentsOfJiraIssue(Issue issue) {
		if (issue == null) {
			return;
		}
		this.classifyComments(getComments(issue));
	}

	public void classifyDescription(IssueEvent issueEvent) {
		if (issueEvent == null) {
			return;
		}
		MutableIssue issue = (MutableIssue) issueEvent.getIssue();
		TextSplitter splitter = new TextSplitterImpl();
		// Convert Description-String to a List of PartOfJiraIssueText
		List<PartOfJiraIssueText> partsOfDescription = splitter
				.getPartsOfText(issue.getDescription(), issue.getProjectObject().getKey()).stream()
				// The PartOfJiraIssueTextImpl constructor with signature (PartOfText, Issue)
				// creates a Part of Description by setting the ID to 0
				.map(part -> new PartOfJiraIssueTextImpl(part, issue)).collect(Collectors.toList());

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager
				.getOrCreate(issueEvent.getProject().getKey()).getJiraIssueTextManager();

		List<PartOfJiraIssueText> partsOfDescriptionWithIdInDatabase = new ArrayList<>();
		for (PartOfJiraIssueText p : partsOfDescription) {
			partsOfDescriptionWithIdInDatabase
					.add((PartOfJiraIssueText) persistenceManager.insertDecisionKnowledgeElement(p, p.getCreator()));
		}

		classifySentencesBinary(partsOfDescriptionWithIdInDatabase);
		classifySentencesFineGrained(partsOfDescriptionWithIdInDatabase);
	}

	public void classifyComments(List<Comment> comments) {
		if (comments == null || comments.isEmpty()) {
			return;
		}
		List<PartOfJiraIssueText> sentences = new ArrayList<PartOfJiraIssueText>();

		for (Comment comment : comments) {
			List<PartOfJiraIssueText> sentencesOfComment = JiraIssueTextPersistenceManager.getPartsOfComment(comment);
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

	public List<PartOfJiraIssueText> classifySentencesBinary(List<PartOfJiraIssueText> sentences) {
		if (sentences == null) {
			return new ArrayList<PartOfJiraIssueText>();
		}
		List<PartOfJiraIssueText> sentencesRelevantForBinaryClf = getSentencesForBinaryClassification(sentences);
		List<String> stringsToBeClassified = extractStringsFromPoji(sentencesRelevantForBinaryClf);
		List<Boolean> classificationResult = classifierTrainer.getClassifier()
				.makeBinaryPredictions(stringsToBeClassified);
		updateSentencesWithBinaryClassificationResult(classificationResult, sentences);
		return sentences;
	}

	private List<PartOfJiraIssueText> getSentencesForBinaryClassification(List<PartOfJiraIssueText> sentences) {
		List<PartOfJiraIssueText> stringsToBeClassified = new ArrayList<PartOfJiraIssueText>();
		for (PartOfJiraIssueText sentence : sentences) {
			if (isSentenceQualifiedForBinaryClassification(sentence)) {
				stringsToBeClassified.add(sentence);
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
	private static boolean isSentenceQualifiedForBinaryClassification(PartOfJiraIssueText sentence) {
		return !sentence.isValidated() && sentence.isPlainText();
	}

	private List<PartOfJiraIssueText> updateSentencesWithBinaryClassificationResult(List<Boolean> classificationResult,
			List<PartOfJiraIssueText> sentences) {
		if (classificationResult.size() == 0) {
			return sentences;
		}
		int i = 0;
		for (PartOfJiraIssueText sentence : sentences) {
			if (isSentenceQualifiedForBinaryClassification(sentence)) {
				sentence.setRelevant(classificationResult.get(i));
				sentence.setValidated(false);
				JiraIssueTextPersistenceManager.updateInDatabase(sentence);
				i++;
			}
		}
		return sentences;
	}

	public List<PartOfJiraIssueText> classifySentencesFineGrained(List<PartOfJiraIssueText> sentences) {
		if (sentences == null) {
			return new ArrayList<PartOfJiraIssueText>();
		}
		// make getStringsForFineGrainedClassification return List<PartOfJiraIssueText>
		// add method to extract text
		List<PartOfJiraIssueText> sentencesToBeClassified = getSentencesForFineGrainedClassification(sentences);
		List<String> stringsToBeClassified = extractStringsFromPoji(sentencesToBeClassified);
		List<KnowledgeType> classificationResult = this.classifierTrainer.getClassifier()
				.makeFineGrainedPredictions(stringsToBeClassified);
		updateSentencesWithFineGrainedClassificationResult(classificationResult, sentencesToBeClassified);
		return sentences;
	}

	public static List<String> extractStringsFromPoji(List<PartOfJiraIssueText> sentences) {
		List<String> extractedStringsFromPoji = new ArrayList<String>();
		for (PartOfJiraIssueText sentence : sentences) {
			extractedStringsFromPoji.add(sentence.getDescription());
		}
		return extractedStringsFromPoji;
	}

	private List<PartOfJiraIssueText> getSentencesForFineGrainedClassification(List<PartOfJiraIssueText> sentences) {
		List<PartOfJiraIssueText> stringsToBeClassified = new ArrayList<PartOfJiraIssueText>();
		for (PartOfJiraIssueText sentence : sentences) {
			if (isSentenceQualifiedForFineGrainedClassification(sentence)) {
				stringsToBeClassified.add(sentence);
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
	private static boolean isSentenceQualifiedForFineGrainedClassification(PartOfJiraIssueText sentence) {
		return sentence.isRelevant() && isSentenceQualifiedForBinaryClassification(sentence);
	}

	private List<PartOfJiraIssueText> updateSentencesWithFineGrainedClassificationResult(
			List<KnowledgeType> classificationResult, List<PartOfJiraIssueText> sentences) {
		JiraIssueTextPersistenceManager persistenceManager = new JiraIssueTextPersistenceManager("");
		int i = 0;
		for (PartOfJiraIssueText sentence : sentences) {
			if (isSentenceQualifiedForFineGrainedClassification(sentence)) {
				System.out.println(sentence);
				sentence.setType(classificationResult.get(i));
				System.out.println(sentence.getTypeAsString());
				// sentence.setSummary(null);
				sentence.setValidated(false);
				persistenceManager.updateDecisionKnowledgeElement(sentence, null);
				i++;
			}
		}
		return sentences;
	}

	public OnlineFileTrainerImpl getClassifierTrainer() {
		return this.classifierTrainer;
	}

	public void classifyComment(Comment comment) {
		this.classifyComments(Collections.singletonList(comment));
	}
}
