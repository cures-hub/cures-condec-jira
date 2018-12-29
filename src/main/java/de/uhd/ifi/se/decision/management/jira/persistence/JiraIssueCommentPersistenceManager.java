package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.DecXtractEventListener;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.CommentImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.model.util.CommentSplitter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeInCommentEntity;
import net.java.ao.Query;

/**
 * Extends the abstract class AbstractPersistenceManager. Uses JIRA issue
 * comments to store decision knowledge.
 *
 * @see AbstractPersistenceManager
 */
public class JiraIssueCommentPersistenceManager extends AbstractPersistenceManager {

	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	public JiraIssueCommentPersistenceManager(String projectKey) {
		this.projectKey = projectKey;
		this.documentationLocation = DocumentationLocation.JIRAISSUECOMMENT;
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(long id, ApplicationUser user) {
		boolean isDeleted = false;
		for (DecisionKnowledgeInCommentEntity databaseEntry : ACTIVE_OBJECTS
				.find(DecisionKnowledgeInCommentEntity.class, Query.select().where("ID = ?", id))) {
			GenericLinkManager.deleteLinksForElement("s" + id);
			isDeleted = DecisionKnowledgeInCommentEntity.deleteElement(databaseEntry);
		}
		return isDeleted;
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(long id) {
		for (DecisionKnowledgeInCommentEntity databaseEntry : ACTIVE_OBJECTS
				.find(DecisionKnowledgeInCommentEntity.class, Query.select().where("ID = ?", id))) {
			return new SentenceImpl(databaseEntry);

		}
		return null;
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(DecisionKnowledgeElement element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Link> getInwardLinks(DecisionKnowledgeElement element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user, DecisionKnowledgeElement parentElement) {
		long issueId;
		if (parentElement.getDocumentationLocation() == DocumentationLocation.JIRAISSUECOMMENT) {
			Sentence sentence = (Sentence) this.getDecisionKnowledgeElement(parentElement.getId());
			issueId = sentence.getIssueId();
		} else {
			issueId = parentElement.getId();
		}
		MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject(issueId);
		if (issue == null) {
			return null;
		}
		String macro = getMacro(element);
		String text = macro + element.getSummary() + "\n" + element.getDescription() + macro;
		com.atlassian.jira.issue.comments.Comment comment = ComponentAccessor.getCommentManager().create(issue, user,
				text, false);
		Comment com = new CommentImpl(comment, true);
		for (Sentence sentence : com.getSentences()) {
			GenericLinkManager.deleteLinksForElement("s" + sentence.getId());
		}
		return com.getSentences().get(0);
	}

	private static String getMacro(DecisionKnowledgeElement element) {
		KnowledgeType knowledgeType = element.getType();
		String macro = "{" + knowledgeType.toString() + "}";
		return macro;
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		DecXtractEventListener.editCommentLock = true;

		if (element.getSummary() != null) {
			// Get corresponding element from ao database
			Sentence databaseEntity = (Sentence) this.getDecisionKnowledgeElement(element.getId());
			int newSentenceEnd = databaseEntity.getEndSubstringCount();
			int newSentenceStart = databaseEntity.getStartSubstringCount();
			String newSentenceBody = element.getDescription();

			if ((newSentenceEnd - newSentenceStart) != newSentenceBody.length()) {
				// Get JIRA Comment instance - Casting fails in unittesting with Mock
				CommentManager commentManager = ComponentAccessor.getCommentManager();
				MutableComment mutableComment = (MutableComment) commentManager
						.getCommentById(databaseEntity.getCommentId());

				if (mutableComment.getBody().length() >= databaseEntity.getEndSubstringCount()) {
					String oldSentenceInComment = mutableComment.getBody().substring(newSentenceStart, newSentenceEnd);
					int indexOfOldSentence = mutableComment.getBody().indexOf(oldSentenceInComment);

					String newType = element.getType().toString();
					String tag = "";
					// Allow changing of manual tags, but no tags for icons
					if (databaseEntity.isTagged() && !CommentSplitter.isCommentIconTagged(oldSentenceInComment)) {
						tag = "{" + WordUtils.capitalize(newType) + "}";
					} else if (CommentSplitter.isCommentIconTagged(oldSentenceInComment)) {
						indexOfOldSentence = indexOfOldSentence + 3; // add icon to text.
					}
					String first = mutableComment.getBody().substring(0, indexOfOldSentence);
					String second = tag + newSentenceBody + tag;
					String third = mutableComment.getBody()
							.substring(indexOfOldSentence + oldSentenceInComment.length());

					mutableComment.setBody(first + second + third);
					commentManager.update(mutableComment, true);
					updateSentenceBodyWhenCommentChanged(databaseEntity.getCommentId(), element.getId(), second);

				}
			}
		}
		boolean isUpdated = updateKnowledgeTypeOfSentence(element.getId(), element.getType());
		DecXtractEventListener.editCommentLock = false;
		return isUpdated;
	}

	public static boolean updateSentenceBodyWhenCommentChanged(long commentId, long aoId, String description) {
		return ACTIVE_OBJECTS.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				int lengthDifference = 0;
				int oldStart = 0;
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ACTIVE_OBJECTS
						.find(DecisionKnowledgeInCommentEntity.class, "ID = ?", aoId)) {
					int oldLength = sentenceEntity.getEndSubstringCount() - sentenceEntity.getStartSubstringCount();
					lengthDifference = (oldLength - description.length()) * -1;
					sentenceEntity.setEndSubstringCount(sentenceEntity.getEndSubstringCount() + lengthDifference);
					sentenceEntity.save();
					oldStart = sentenceEntity.getStartSubstringCount();
				}
				updateSentenceLengthForOtherSentencesInSameComment(commentId, oldStart, lengthDifference, aoId);
				return true;
			}
		});
	}

