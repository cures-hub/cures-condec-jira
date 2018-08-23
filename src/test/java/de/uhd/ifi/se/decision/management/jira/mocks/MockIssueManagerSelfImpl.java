package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.ArrayList;
import java.util.Collection;

import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;

/**
 * This class provides functions not implemented in the Jira Mock Class
 */
public class MockIssueManagerSelfImpl extends com.atlassian.jira.mock.MockIssueManager {

	@Override
	public Collection<Long> getIssueIdsForProject(Long id) throws GenericEntityException {
		if (id == 10) {
			throw new GenericEntityException();
		}
		Collection<Long> col = new ArrayList<>();
		if (id == 30) {
			Issue issue = this.getIssueObject((long) 30);
			col.add(issue.getId());
			return col;
		}
		// Iterate over the IssueTypes that are added in the TestIssueStrategySetUp
		for (int i = 2; i <= 15; i++) {
			Issue issue = this.getIssueObject((long) i);
			if (id.equals(issue.getProjectId())) {
				col.add(issue.getId());
			}
		}
		return col;
	}

	@Override
	public MutableIssue getIssueByCurrentKey(String key) {
		if ("30".equals(key)) {
			return this.getIssueObject((long) 30);
		}
		if("CONDEC-1234".equals(key)){
			Issue issue = this.getIssueObject((long)1234);
			return (MutableIssue) issue;
		}
		for (int i = 2; i <= 16; i++) {
			Issue issue = this.getIssueObject((long) i);
			if (key.equals(issue.getId().toString())) {
				return (MutableIssue) issue;
			}
			if (key.equals(issue.getKey())) {
				return (MutableIssue) issue;
			}
		}

		return null;
	}
}
