package de.uhd.ifi.se.decision.management.jira.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.AutomaticLinkCreator;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.macros.AbstractKnowledgeClassificationMacro;

/**
 * Classifies the sentences (i.e. {@link PartOfJiraIssueText} objects) of Jira
 * issue comments and description as either irrelevant in terms of decision
 * knowledge (binary classification) or - if relevant - into decision knowledge
 * elements with certain knowledge types (fine grained classification).
 */
public class ClassificationManagerForJiraIssueText {

	private JiraIssueTextPersistenceManager persistenceManager;

	public ClassificationManagerForJiraIssueText(String projectKey) {
		persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();
	}

	/**
	 * Splits the description and all comments of the Jira issue into sentences,
	 * i.e. {@link PartOfJiraIssueText} objects and predicts whether each sentence
	 * contains relevant decision knowledge and -if yes - its {@link KnowledgeType}.
	 * Directly overwrites the description and comments with the classification
	 * results, i.e. introduces macro tags
	 * ({@link AbstractKnowledgeClassificationMacro}) for the
	 * {@link KnowledgeType}s.
	 * 
	 * @param issue
	 *            Jira issue.
	 */
	public void classifyDescriptionAndAllComments(Issue issue) {
		classifyDescription(issue);
		classifyComments(getComments(issue));
	}

	/**
	 * Splits the description of the Jira issue into sentences, i.e.
	 * {@link PartOfJiraIssueText} objects and predicts whether each sentence
	 * contains relevant decision knowledge and -if yes - its {@link KnowledgeType}.
	 * Directly overwrites the description with the classification results, i.e.
	 * introduces macro tags ({@link AbstractKnowledgeClassificationMacro}) for the
	 * {@link KnowledgeType}s.
	 * 
	 * @param issue
	 *            Jira issue.
	 */
	public void classifyDescription(Issue issue) {
		List<PartOfJiraIssueText> partsOfDescriptionWithIdInDatabase = persistenceManager
				.updateElementsOfDescriptionInDatabase(issue);
		classifySentencesBinary(partsOfDescriptionWithIdInDatabase);
		classifySentencesFineGrained(partsOfDescriptionWithIdInDatabase);
	}

	public void classifyComments(List<Comment> comments) {
		if (comments == null || comments.isEmpty()) {
			return;
		}
		for (Comment comment : comments) {
			classifyComment(comment);
		}
	}

	public void classifyComment(Comment comment) {
		List<PartOfJiraIssueText> sentences = new ArrayList<>();
		List<PartOfJiraIssueText> sentencesOfComment = persistenceManager
				.updateElementsOfCommentInDatabase(comment);
		sentences.addAll(sentencesOfComment);
		classifySentencesBinary(sentences);
		classifySentencesFineGrained(sentences);
	}

	public static List<Comment> getComments(Issue issue) {
		return ComponentAccessor.getCommentManager().getComments(issue);
	}

	public List<PartOfJiraIssueText> classifySentencesBinary(List<PartOfJiraIssueText> sentences) {
		if (sentences == null) {
			return new ArrayList<PartOfJiraIssueText>();
		}
		List<PartOfJiraIssueText> sentencesRelevantForBinaryClf = getSentencesForBinaryClassification(sentences);
		List<String> stringsToBeClassified = sentencesRelevantForBinaryClf.stream()
				.map(PartOfJiraIssueText::getDescription)
				.collect(Collectors.toList());
		boolean[] classificationResult = TextClassifier.getInstance().getBinaryClassifier()
				.predict(stringsToBeClassified);
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
	 * @param sentence
	 *            {@link PartOfJiraIssueText} to check if qualified for binary
	 *            classification.
	 * @return true if a sentence, i.e. {@link PartOfJiraIssueText} should be the
	 *         input for binary classification. It is qualified if its type is not
	 *         yet validated.
	 */
	private static boolean isSentenceQualifiedForBinaryClassification(PartOfJiraIssueText sentence) {
		return !sentence.isValidated();
	}

	private List<PartOfJiraIssueText> updateSentencesWithBinaryClassificationResult(boolean[] classificationResult,
			List<PartOfJiraIssueText> sentences) {
		if (classificationResult.length == 0) {
			return sentences;
		}
		int i = 0;
		for (PartOfJiraIssueText sentence : sentences) {
			if (isSentenceQualifiedForBinaryClassification(sentence)) {
				sentence.setRelevant(classificationResult[i]);
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
		List<PartOfJiraIssueText> sentencesToBeClassified = getSentencesForFineGrainedClassification(sentences);
		List<String> stringsToBeClassified = sentencesToBeClassified.stream().map(PartOfJiraIssueText::getDescription)
				.collect(Collectors.toList());
		List<KnowledgeType> classificationResult = TextClassifier.getInstance().getFineGrainedClassifier()
				.predict(stringsToBeClassified);
		updateSentencesWithFineGrainedClassificationResult(classificationResult, sentencesToBeClassified);
		return sentences;
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
	 * Determines whether a part of Jira issue comment (substring) should be the
	 * input for fine grained classification. It is qualified if it's plain text, if
	 * its type is not yet validated and if it's classified as relevant decision
	 * knowledge by the binary classifier.
	 *
	 * @param sentence
	 *            part of Jira issue comment (substring) to check if qualified for
	 *            fine grained classification.
	 * @return true if the part of Jira issue comment (substring) should be the
	 *         input for fine grained classification.
	 */
	private static boolean isSentenceQualifiedForFineGrainedClassification(PartOfJiraIssueText sentence) {
		return sentence.isRelevant() && isSentenceQualifiedForBinaryClassification(sentence);
	}

	private List<PartOfJiraIssueText> updateSentencesWithFineGrainedClassificationResult(
			List<KnowledgeType> classificationResult, List<PartOfJiraIssueText> sentences) {
		int i = 0;
		for (PartOfJiraIssueText sentence : sentences) {
			if (isSentenceQualifiedForFineGrainedClassification(sentence)) {
				sentence.setType(classificationResult.get(i));
				sentence.setValidated(false);
				persistenceManager.updateKnowledgeElement(sentence, null);
				AutomaticLinkCreator.createSmartLinkForSentenceIfRelevant(sentence);
				i++;
			}
		}
		return sentences;
	}
}
