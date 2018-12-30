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

	private static DecisionKnowledgeInCommentEntity setParameters(Sentence element,
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

		MutableComment mutableComment = sentence.getComment();		

		String body = "";
		if (element.getSummary() == null) {
			// only knowledge type is changed
			body = mutableComment.getBody().substring(sentence.getStartSubstringCount(),
					sentence.getEndSubstringCount());
			body = body.replaceAll("(?i)" + sentence.getType().toString() + "}", element.getType().toString() + "}");
		} else {
			// description and maybe knowledge type are changed
			String tag = AbstractKnowledgeClassificationMacro.getTag(element.getType());
			body = tag + element.getDescription() + tag;
		}

		sentence.setType(element.getType());

		String firstPartOfComment = mutableComment.getBody().substring(0, sentence.getStartSubstringCount());
		String lastPartOfComment = mutableComment.getBody().substring(sentence.getEndSubstringCount());
		mutableComment.setBody(firstPartOfComment + body + lastPartOfComment);
		ComponentAccessor.getCommentManager().update(mutableComment, true);

		int lengthDifference = body.length() - sentence.getLength();
		updateSentenceLengthForOtherSentencesInSameComment(sentence, lengthDifference);

		sentence.setEndSubstringCount(sentence.getStartSubstringCount() + body.length());

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

	public static void updateSentenceLengthForOtherSentencesInSameComment(Sentence sentence, int lengthDifference) {
		for (DecisionKnowledgeInCommentEntity otherSentenceInComment : ACTIVE_OBJECTS
				.find(DecisionKnowledgeInCommentEntity.class, "COMMENT_ID = ?", sentence.getCommentId())) {
			if (otherSentenceInComment.getStartSubstringCount() > sentence.getStartSubstringCount()
					&& otherSentenceInComment.getId() != sentence.getId()) {
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
