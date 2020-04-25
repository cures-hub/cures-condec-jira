package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.ViewRestImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetClassTreant extends TestSetUp {

	private ViewRest viewRest;

	@Before
	public void setUp() {
		init();
		viewRest = new ViewRestImpl();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		CodeClassPersistenceManager ccManager
				= new CodeClassPersistenceManager("Test");
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("Test");
		element.setType("Other");
		element.setDescription("");
		element.setSummary("TestClass.java");
		ccManager.insertKnowledgeElement(element, user);
	}

	@Test
	public void testElementKeyNullDepthNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getClassTreant(null, null, "", null, null, false, 1, 00).getStatus());
	}

	@Test
	public void testElementNotExistsDepthNull() throws GenericEntityException {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getClassTreant(null, "NotTEST", null, "", null, false, 1, 100).getStatus());
	}

	@Test
	public void testElementNotExistsDepthFilled() throws GenericEntityException {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getClassTreant(null, "NotTEST", "3", "", true, false, 1, 100).getStatus());
	}

	@Test
	public void testElementExistsDepthNaN() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getClassTreant(null, "TEST-12", "test", "", true, false, 1, 100).getStatus());
	}

	@Test
	public void testElemetExistsDepthNumber() {
		HttpServletRequest request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		assertEquals(Status.OK.getStatusCode(), viewRest.getClassTreant(request, "TEST-1", "3", "", false, false, 1, 100).getStatus());
	}
}
