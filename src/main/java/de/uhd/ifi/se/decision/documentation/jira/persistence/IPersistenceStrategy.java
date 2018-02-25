package de.uhd.ifi.se.decision.documentation.jira.persistence;

import java.util.List;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.Link;
import de.uhd.ifi.se.decision.documentation.jira.view.treants.Treant;
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Core;
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Data;

/**
 * @author Ewald Rode
 * @description Interface to create, edit, delete and retrieve decision
 *              knowledge elements and their links
 */
public interface IPersistenceStrategy {
	public Data createDecisionComponent(DecisionKnowledgeElement decisionKnowledgeElement, ApplicationUser user);

	public Data editDecisionComponent(DecisionKnowledgeElement decisionKnowledgeElement, ApplicationUser user);

	public boolean deleteDecisionComponent(DecisionKnowledgeElement decisionKnowledgeElement, ApplicationUser user);

	public Long createLink(Link link, ApplicationUser user);	

	public List<DecisionKnowledgeElement> getDecisionsInProject(Project project);

	public List<DecisionKnowledgeElement> getUnlinkedDecisionComponents(long id, String projectKey);

	public Core createCore(Project project);

	public Treant createTreant(String issueKey, int depth);
}
