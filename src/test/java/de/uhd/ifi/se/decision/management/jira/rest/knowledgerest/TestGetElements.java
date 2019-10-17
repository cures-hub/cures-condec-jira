package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetElements extends TestSetUp {
	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	private final static String BAD_REQUEST_ERROR = "Getting elements failed due to a bad request.";

	@Before
	public void setUp() {
		super.init();
		knowledgeRest = new KnowledgeRest();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
	}

	@Test
	@NonTransactional
	public void testNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				.build().getEntity(), knowledgeRest.getElements(false, null, null, null, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testFilledAllTreesTrue() {
		Response response = knowledgeRest.getElements(true, "TEST", "", "TEST-4", request);

		// TODO Why are there no Jira issue keys?
//		assertEquals(
//				"[[WI: Implement feature, This is a great solution.], [WI: Yet another work item, We could do it like this!, WI: Implement feature, This is a great solution.], [WI: Do an interesting task], [How can we implement the feature?, We could do it like this!, We will do it like this!, WI: Implement feature, WI: Yet another work item, WI: Do an interesting task, This is a great solution.], [How can we implement the new get function?]]",
//				response.getEntity().toString());
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	@NonTransactional
	public void testFilledAllTreesFalse() {
		Response response = knowledgeRest.getElements(false, "TEST", "", "TEST-4", request);
		
//		assertEquals(
//				"[WI: Implement feature, WI: Yet another work item, WI: Do an interesting task, How can we implement the feature?, How can we implement the new get function?, We could do it like this!, We will do it like this!, This is a great solution.]",
//				response.getEntity().toString());
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
}
