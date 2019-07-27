package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestFilterExtractor extends TestSetUp {

	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringNullNullNull() {
		FilterExtractor extractor = new FilterExtractor(null, null, (String) null);
		assertNull(extractor.getQueryHandler());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringFilledNullNull() {
		FilterExtractor extractor = new FilterExtractor("TEST", null, (String) null);
		assertNull(extractor.getQueryHandler());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringNullFilledNull() {
		FilterExtractor extractor = new FilterExtractor(null, user, (String) null);
		assertNull(extractor.getQueryHandler());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringNullNullEmpty() {
		FilterExtractor extractor = new FilterExtractor(null, null, "");
		assertNull(extractor.getQueryHandler());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringNullNullFilledString() {
		String filter = "allopenissues";
		FilterExtractor extractor = new FilterExtractor(null, null, "\\?filter=(.)+" + filter);
		assertNull(extractor.getQueryHandler());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringNullNullFilledJQL() {
		FilterExtractor extractor = new FilterExtractor(null, null, "\\?filter=allissues?jql=project=TEST");
		assertNull(extractor.getQueryHandler());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringFilledFilledEmpty() {
		FilterExtractor extractor = new FilterExtractor("TEST", user, "");
		// the empty query will be changed to "allissues", i.e. "type != null"
		assertEquals(2, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringFilledFilledString() {
		FilterExtractor extractor = new FilterExtractor("TEST", user, "?filter=allopenissues");
		assertEquals(2, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringFilledFilledFilledJQL() {
		FilterExtractor extractor = new FilterExtractor("TEST", user, "?jql=project=TEST");
		assertEquals(2, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterOwnNullProject() {
		FilterExtractor extractor = new FilterExtractor(null, (FilterSettings) null);
		assertNull(extractor.getQueryHandler());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterOwnNullSearch() {
		FilterExtractor extractor = new FilterExtractor(user, (FilterSettings) null);
		assertNull(extractor.getQueryHandler());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterOwnFilled() {
		FilterExtractor extractor = new FilterExtractor(user, new FilterSettingsImpl("TEST", ""));
		// the empty query will be changed to "allissues", i.e. "type != null"
		assertEquals(2, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	@NonTransactional
	public void testGetGraphsMatchingQueryEmpty() {
		FilterExtractor extractor = new FilterExtractor("Test", user, "");
		// the empty query will be changed to "allissues", i.e. "type != null"
		assertEquals(1, extractor.getAllGraphs().size());
	}

	@Test
	@NonTransactional
	public void testGetGraphsMatchingQueryFilled() {
		FilterExtractor extractor = new FilterExtractor("Test", user, "?jql=project=TEST");
		assertEquals(1, extractor.getAllGraphs().size());
	}

	@Test
	@NonTransactional
	public void testGetFilterSettings() {
		FilterExtractor extractor = new FilterExtractor("Test", user, "?jql=project=TEST");
		assertEquals("?jql=project=TEST", extractor.getFilterSettings().getSearchString());
	}
}
