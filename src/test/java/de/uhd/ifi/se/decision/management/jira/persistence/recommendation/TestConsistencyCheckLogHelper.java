package de.uhd.ifi.se.decision.management.jira.persistence.recommendation;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.ConsistencyCheckLogsInDatabase;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestConsistencyCheckLogHelper extends TestSetUp {

	private KnowledgeElement ke;
	private KnowledgeElement otherKe;

	@Before
	public void setUp() {
		TestSetUp.init();
		ke = new KnowledgeElement(JiraIssues.getTestJiraIssues().get(0));
		otherKe = new KnowledgeElement(JiraIssues.getTestJiraIssues().get(1));
		ConsistencyCheckLogHelper.resetConsistencyCheckLogs();

	}

	@Test
	public void testAddCheck() {
		assertTrue("Initially no check should exist.", ConsistencyCheckLogHelper.getCheck(ke).isEmpty());
		long idOfCheck = ConsistencyCheckLogHelper.addCheck(ke);
		assertTrue("A pending check should exist.", ConsistencyCheckLogHelper.getCheck(ke).isPresent());
		assertEquals("If the added check already exist, the ids should be equal.", ConsistencyCheckLogHelper.addCheck(ke), idOfCheck);

		long idOfNull = ConsistencyCheckLogHelper.addCheck(null);
		assertEquals("If the added check is null, -1 should be the returned id.", -1L, idOfNull);
	}

	@Test
	public void testGetCheck() {
		ConsistencyCheckLogHelper.addCheck(ke);
		assertTrue("A pending check should exist.", ConsistencyCheckLogHelper.getCheck(ke).isPresent());

		assertTrue("No pending check should exist.", ConsistencyCheckLogHelper.getCheck(otherKe).isEmpty());

	}

	@Test
	public void testApproval() {
		ConsistencyCheckLogHelper.addCheck(ke);
		assertTrue("Issue is not approved yet. Therefore it needs approval.", ConsistencyCheckLogHelper.doesKnowledgeElementNeedApproval(ke));

		ConsistencyCheckLogHelper.approveCheck(ke, "TestUser");
		assertFalse("Issue is approved. Therefore it does not need approval.", ConsistencyCheckLogHelper.doesKnowledgeElementNeedApproval(ke));

		long idOfApprovalIssueNull = ConsistencyCheckLogHelper.approveCheck(null, "TestUser");
		assertEquals("If the approved check's ke is null, -1 should be the returned id.", -1L, idOfApprovalIssueNull);
	}

	@Test
	public void testIsCheckApproved() {
		//check.getApprover() != null && !check.getApprover().isBlank() && !check.getApprover().isEmpty();
		assertFalse("Check is null. Therefore it is not approved.", ConsistencyCheckLogHelper.isCheckApproved(null));

		ConsistencyCheckLogHelper.addCheck(ke);
		Optional<ConsistencyCheckLogsInDatabase> check = ConsistencyCheckLogHelper.getCheck(ke);
		assertFalse("Check is new. Therefore it is not approved.", ConsistencyCheckLogHelper.isCheckApproved(check.get()));
		check.get().setApprover(null);
		assertFalse("Approver is null. Therefore it is not approved.", ConsistencyCheckLogHelper.isCheckApproved(check.get()));
		check.get().setApprover("");
		assertFalse("Approver is blank. Therefore it is not approved.", ConsistencyCheckLogHelper.isCheckApproved(check.get()));

		check.get().setApprover("TESTUSER");
		assertTrue("Approver is set. Therefore it is approved.", ConsistencyCheckLogHelper.isCheckApproved(check.get()));

	}

	@Test
	public void testDeleteCheck() {
		ConsistencyCheckLogHelper.addCheck(ke);
		assertTrue("A pending check should exist.", ConsistencyCheckLogHelper.getCheck(ke).isPresent());
		ConsistencyCheckLogHelper.deleteCheck(ke);
		assertTrue("Issue should no longer exist.", ConsistencyCheckLogHelper.getCheck(ke).isEmpty());

		ConsistencyCheckLogHelper.deleteCheck(ke);
		assertTrue("Issue should no longer exist.", ConsistencyCheckLogHelper.getCheck(ke).isEmpty());
		try {
			ConsistencyCheckLogHelper.deleteCheck(null);

		} catch (Exception e) {
			assertNotNull("No exception should be thrown", e);
		}

	}

	@AfterEach
	public void reset() {
		ConsistencyCheckLogHelper.resetConsistencyCheckLogs();
	}


}
