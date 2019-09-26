package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;
import org.ofbiz.core.entity.GenericEntityException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestGetMatrixData extends TestSetUp {
	private ViewRest viewRest;
	protected HttpServletRequest request;
	private static final String INVALID_PROJECTKEY = "Decision knowledge elements cannot be shown since project key is invalid.";

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		init();
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
	}

	@Test
	public void testProjectKeyNullDocumentationLocationNull() {
		assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_PROJECTKEY))
				.build().getEntity(), viewRest.getMatrixData(request, null, null).getEntity());
	}

	@Test
	public void testProjectKeyNonExistentDocumentationLocationNull() {
		assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_PROJECTKEY))
				.build().getEntity(), viewRest.getMatrixData(request, "NotTEST", null).getEntity());
	}

	@Test
	@NonTransactional
	public void testProjectKeyExistentDocumentationLocationNull() {
		assertEquals(200, viewRest.getMatrixData(request,"TEST", null).getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectKeyExistentDocumentationLocationJiraIssue() {
		assertEquals(200, viewRest.getMatrixData(request,"TEST", "i").getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectKeyExistentDocumentationLocationJiraIssueComment() {
		assertEquals(200, viewRest.getMatrixData(request,"TEST", "s").getStatus());
	}

}
