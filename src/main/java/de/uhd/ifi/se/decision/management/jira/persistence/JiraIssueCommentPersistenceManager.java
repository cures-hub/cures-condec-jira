package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
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
import de.uhd.ifi.se.decision.management.jira.extraction.view.macros.AbstractKnowledgeClassificationMacro;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
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
		String tag = AbstractKnowledgeClassificationMacro.getTag(element.getTypeAsString());
		String text = tag + element.getSummary() + "\n" + element.getDescription() + tag;
		com.atlassian.jira.issue.comments.Comment comment = ComponentAccessor.getCommentManager().create(issue, user,
				text, false);
		Comment com = new CommentImpl(comment, true);
		for (Sentence sentence : com.getSentences()) {
			GenericLinkManager.deleteLinksForElement("s" + sentence.getId());
		}
		return com.getSentences().get(0);
	}

	private static DecisionKnowledgeInCommentEntity setParameters(final Sentence element,
			DecisionKnowledgeInCommentEntity databaseEntry) {
		databaseEntry.setType(element.getTypeAsString());
		databaseEntry.setRelevant(true);
		databaseEntry.setTagged(true);
		databaseEntry.setEndSubstringCount(element.getEndSubstringCount());
		return databaseEntry;
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		DecXtractEventListener.editCommentLock = true;

		// Get corresponding element from database
		Sentence sentence = (Sentence) this.getDecisionKnowledgeElement(element.getId());
		if (sentence == null) {
			return false;
		}

		int oldTextLength = sentence.getLength();
		sentence.updateTagsInComment(element.getType());
		sentence.setType(element.getType());
		
		int newTextLength = sentence.getLength();

		updateSentenceLengthForOtherSentencesInSameComment(sentence.getCommentId(), sentence.getStartSubstringCount(),
				newTextLength - oldTextLength, sentence.getId());

		int oldEndPosition = sentence.getEndSubstringCount();
		int oldStartPosition = sentence.getStartSubstringCount();

		if (element.getSummary() != null) {
			sentence.setSummary(element.getSummary());
			sentence.setDescription(element.getDescription());

			String newBody = sentence.getDescription();

			if (oldTextLength != newBody.length()) {
				MutableComment mutableComment = sentence.getComment();

				if (mutableComment.getBody().length() >= sentence.getEndSubstringCount()) {
					String oldSentenceInComment = mutableComment.getBody().substring(oldStartPosition, oldEndPosition);
					int indexOfOldSentence = mutableComment.getBody().indexOf(oldSentenceInComment);

					String tag = "";
					// Allow changing of manual tags, but no tags for icons
					if (sentence.isTagged() && !CommentSplitter.isCommentIconTagged(oldSentenceInComment)) {
						tag = AbstractKnowledgeClassificationMacro.getTag(sentence.getTypeAsString());
					} else if (CommentSplitter.isCommentIconTagged(oldSentenceInComment)) {
						indexOfOldSentence = indexOfOldSentence + 3; // add icon to text.
					}
					String first = mutableComment.getBody().substring(0, indexOfOldSentence);
					String second = tag + newBody + tag;
					String third = mutableComment.getBody()
							.substring(indexOfOldSentence + oldSentenceInComment.length());

					mutableComment.setBody(first + second + third);
					ComponentAccessor.getCommentManager().update(mutableComment, true);
					updateSentenceBodyWhenCommentChanged(sentence, second);

					// int lengthDifference = (oldElement.getLength() - second.length()) * -1;
					// element.setEndSubstringCount(element.getEndSubstringCount() +
					// lengthDifference);
				}
			}
		}

		DecisionKnowledgeInCommentEntity databaseEntry = ACTIVE_OBJECTS
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						for (DecisionKnowledgeInCommentEntity databaseEntry : ACTIVE_OBJECTS
								.find(DecisionKnowledgeInCommentEntity.class)) {
							if (databaseEntry.getId() == element.getId()) {
								databaseEntry = setParameters(sentence, databaseEntry);
								databaseEntry.save();
								return databaseEntry;
							}
						}
						return null;
					}
				});

		DecXtractEventListener.editCommentLock = false;
		return databaseEntry != null;
	}

	public static boolean updateSentenceBodyWhenCommentChanged(Sentence element, String description) {
		return ACTIVE_OBJECTS.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ACTIVE_OBJECTS
						.find(DecisionKnowledgeInCommentEntity.class, "ID = ?", element.getId())) {
					int lengthDifference = (element.getLength() - description.length()) * -1;
					sentenceEntity.setEndSubstringCount(sentenceEntity.getEndSubstringCount() + lengthDifference);
					int oldStart = sentenceEntity.getStartSubstringCount();
					sentenceEntity.save();
					updateSentenceLengthForOtherSentencesInSameComment(element.getCommentId(), oldStart,
							lengthDifference, element.getId());
				}
				return true;
			}
		});
	}

	public static void updateSentenceLengthForOtherSentencesInSameComment(long commentId, int oldStart,
			int lengthDifference, long elementId) {
		for (DecisionKnowledgeInCommentEntity otherSentenceInComment : ACTIVE_OBJECTS
				.find(DecisionKnowledgeInCommentEntity.class, "COMMENT_ID = ?", commentId)) {
			if (otherSentenceInComment.getStartSubstringCount() > oldStart
					&& otherSentenceInComment.getId() != elementId
					&& otherSentenceInComment.getCommentId() == commentId) {
				otherSentenceInComment
						.setStartSubstringCount(otherSentenceInComment.getStartSubstringCount() + lengthDifference);
				otherSentenceInComment
						.setEndSubstringCount(otherSentenceInComment.getEndSubstringCount() + lengthDifference);
				otherSentenceInComment.save();
			}
		}
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
