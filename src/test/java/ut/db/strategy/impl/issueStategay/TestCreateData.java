package ut.db.strategy.impl.issueStategay;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.atlassian.DecisionDocumentation.rest.treeviewer.model.Data;
import com.atlassian.jira.mock.issue.MockIssue;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;

public class TestCreateData extends TestIssueStartegySup{
	
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
