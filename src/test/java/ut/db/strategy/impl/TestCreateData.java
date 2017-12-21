package ut.db.strategy.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.DecisionDocumentation.db.strategy.impl.IssueStrategy;
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.Data;
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
		Issue issue = new IssueTestMock();
		Data data = this.issueStrat.createData(issue);
		assertNotNull(data);
	}
	
	@Test
	public void testIssueFilled() {
		Issue issue = new IssueTestMock();
		((IssueTestMock) issue).setId(1);
		((IssueTestMock) issue).setKey("Test-112");
		((IssueTestMock) issue).setSummary("Test");
		((IssueTestMock) issue).setDescription("Test");
		Data data = this.issueStrat.createData(issue);
		assertNotNull(data);
	}
}
