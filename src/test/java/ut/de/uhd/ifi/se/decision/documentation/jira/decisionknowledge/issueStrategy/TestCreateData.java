package ut.de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.issueStrategy;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.atlassian.jira.mock.issue.MockIssue;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Data;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;

/**
 * @author Tim Kuchenbuch
 */
public class TestCreateData extends TestIssueStrategySetUp {
	
//	@Test
//	public void testIssueNull(){
//		Issue issue = null;
//		Data data = this.issueStrategy.createData(new DecisionKnowledgeElement(issue));
//		assertNotNull(data);
//	}
//
//	@Test
//	public void testIssueEmpty() {
//		IssueType issueType = new MockIssueType(12, "Solution");
//		Issue issue = new MockIssue(1, "TEST-12");
//		((MockIssue) issue).setIssueType(issueType);
//
//		Data data = this.issueStrategy.createData(new DecisionKnowledgeElement(issue));
//
//		assertNotNull(data);
//	}
//
//	@Test
//	public void testIssueFilled() {
//		IssueType issueType = new MockIssueType(12, "Solution");
//		Issue issue = new MockIssue(1, "TEST-12");
//		((MockIssue) issue).setIssueType(issueType);
//		((MockIssue) issue).setDescription("Test");
//		((MockIssue) issue).setSummary("Test");
//
//		Data data = this.issueStrategy.createData(new DecisionKnowledgeElement(issue));
//
//		assertNotNull(data);
//	}
//
//	@Test
//	public void testIssueFilledLinks() {
//		new TreeViewerKeyValuePairList().init();
//		IssueType issueType = new MockIssueType(12, "Solution");
//		Issue issue = new MockIssue(20, "TEST-12");
//		((MockIssue) issue).setIssueType(issueType);
//		((MockIssue) issue).setDescription("Test");
//		((MockIssue) issue).setSummary("Test");
//
//		Data data = this.issueStrategy.createData(new DecisionKnowledgeElement(issue));
//
//		assertNotNull(data);
//	}
}
