package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
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

		FilterSettings filterSettings = (FilterSettings) filterSettingsResponse.getEntity();
		assertEquals(5, filterSettings.getNamesOfDocumentationLocations().size());
		assertEquals(16, filterSettings.getAllJiraIssueTypes().size());
		assertEquals(16, filterSettings.getNamesOfSelectedJiraIssueTypes().size());
		assertEquals(-1, filterSettings.getCreatedEarliest());
		assertEquals(-1, filterSettings.getCreatedLatest());
	}

	@Test
	public void testRequestFilledSearchTermJqlElementExistent() {
		Response filterSettingsResponse = viewRest.getFilterSettings(request, "?jql=issuetype%20%3D%20Issue",
				"TEST-12");
		assertEquals(Response.Status.OK.getStatusCode(), filterSettingsResponse.getStatus());

		FilterSettingsImpl filterSettings = (FilterSettingsImpl) filterSettingsResponse.getEntity();
		assertEquals(5, filterSettings.getNamesOfDocumentationLocations().size());
		List<String> issueTypesMatchingFilter = filterSettings.getNamesOfSelectedJiraIssueTypes();
		assertEquals("Issue", issueTypesMatchingFilter.get(0));
		assertEquals(1, issueTypesMatchingFilter.size());
		assertEquals(-1, filterSettings.getCreatedEarliest());
		assertEquals(-1, filterSettings.getCreatedLatest());
	}

	@Test
	public void testRequestFilledSearchTermPresetJiraFilterElementExistent() {
		Response filterSettingsResponse = viewRest.getFilterSettings(request, "?filter=allopenissues", "TEST-12");
		assertEquals(Response.Status.OK.getStatusCode(), filterSettingsResponse.getStatus());

		FilterSettingsImpl filterSettings = (FilterSettingsImpl) filterSettingsResponse.getEntity();
		assertEquals(5, filterSettings.getNamesOfDocumentationLocations().size());
		assertEquals(-1, filterSettings.getCreatedEarliest());
		assertEquals(-1, filterSettings.getCreatedLatest());
	}
}