	public static void updateSentenceLengthForOtherSentencesInSameComment(long commentId, int oldStart,
			int lengthDifference, long aoId) {
		for (DecisionKnowledgeInCommentEntity otherSentenceInComment : ACTIVE_OBJECTS
				.find(DecisionKnowledgeInCommentEntity.class, "COMMENT_ID = ?", commentId)) {
			if (otherSentenceInComment.getStartSubstringCount() > oldStart && otherSentenceInComment.getId() != aoId
					&& otherSentenceInComment.getCommentId() == commentId) {
				otherSentenceInComment
						.setStartSubstringCount(otherSentenceInComment.getStartSubstringCount() + lengthDifference);
				otherSentenceInComment
						.setEndSubstringCount(otherSentenceInComment.getEndSubstringCount() + lengthDifference);
				otherSentenceInComment.save();
			}
		}
	}

	public static Boolean updateKnowledgeTypeOfSentence(long id, KnowledgeType knowledgeType) {
		return ACTIVE_OBJECTS.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ACTIVE_OBJECTS
						.find(DecisionKnowledgeInCommentEntity.class)) {
					if (sentenceEntity.getId() == id) {

						String argument = "";
						if (knowledgeType == KnowledgeType.PRO || knowledgeType == KnowledgeType.CON) {
							argument = knowledgeType.toString();
						}
						// Knowledgetype is an Argument

						String oldKnowledgeType = sentenceEntity.getType();
						if (knowledgeType.equals(KnowledgeType.OTHER) || knowledgeType.equals(KnowledgeType.ARGUMENT)) {
							sentenceEntity.setType(argument);
						} else {
							sentenceEntity.setType(knowledgeType.toString());
						}
						sentenceEntity.setRelevant(true);
						// TODO used to be setTaggedFinegrained
						sentenceEntity.setTagged(true);
						if (!sentenceEntity.getType().equals("Pro") && !sentenceEntity.getType().equals("Con")) {
							if (knowledgeType.equals(KnowledgeType.OTHER)) {
								sentenceEntity.setRelevant(false);
							}
						}
						// TODO used to be if (sentenceEntity.isTaggedManually())
						if (sentenceEntity.isTagged()) {
							int oldTextLength = getTextLengthOfAoElement(sentenceEntity);
							int newTextLength = updateTagsInComment(sentenceEntity, knowledgeType, argument,
									oldKnowledgeType);
							sentenceEntity
									.setEndSubstringCount(sentenceEntity.getStartSubstringCount() + newTextLength);
							updateSentenceLengthForOtherSentencesInSameComment(sentenceEntity.getCommentId(),
									sentenceEntity.getStartSubstringCount(), newTextLength - oldTextLength,
									sentenceEntity.getId());
							sentenceEntity.save();
						} else {
							sentenceEntity.setTagged(true);
							int newLength = addTagsToCommentWhenAutoClassified(sentenceEntity);
							sentenceEntity.setEndSubstringCount(sentenceEntity.getEndSubstringCount() + newLength);
							updateSentenceLengthForOtherSentencesInSameComment(sentenceEntity.getCommentId(),
									sentenceEntity.getStartSubstringCount(), newLength, sentenceEntity.getId());
							sentenceEntity.save();
						}
						sentenceEntity.save();
						return true;
					}
				}
				return false;
			}
		});
	}

	private static int getTextLengthOfAoElement(DecisionKnowledgeInCommentEntity sentence) {
		return sentence.getEndSubstringCount() - sentence.getStartSubstringCount();
	}

	private static int updateTagsInComment(DecisionKnowledgeInCommentEntity sentenceEntity, KnowledgeType knowledgeType,
			String argument, String oldKnowledgeType) {
		CommentManager cm = ComponentAccessor.getCommentManager();
		MutableComment mc = (MutableComment) cm.getMutableComment(sentenceEntity.getCommentId());
		String oldBody = mc.getBody();

		String newBody = oldBody.substring(sentenceEntity.getStartSubstringCount(),
				sentenceEntity.getEndSubstringCount());
		if (knowledgeType.toString().equalsIgnoreCase("other")
				|| knowledgeType.toString().equalsIgnoreCase("argument")) {
			newBody = newBody.replaceAll("(?i)" + oldKnowledgeType + "}", argument + "}");
		} else {
			newBody = newBody.replaceAll("(?i)" + oldKnowledgeType + "}", knowledgeType.toString() + "}");
		}
		// build body with first text and changed text
		int newEndSubstringCount = newBody.length();
		newBody = oldBody.substring(0, sentenceEntity.getStartSubstringCount()) + newBody;
		// If Changed sentence is in the middle of a sentence
		if (oldBody.length() > sentenceEntity.getEndSubstringCount()) {
			newBody = newBody + oldBody.substring(sentenceEntity.getEndSubstringCount());
		}

		mc.setBody(newBody);
		cm.update(mc, true);
		return newEndSubstringCount;
	}

	public static int addTagsToCommentWhenAutoClassified(DecisionKnowledgeInCommentEntity sentence) {
		CommentManager cm = ComponentAccessor.getCommentManager();
		MutableComment mc = (MutableComment) cm.getMutableComment(sentence.getCommentId());
		String newBody = mc.getBody().substring(sentence.getStartSubstringCount(), sentence.getEndSubstringCount());

		newBody = "{" + sentence.getType() + "}" + newBody + "{" + sentence.getType() + "}";
		int lengthDiff = (sentence.getType().length() + 2) * 2;

		DecXtractEventListener.editCommentLock = true;
		mc.setBody(mc.getBody().substring(0, sentence.getStartSubstringCount()) + newBody
				+ mc.getBody().substring(sentence.getEndSubstringCount()));
		cm.update(mc, true);
		DecXtractEventListener.editCommentLock = false;
		return lengthDiff;
	}

}
