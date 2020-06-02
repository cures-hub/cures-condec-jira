package de.uhd.ifi.se.decision.management.jira.persistence.consistency;

import com.atlassian.jira.issue.MutableIssue;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestConsistencyCheckLogHelper extends TestSetUp {

	private MutableIssue issue;
	private MutableIssue otherIssue;

	@Before
	public void setUp() {
		TestSetUp.init();
		issue = JiraIssues.getTestJiraIssues().get(0);
		otherIssue = JiraIssues.getTestJiraIssues().get(1);
		ConsistencyCheckLogHelper.resetConsistencyCheckLogs();

	}

	@Test
	public void testAddCheck() {
		assertTrue("Initially no check should exist.", ConsistencyCheckLogHelper.getCheck(issue.getKey()).isEmpty());
		long idOfCheck = ConsistencyCheckLogHelper.addCheck(issue);
		assertTrue("A pending check should exist.", ConsistencyCheckLogHelper.getCheck(issue.getKey()).isPresent());
		assertEquals("If the added check already exist, the ids should be equal.", ConsistencyCheckLogHelper.addCheck(issue), idOfCheck);

		long idOfNull = ConsistencyCheckLogHelper.addCheck(null);
		assertEquals("If the added check is null, -1 should be the returned id.", -1L, idOfNull);
	}

	@Test
	public void testGetCheck() {
		ConsistencyCheckLogHelper.addCheck(issue);
		assertTrue("A pending check should exist.", ConsistencyCheckLogHelper.getCheck(issue.getKey()).isPresent());

		assertTrue("No pending check should exist.", ConsistencyCheckLogHelper.getCheck(otherIssue.getKey()).isEmpty());

	}

	@Test
	public void testApproval() {
		ConsistencyCheckLogHelper.addCheck(issue);
		assertTrue("Issue is not approved yet. Therefore it needs approval.", ConsistencyCheckLogHelper.doesIssueNeedApproval(issue));

		ConsistencyCheckLogHelper.approveCheck(issue, "TestUser");
		assertFalse("Issue is approved. Therefore it does not need approval.", ConsistencyCheckLogHelper.doesIssueNeedApproval(issue));

		long idOfApprovalIssueNull =ConsistencyCheckLogHelper.approveCheck(null, "TestUser");
		assertEquals("If the approved check's issue is null, -1 should be the returned id.", -1L, idOfApprovalIssueNull);
	}

	@Test
	public void testDeleteCheck() {
		ConsistencyCheckLogHelper.addCheck(issue);
		assertTrue("A pending check should exist.", ConsistencyCheckLogHelper.getCheck(issue.getKey()).isPresent());
		ConsistencyCheckLogHelper.deleteCheck(issue);
		assertTrue("Issue should no longer exist.", ConsistencyCheckLogHelper.getCheck(issue.getKey()).isEmpty());

		ConsistencyCheckLogHelper.deleteCheck(issue);
		assertTrue("Issue should no longer exist.", ConsistencyCheckLogHelper.getCheck(issue.getKey()).isEmpty());
		try {
			ConsistencyCheckLogHelper.deleteCheck(null);

		}catch (Exception e){
			assertNotNull("No exception should be thrown", e);
		}

	}

	@AfterEach
	public void reset() {
		ConsistencyCheckLogHelper.resetConsistencyCheckLogs();
	}


}
