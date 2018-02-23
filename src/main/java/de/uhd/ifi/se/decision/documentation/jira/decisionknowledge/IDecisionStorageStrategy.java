package de.uhd.ifi.se.decision.documentation.jira.db.strategy;

import java.util.List;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.rest.decisions.model.DecisionRepresentation;
import de.uhd.ifi.se.decision.documentation.jira.rest.decisions.model.LinkRepresentation;
import de.uhd.ifi.se.decision.documentation.jira.rest.decisions.model.SimpleDecisionRepresentation;
import de.uhd.ifi.se.decision.documentation.jira.rest.treants.model.Treant;
import de.uhd.ifi.se.decision.documentation.jira.rest.treeviewer.model.Core;
import de.uhd.ifi.se.decision.documentation.jira.rest.treeviewer.model.Data;

/**
 * @author Ewald Rode
 * @description Interface for different Solutions approaches to creating, editing, deleting and rendering decisioncomponents and their links
 */
public interface Strategy {
	public Data createDecisionComponent(DecisionRepresentation dec, ApplicationUser user);
	public Data editDecisionComponent(DecisionRepresentation dec, ApplicationUser user);
	public boolean deleteDecisionComponent(DecisionRepresentation dec, ApplicationUser user);
	
	public Long createLink(LinkRepresentation link, ApplicationUser user);
	
	public List<SimpleDecisionRepresentation> searchUnlinkedDecisionComponents(long id, String projectKey);
	
	public Core createCore(Project project);
	public Treant createTreant(String issueKey, int depth);
}
