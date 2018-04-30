package de.uhd.ifi.se.decision.documentation.jira.mocks;

import java.util.ArrayList;
import java.util.Collection;

import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;

/**
 *
 * 
 * @description This function is not implemented in the Jira Mock Class
 *
 */
public class MockIssueManager extends com.atlassian.jira.mock.MockIssueManager {

	public Collection<Long> getIssueIdsForProject(Long id) throws GenericEntityException{
		if(id==10) {
			throw new GenericEntityException();
		}
		Collection<Long> col = new ArrayList<>();
		if(id==30) {
			Issue issue=this.getIssueObject((long)30);
			col.add(issue.getId());
			return col;
		}
		// Iterate over the IssueTypes that are added in the TestIssueStartegySup
		for(int i=2;i<=15;i++) {
			Issue issue=this.getIssueObject((long)i);
			if(id==issue.getProjectId()) {
				col.add(issue.getId());
			}
		}
		if(id==30) {
			Issue issue=this.getIssueObject((long)30);
			col.add(issue.getId());
		}
		return col;		
	}
	
	public MutableIssue getIssueByCurrentKey(String key) {
		if(key.equals("30")) {
			Issue issue=this.getIssueObject((long)30);
			return (MutableIssue) issue;
		}
		for(int i=2;i<=16;i++) {
			Issue issue=this.getIssueObject((long)i);
			if(key.equals(issue.getId().toString())) {
				return (MutableIssue) issue;
			}
		}
		return null;
	}
}
