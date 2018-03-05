package de.uhd.ifi.se.decision.documentation.jira.persistence;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.Link;

/**
 * @description Abstract Class to create, edit, delete and retrieve decision
 *              knowledge elements and their links
 */
public abstract class PersistenceStrategy {

	public abstract DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement, ApplicationUser user);

	public abstract boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement, ApplicationUser user);

	public abstract boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement, ApplicationUser user);

	public abstract DecisionKnowledgeElement getDecisionKnowledgeElement(String key);
	
	public abstract List<DecisionKnowledgeElement> getDecisionKnowledgeElements(String projectKey);

	public List<DecisionKnowledgeElement> getDecisions(String projectKey) {
		List<DecisionKnowledgeElement> decisionKnowledgeElements = this.getDecisionKnowledgeElements(projectKey);
		List<DecisionKnowledgeElement> decisions = new ArrayList<DecisionKnowledgeElement>();
		for (DecisionKnowledgeElement decisionKnowledgeElement : decisionKnowledgeElements) {
			if (decisionKnowledgeElement.getType().equals("Decision")) {
				decisions.add(decisionKnowledgeElement);
			}
		}
		return decisions;
	}

	public abstract List<DecisionKnowledgeElement> getChildren(DecisionKnowledgeElement decisionKnowledgeElement);

	public abstract List<DecisionKnowledgeElement> getParents(DecisionKnowledgeElement decisionKnowledgeElement);

	public abstract List<DecisionKnowledgeElement> getUnlinkedDecisionComponents(long id, String projectKey);

	public abstract long insertLink(Link link, ApplicationUser user);

	public abstract void deleteLink(Link link, ApplicationUser user);

	public abstract List<Link> getInwardLinks(DecisionKnowledgeElement element);

	public abstract List<Link> getOutwardLinks(DecisionKnowledgeElement element);
}
