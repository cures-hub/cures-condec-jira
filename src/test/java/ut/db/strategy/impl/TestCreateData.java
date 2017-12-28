package ut.db.strategy.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.DecisionDocumentation.db.strategy.impl.IssueStrategy;
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.Data;
import com.atlassian.jira.mock.issue.MockIssue;

import ut.mocks.MockIssueLinkManager;

import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.issue.link.IssueLinkManager;

public class TestCreateData {

	private IssueStrategy issueStrat;
	
	@Before
	public void setUp(){
		this.issueStrat=new IssueStrategy();
		new MockComponentWorker().init().addMock(IssueLinkManager.class, new MockIssueLinkManager());
	}
	
	@Test
	public void testIssueNull(){
		Issue issue = null;
		Data data = this.issueStrat.createData(issue);
		assertNotNull(data);
	}

	@Test
	public void testIssueEmpty() {
		IssueType issueType = new MockIssueType(12, "Solution");
		Issue issue = new MockIssue(1, "TEST-12");
		((MockIssue) issue).setIssueType(issueType);
				
		Data data = this.issueStrat.createData(issue);
		
		assertNotNull(data);
	}
	
	@Test
	public void testIssueFilled() {
		IssueType issueType = new MockIssueType(12, "Solution");
		Issue issue = new MockIssue(1, "TEST-12");
		((MockIssue) issue).setIssueType(issueType);
		((MockIssue) issue).setDescription("Test");
		((MockIssue) issue).setSummary("Test");
		
		Data data = this.issueStrat.createData(issue);
		
		assertNotNull(data);
	}
}
