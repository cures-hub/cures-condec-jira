package de.uhd.ifi.se.decision.documentation.jira.persistence;

import java.util.List;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.Link;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.SimpleDecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.view.treants.Treant;
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Core;
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Data;

/**
 * @author Ewald Rode
 * @description Interface for different Solutions approaches to creating,
 *              editing, deleting and rendering decisioncomponents and their
 *              links
 */
public interface IPersistenceStrategy {
	public Data createDecisionComponent(DecisionKnowledgeElement dec, ApplicationUser user);

	public Data editDecisionComponent(DecisionKnowledgeElement dec, ApplicationUser user);

	public boolean deleteDecisionComponent(DecisionKnowledgeElement dec, ApplicationUser user);

	public Long createLink(Link link, ApplicationUser user);

	public List<DecisionKnowledgeElement> searchUnlinkedDecisionComponents(long id, String projectKey);
	
	public List<Issue> getDecisionsInProject(Project project);

	public Core createCore(Project project);

	public Treant createTreant(String issueKey, int depth);
}
