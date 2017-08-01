package com.atlassian.DecisionDocumentation.db.strategy;

import java.util.List;

import com.atlassian.DecisionDocumentation.rest.Decisions.model.DecisionRepresentation;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.LinkRepresentation;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.SimpleDecisionRepresentation;
import com.atlassian.DecisionDocumentation.rest.treants.model.Treant;
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.Core;
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.Data;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

/**
 * 
 * @author Ewald Rode
 * @description
 */
public interface Strategy {
	public Data createDecisionComponent(DecisionRepresentation dec, ApplicationUser user);
	public void editDecisionComponent(DecisionRepresentation dec, ApplicationUser user);
	public void deleteDecisionComponent(DecisionRepresentation dec, ApplicationUser user);
	
	public Long createLink(LinkRepresentation link, ApplicationUser user);
	public void deleteLink(LinkRepresentation link, ApplicationUser user);
	
	public List<SimpleDecisionRepresentation> searchUnlinkedDecisionComponents(long id, String projectKey);
	
	public Core createCore(Project project);
	public Treant createTreant(String issueKey, int depth);
}
