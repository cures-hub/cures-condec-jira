package com.atlassian.DecisionDocumentation.db.strategy;

import java.util.List;

import com.atlassian.DecisionDocumentation.rest.model.DecisionRepresentation;
import com.atlassian.DecisionDocumentation.rest.model.LinkRepresentation;
import com.atlassian.DecisionDocumentation.rest.model.SimpleDecisionRepresentation;
import com.atlassian.jira.user.ApplicationUser;

/**
 * 
 * @author Ewald Rode
 * @description
 */
public interface Strategy {
	public long createDecisionComponent(DecisionRepresentation dec, ApplicationUser user);
	public void editDecisionComponent(DecisionRepresentation dec, ApplicationUser user);
	public void deleteDecisionComponent(DecisionRepresentation dec, ApplicationUser user);
	
	public void createLink(LinkRepresentation link, ApplicationUser user);
	public void deleteLink(LinkRepresentation link, ApplicationUser user);
	
	public List<SimpleDecisionRepresentation> searchUnlinkedDecisionComponents(long id, String projectKey);
}
