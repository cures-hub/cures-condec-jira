package de.uhd.ifi.se.decision.documentation.jira.persistence;

import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.Link;

/**
 * @author Ewald Rode
 * @description Interface to create, edit, delete and retrieve decision
 *              knowledge elements and their links
 */
public interface IPersistenceStrategy {

	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement, ApplicationUser user);

	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement, ApplicationUser user);

	public boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement, ApplicationUser user);

	public DecisionKnowledgeElement getDecisionKnowledgeElement(String key);
	
	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements(String projectKey);

	public List<DecisionKnowledgeElement> getDecisions(String projectKey);

	public List<DecisionKnowledgeElement> getChildren(DecisionKnowledgeElement decisionKnowledgeElement);

	public List<DecisionKnowledgeElement> getParents(DecisionKnowledgeElement decisionKnowledgeElement);

	public List<DecisionKnowledgeElement> getUnlinkedDecisionComponents(long id, String projectKey);

	public long insertLink(Link link, ApplicationUser user);

	public void deleteLink(Link link, ApplicationUser user);

	public List<Link> getInwardLinks(DecisionKnowledgeElement element);

	public List<Link> getOutwardLinks(DecisionKnowledgeElement element);
}
