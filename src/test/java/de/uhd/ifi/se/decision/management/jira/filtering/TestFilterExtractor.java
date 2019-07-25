package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;

public class TestFilterExtractor extends TestSetUpWithIssues {

	private ApplicationUser user;

	@Before
	public void setUp() {
		initialization();
		user = ComponentAccessor.getUserManager().getUserByName("SysAdmin");
	}

	@Test
	public void testConstructorFilterStringNullNullNull() {
		FilterExtractor extractor = new FilterExtractor(null, null, (String) null);
		assertNull(extractor.getQueryHandler());
	}

	@Test
	public void testConstructorFilterStringFilledNullNull() {
		FilterExtractor extractor = new FilterExtractor("TEST", null, (String) null);
		assertNull(extractor.getQueryHandler());
	}

	@Test
	public void testConstructorFilterStringNullFilledNull() {
		FilterExtractor extractor = new FilterExtractor(null, user, (String) null);
		assertNull(extractor.getQueryHandler());
	}

	@Test
	public void testConstructorFilterStringNullNullEmpty() {
		FilterExtractor extractor = new FilterExtractor(null, null, "");
		assertNull(extractor.getQueryHandler());
	}

	@Test
	public void testConstructorFilterStringNullNullFilledString() {
		String filter = "allopenissues";
		FilterExtractor extractor = new FilterExtractor(null, null, "\\?filter=(.)+" + filter);
		assertNull(extractor.getQueryHandler());
	}

	@Test
	public void testConstructorFilterStringNullNullFilledJQL() {
		FilterExtractor extractor = new FilterExtractor(null, null, "\\?filter=allissues?jql=project=TEST");
		assertNull(extractor.getQueryHandler());
	}

	@Test
	public void testConstructorFilterStringFilledFilledEmpty() {
		FilterExtractor extractor = new FilterExtractor("TEST", user, "");
		// the empty query will be changed to "allissues", i.e. "type != null"
		assertEquals(2, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	public void testConstructorFilterStringFilledFilledString() {
		FilterExtractor extractor = new FilterExtractor("TEST", user, "?filter=allopenissues");
		assertEquals(2, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	public void testConstructorFilterStringFilledFilledFilledJQL() {
		FilterExtractor extractor = new FilterExtractor("TEST", user, "?jql=project=TEST");
		assertEquals(2, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	public void testConstructorFilterOwnNullProject() {
		FilterExtractor extractor = new FilterExtractor(null, (FilterSettings) null);
		assertNull(extractor.getQueryHandler());
	}

	@Test
	public void testConstructorFilterOwnNullSearch() {
		FilterExtractor extractor = new FilterExtractor(user, (FilterSettings) null);
		assertNull(extractor.getQueryHandler());
	}

	@Test
	public void testConstructorFilterOwnFilled() {
		FilterExtractor extractor = new FilterExtractor(user, new FilterSettingsImpl("TEST", ""));
		// the empty query will be changed to "allissues", i.e. "type != null"
		assertEquals(2, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	public void testGetGraphsMatchingQueryEmpty() {
		FilterExtractor extractor = new FilterExtractor("Test", user, "");
		// the empty query will be changed to "allissues", i.e. "type != null"
		assertEquals(1, extractor.getAllGraphs().size());
	}

	@Test
	public void testGetGraphsMatchingQueryFilled() {
		FilterExtractor extractor = new FilterExtractor("Test", user, "?jql=project=TEST");
		assertEquals(1, extractor.getAllGraphs().size());
	}

	@Test
	public void testGetFilterSettings() {
		FilterExtractor extractor = new FilterExtractor("Test", user, "?jql=project=TEST");
		assertEquals("?jql=project=TEST", extractor.getFilterSettings().getSearchString());
	}
}
