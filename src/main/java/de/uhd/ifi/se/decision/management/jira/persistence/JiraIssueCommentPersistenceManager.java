package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.DecXtractEventListener;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Extends the abstract class AbstractPersistenceManager. Uses JIRA issue
 * comments to store decision knowledge.
 *
 * @see AbstractPersistenceManager
 */
public class JiraIssueCommentPersistenceManager extends AbstractPersistenceManager {

	// private String projectKey;

	public JiraIssueCommentPersistenceManager(String projectKey) {
		// this.projectKey = projectKey;
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
	public boolean deleteLink(Link link, ApplicationUser user) {
		// TODO Auto-generated method stub
		return false;
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
			ApplicationUser user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long insertLink(Link link, ApplicationUser user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean changeKnowledgeType(DecisionKnowledgeElement element, ApplicationUser user) {
		DecXtractEventListener.editCommentLock = true;
		boolean isUpdated = ActiveObjectsManager.updateKnowledgeTypeOfSentence(element.getId(), element.getType());
		DecXtractEventListener.editCommentLock = false;
		return isUpdated;
	}
}
