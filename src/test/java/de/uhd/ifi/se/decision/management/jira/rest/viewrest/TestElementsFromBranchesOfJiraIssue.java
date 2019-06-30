package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.view.diffviewer.DiffViewer;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestElementsFromBranchesOfJiraIssue extends TestSetUpGit {
	private EntityManager entityManager;
	private ViewRest viewRest;

	private static final String INVALID_ISSUEKEY = "Decision knowledge elements cannot be shown since issue key is invalid.";

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
	}

	@Test
	public void testEmptyIssueKey() throws GenericEntityException {
		assertEquals(400, viewRest.getFeatureBranchTree("").getStatus());
	}

	@Test
	public void testUnknownIssueKey() throws GenericEntityException {
		assertEquals(400, viewRest.getFeatureBranchTree("HOUDINI-1").getStatus());
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_ISSUEKEY))
				.build().getEntity(), viewRest.getFeatureBranchTree("HOUDINI-1").getEntity());
	}

	@Test
	public void testExistingIssueKey() throws GenericEntityException {
		assertEquals(200, viewRest.getFeatureBranchTree("TEST-1").getStatus());
		Object receivedEntity = viewRest.getFeatureBranchTree("TEST-1").getEntity();

		Object expectedEntity = new DiffViewer(null);
		assertEquals(expectedEntity.getClass(), receivedEntity.getClass());
	}
}
