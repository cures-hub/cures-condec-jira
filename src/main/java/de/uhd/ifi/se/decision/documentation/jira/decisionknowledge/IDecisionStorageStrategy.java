package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import java.util.List;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.view.treants.Treant;
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Core;
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Data;

/**
 * @author Ewald Rode
 * @description Interface for different Solutions approaches to creating,
 *              editing, deleting and rendering decisioncomponents and their
 *              links
 */
public interface IDecisionStorageStrategy {
	public Data createDecisionComponent(DecisionKnowledgeElement dec, ApplicationUser user);

	public Data editDecisionComponent(DecisionKnowledgeElement dec, ApplicationUser user);

	public boolean deleteDecisionComponent(DecisionKnowledgeElement dec, ApplicationUser user);

	public Long createLink(LinkRepresentation link, ApplicationUser user);

	public List<SimpleDecisionRepresentation> searchUnlinkedDecisionComponents(long id, String projectKey);
	
	public List<Issue> getDecisionsInProject(Project project);

	public Core createCore(Project project);

	public Treant createTreant(String issueKey, int depth);
}
