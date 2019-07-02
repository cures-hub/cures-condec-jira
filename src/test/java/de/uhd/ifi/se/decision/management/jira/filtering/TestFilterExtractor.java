package de.uhd.ifi.se.decision.management.jira.filtering;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestFilterExtractor extends TestSetUpWithIssues {

	private ApplicationUser user;

	@Before
	public void setUp() {
		initialization();
		user = ComponentAccessor.getUserManager().getUserByName("NoFails");
	}

	@Test
	public void testConstructorFilterStringNullNullNull() {
		FilterExtractor extractor = new FilterExtractor(null, null, null);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringFilledNullNull() {
		FilterExtractor extractor = new FilterExtractor("TEST", null, null);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringNullFilledNull() {
		FilterExtractor extractor = new FilterExtractor(null, user, null);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringNullNullEmpty() {
		FilterExtractor extractor = new FilterExtractor(null, null, "");
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringNullNullFilledString() {
		String filter = "allopenissues";
		FilterExtractor extractor = new FilterExtractor(null, null, "\\?filter=(.)+" + filter);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringNullNullFilledJQL() {
		String jql = "project%20%3D%20CONDEC%20AND%20assignee%20%3D%20currentUser()%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20updated%20DESC";
		FilterExtractor extractor = new FilterExtractor(null, null, "\\?filter=(.)+" + jql);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringFilledFilledEmpty() {
		FilterExtractor extractor = new FilterExtractor("TEST", user, "");
		assertEquals(0.0, extractor.getFilteredDecisions().size(), 0.0);
	}

	@Test
	public void testConstructorFilterStringFilledFilledString() {
		String filter = "allopenissues";
		FilterExtractor extractor = new FilterExtractor("TEST", user, "?filter=" + filter);
		assertEquals(0.0, extractor.getFilteredDecisions().size(), 0.0);
	}

	@Test
	public void testConstructorFilterStringFilledFilledFilledJQL() {
		String jql = "project%20%3D%20CONDEC%20AND%20assignee%20%3D%20currentUser()%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20updated%20DESC";
		FilterExtractor extractor = new FilterExtractor("Test", user, "?jql=" + jql);
		assertEquals(0.0, extractor.getFilteredDecisions().size(), 0.0);
	}

	// Test FilterExtractor constructor with personal filter data

	@Test
	public void testConstructorFilterOwnNullProject() {
		FilterExtractor extractor = new FilterExtractor(null, null, null, null, 0, 0);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterOwnNullUser() {
		FilterExtractor extractor = new FilterExtractor("TEST", null, null, null, 0, 0);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterOwnNullSearch() {
		FilterExtractor extractor = new FilterExtractor("TEST", user, null, null, -1, -1);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterOwnEmpty() {
		FilterExtractor extractor = new FilterExtractor("TEST", user, "", "", -1, -1);
		assertEquals(0.0, extractor.getFilteredDecisions().size(), 0.0);
	}

	@Test
	public void testConstructorFilterOwnFilledNoTime() {
		String jql = "project%20%3D%20CONDEC%20AND%20assignee%20%3D%20currentUser()%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20updated%20DESC";
		FilterExtractor extractor = new FilterExtractor("TEST", user, jql, "Issue", -1, -1);
		assertEquals(0.0, extractor.getFilteredDecisions().size(), 0.0);
	}

	@Test
	public void testConstructorFilterOwnFilled() {
		String jql = "project%20%3D%20CONDEC%20AND%20assignee%20%3D%20currentUser()%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20updated%20DESC";
		FilterExtractor extractor = new FilterExtractor("TEST", user, jql, "Issue", System.currentTimeMillis() - 100, System.currentTimeMillis());
		assertEquals(0.0, extractor.getFilteredDecisions().size(), 0.0);
	}

}
