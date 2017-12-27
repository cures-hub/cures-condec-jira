package ut.db.strategy.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.DecisionDocumentation.db.strategy.impl.IssueStrategy;
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.Data;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.issue.Issue;

public class TestCreateData {

	private IssueStrategy issueStrat;
	
	@Before
	public void setUp(){
		this.issueStrat=new IssueStrategy();
	
	}
	
	@Test
	public void testIssueNull(){
		Issue issue = null;
		Data data = this.issueStrat.createData(issue);
		assertNotNull(data);
	}

	@Test
	public void testIssueEmpty() {
		Issue issue = new MockIssue();
		Data data = this.issueStrat.createData(issue);
		assertNotNull(data);
	}
	
	@Ignore
	public void testIssueFilled() {
		Issue issue = new MockIssue();
		Data data = this.issueStrat.createData(issue);
		assertNotNull(data);
	}
}
