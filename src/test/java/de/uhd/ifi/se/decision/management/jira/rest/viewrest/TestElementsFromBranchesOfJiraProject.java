package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.view.diffviewer.DiffViewer;
import org.junit.Before;
import org.junit.Test;
import org.ofbiz.core.entity.GenericEntityException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestElementsFromBranchesOfJiraProject extends TestSetUpGit {
	private ViewRest viewRest;

	private static final String INVALID_ISSUEKEY = "Decision knowledge elements cannot be shown since issue key is invalid.";
	protected HttpServletRequest request;

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		init();
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
	}

	@Test
	public void testEmptyIssueKey() throws GenericEntityException {
		assertEquals(400, viewRest.getAllFeatureBranchesTree("").getStatus());
	}

	@Test
	public void testUnknownProjectKey() throws GenericEntityException {
		assertEquals(400, viewRest.getAllFeatureBranchesTree("HOUDINI").getStatus());
		try {
			assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_ISSUEKEY))
					.build().getEntity(), viewRest.getFeatureBranchTree(request, "HOUDINI").getEntity());
		} catch (PermissionException e) {
			assertNull(e);
		}
	}

	@Test
	public void testExistingProjectKey() throws GenericEntityException {
		assertEquals(200, viewRest.getAllFeatureBranchesTree("TEST").getStatus());
		Object receivedEntity = viewRest.getAllFeatureBranchesTree("TEST").getEntity();

		Object expectedEntity = new DiffViewer(null);
		assertEquals(expectedEntity.getClass(), receivedEntity.getClass());
	}
}
