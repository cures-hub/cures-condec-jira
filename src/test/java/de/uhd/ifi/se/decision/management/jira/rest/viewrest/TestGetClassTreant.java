package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetClassTreant extends TestSetUp {

	private ViewRest viewRest;

	@Before
	public void setUp() {
		init();
		viewRest = new ViewRest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		CodeClassPersistenceManager ccManager = new CodeClassPersistenceManager("Test");
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("Test");
		element.setType("Other");
		element.setDescription("");
		element.setSummary("TestClass.java");
		ccManager.insertKnowledgeElement(element, user);
	}

	@Test
	public void testRequestNullElementKeyNullFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getClassTreant(null, null, false, null).getStatus());
	}

	@Test
	public void testRequestNullElementNotExistsFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getClassTreant(null, "NotTEST", false, null).getStatus());
	}

	@Test
	public void testRequestFilledElementNotExistsFilterSettingsNull() {
		HttpServletRequest request = new MockHttpServletRequest();
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getClassTreant(request, "NotTEST", false, null).getStatus());
	}

	@Test
	public void testRequestFilledCodeElementNotExistsFilterSettingsNull() {
		HttpServletRequest request = new MockHttpServletRequest();
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getClassTreant(request, "TEST-12", false, null).getStatus());
	}

	@Test
	public void testRequestFilledElementExistsFilterSettingsFilled() {
		HttpServletRequest request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		assertEquals(Status.OK.getStatusCode(),
				viewRest.getClassTreant(request, "TEST-1", true, new FilterSettings("TEST", "")).getStatus());
	}
}
