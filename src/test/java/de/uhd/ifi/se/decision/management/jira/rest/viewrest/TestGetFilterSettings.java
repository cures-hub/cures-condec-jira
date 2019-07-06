package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterDataProvider;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.view.treant.TestTreant;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestTreant.AoSentenceTestDatabaseUpdater.class)
public class TestGetFilterSettings extends TestSetUpWithIssues {

	private ViewRest viewRest;
	private EntityManager entityManager;
	protected HttpServletRequest request;

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		request = new MockHttpServletRequest();
	}

	@Test
	public void testRequestNullSearchTermNullElementNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				viewRest.getFilterSettings(null, null, null).getStatus());
	}

	@Test
	public void testRequestFilledSearchTermEmptyElementExistent() {
		Response filterSettingsResponse = viewRest.getFilterSettings(request, "", "TEST-12");
		assertEquals(Response.Status.OK.getStatusCode(), filterSettingsResponse.getStatus());

		FilterDataProvider filterSettings = (FilterDataProvider) filterSettingsResponse.getEntity();
		assertEquals(6, filterSettings.getDocumentationLocations().size());
		assertEquals(16, filterSettings.getAllIssueTypes().size());
		assertEquals(-1, filterSettings.getEndDate());
		assertEquals(-1, filterSettings.getStartDate());
	}
	
	@Test
	public void testRequestFilledSearchTermJqlElementExistent() {
		Response filterSettingsResponse = viewRest.getFilterSettings(request, "?jql=(.)+", "TEST-12");
		assertEquals(Response.Status.OK.getStatusCode(), filterSettingsResponse.getStatus());

		FilterDataProvider filterSettings = (FilterDataProvider) filterSettingsResponse.getEntity();
		assertEquals(6, filterSettings.getDocumentationLocations().size());
		assertEquals(16, filterSettings.getAllIssueTypes().size());
		assertEquals(-1, filterSettings.getEndDate());
		assertEquals(-1, filterSettings.getStartDate());
	}
	
	@Test
	public void testRequestFilledSearchTermPresetJiraFilterElementExistent() {
		Response filterSettingsResponse = viewRest.getFilterSettings(request, "?filter=allopenissues", "TEST-12");
		assertEquals(Response.Status.OK.getStatusCode(), filterSettingsResponse.getStatus());

		FilterDataProvider filterSettings = (FilterDataProvider) filterSettingsResponse.getEntity();
		assertEquals(6, filterSettings.getDocumentationLocations().size());
		assertEquals(16, filterSettings.getAllIssueTypes().size());
		assertEquals(-1, filterSettings.getEndDate());
		assertEquals(-1, filterSettings.getStartDate());
	}
}
