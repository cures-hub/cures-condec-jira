package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.KnowledgeRestImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetElements extends TestSetUp {
	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		super.init();
		knowledgeRest = new KnowledgeRestImpl();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.getElements(null, false, null, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testFilledAllTreesTrue() {
		Response response = knowledgeRest.getElements(request, true, "TEST", "");

		// TODO Why are there no Jira issue keys?
		// assertEquals(
		// "[[WI: Implement feature, This is a great solution.], [WI: Yet another work
		// item, We could do it like this!, WI: Implement feature, This is a great
		// solution.], [WI: Do an interesting task], [How can we implement the feature?,
		// We could do it like this!, We will do it like this!, WI: Implement feature,
		// WI: Yet another work item, WI: Do an interesting task, This is a great
		// solution.], [How can we implement the new get function?]]",
		// response.getEntity().toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	@NonTransactional
	public void testFilledAllTreesFalse() {
		Response response = knowledgeRest.getElements(request, false, "TEST", "");

		// assertEquals(
		// "[WI: Implement feature, WI: Yet another work item, WI: Do an interesting
		// task, How can we implement the feature?, How can we implement the new get
		// function?, We could do it like this!, We will do it like this!, This is a
		// great solution.]",
		// response.getEntity().toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
}
