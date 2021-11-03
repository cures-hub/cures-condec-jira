package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesConfiguration;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNotesRest;

public class TestProposeElements extends TestSetUp {

	private HttpServletRequest request;
	private ReleaseNotesRest releaseNoteRest;
	private ReleaseNotesConfiguration releaseNoteConfiguration;

	@Before
	public void setUp() {
		init();
		releaseNoteRest = new ReleaseNotesRest();
		request = new MockHttpServletRequest();
		releaseNoteConfiguration = new ReleaseNotesConfiguration();
	}

	@Test
	public void testJiraIssuesCanBeFound() {
		releaseNoteConfiguration.setJiraIssueTypesForImprovements(List.of("Task"));
		assertEquals(Response.Status.OK.getStatusCode(),
				releaseNoteRest.proposeElements(request, "TEST", releaseNoteConfiguration).getStatus());
	}

	@Test
	public void testJiraIssuesCannotBeFound() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				releaseNoteRest.proposeElements(request, "TEST", releaseNoteConfiguration).getStatus());
	}

	@Test
	public void testProjectUnknown() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				releaseNoteRest.proposeElements(request, "UNKNOWNPROJECT", releaseNoteConfiguration).getStatus());
	}
}