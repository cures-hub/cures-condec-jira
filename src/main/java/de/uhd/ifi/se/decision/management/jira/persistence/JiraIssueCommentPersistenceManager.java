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
		String tag = getTag(element);
		String text = tag + element.getSummary() + "\n" + element.getDescription() + tag;
		com.atlassian.jira.issue.comments.Comment comment = ComponentAccessor.getCommentManager().create(issue, user,
				text, false);
		Comment com = new CommentImpl(comment, true);
		for (Sentence sentence : com.getSentences()) {
			GenericLinkManager.deleteLinksForElement("s" + sentence.getId());
		}
		return com.getSentences().get(0);
	}

	private static String getTag(DecisionKnowledgeElement element) {
		return "{" + element.getType().toString() + "}";
	}

	private static DecisionKnowledgeInCommentEntity setParameters(DecisionKnowledgeElement element,
			DecisionKnowledgeInCommentEntity databaseEntry) {
		// String summary = element.getSummary();
		// if (summary != null) {
		// databaseEntry.setSummary(summary);
		// }
		// String description = element.getDescription();
		// if (description != null) {
		// databaseEntry.setSummary(description);
		// }
		// databaseEntry.setType(element.getType().replaceProAndConWithArgument().toString());
		return databaseEntry;
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		DecXtractEventListener.editCommentLock = true;
		boolean isUpdated = updateKnowledgeTypeOfSentence(element.getId(), element.getType());

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
		DecXtractEventListener.editCommentLock = false;
		return isUpdated;
	}

	public static boolean updateKnowledgeTypeOfSentence(long id, KnowledgeType knowledgeType) {
		return ACTIVE_OBJECTS.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ACTIVE_OBJECTS
						.find(DecisionKnowledgeInCommentEntity.class)) {
					if (databaseEntry.getId() == id) {
						String oldKnowledgeType = databaseEntry.getType();
						databaseEntry.setType(knowledgeType.toString());
						databaseEntry.setRelevant(true);
						databaseEntry.setTagged(true);
						int oldTextLength = getTextLengthOfAoElement(databaseEntry);
						int newTextLength = updateTagsInComment(databaseEntry, knowledgeType, knowledgeType.toString(),
								oldKnowledgeType);
						databaseEntry.setEndSubstringCount(databaseEntry.getStartSubstringCount() + newTextLength);
						updateSentenceLengthForOtherSentencesInSameComment(databaseEntry.getCommentId(),
								databaseEntry.getStartSubstringCount(), newTextLength - oldTextLength,
								databaseEntry.getId());
						databaseEntry.save();
						return true;
					}
				}
				return false;
			}
		});
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

	public static int addTagsToCommentWhenAutoClassified(DecisionKnowledgeInCommentEntity sentenceEntity) {
		Sentence sentence = new SentenceImpl(sentenceEntity);
		MutableComment mutableComment = sentence.getComment();
		String newBody = mutableComment.getBody().substring(sentenceEntity.getStartSubstringCount(),
				sentenceEntity.getEndSubstringCount());

		newBody = "{" + sentenceEntity.getType() + "}" + newBody + "{" + sentenceEntity.getType() + "}";
		int lengthDiff = (sentenceEntity.getType().length() + 2) * 2;

		DecXtractEventListener.editCommentLock = true;
		mutableComment.setBody(mutableComment.getBody().substring(0, sentenceEntity.getStartSubstringCount()) + newBody
				+ mutableComment.getBody().substring(sentenceEntity.getEndSubstringCount()));
		ComponentAccessor.getCommentManager().update(mutableComment, true);
		DecXtractEventListener.editCommentLock = false;
		return lengthDiff;
	}

}
