package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.DecXtractEventListener;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.util.CommentSplitter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Extends the abstract class AbstractPersistenceManager. Uses JIRA issue
 * comments to store decision knowledge.
 *
 * @see AbstractPersistenceManager
 */
public class JiraIssueCommentPersistenceManager extends AbstractPersistenceManager {

	public JiraIssueCommentPersistenceManager(String projectKey) {
		this.projectKey = projectKey;
		this.documentationLocation = DocumentationLocation.JIRAISSUECOMMENT;
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		return ActiveObjectsManager.deleteSentenceObject(element.getId());
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(long id, ApplicationUser user) {
		return ActiveObjectsManager.deleteSentenceObject(id);
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(long id) {
		return ActiveObjectsManager.getElementFromAO(id);
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
		if (parentElement.getDocumentationLocation() == DocumentationLocation.JIRAISSUECOMMENT) {
			Sentence sentence = (Sentence) ActiveObjectsManager.getElementFromAO(parentElement.getId());
			element.setId(sentence.getIssueId());
		} else {
			element.setId(parentElement.getId());
		}
		return ActiveObjectsManager.addNewCommentToJIRAIssue(element, user);
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		DecXtractEventListener.editCommentLock = true;

		if (element.getSummary() != null) {
			// Get corresponding element from ao database
			Sentence databaseEntity = (Sentence) ActiveObjectsManager.getElementFromAO(element.getId());
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
					if (databaseEntity.isTagged()
							&& !CommentSplitter.isCommentIconTagged(oldSentenceInComment)) {
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
					ActiveObjectsManager.updateSentenceBodyWhenCommentChanged(databaseEntity.getCommentId(),
							element.getId(), second);

				}
			}
		}
		boolean isUpdated = ActiveObjectsManager.updateKnowledgeTypeOfSentence(element.getId(), element.getType());
		DecXtractEventListener.editCommentLock = false;
		return isUpdated;
	}
}
