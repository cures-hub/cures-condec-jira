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
		user = ComponentAccessor.getUserManager().getUserByName("NoFails");
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
		assertEquals(0, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	public void testConstructorFilterStringFilledFilledString() {
		String filter = "allopenissues";
		FilterExtractor extractor = new FilterExtractor("TEST", user, "?filter=" + filter);
		assertEquals(0, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	public void testConstructorFilterStringFilledFilledFilledJQL() {
		FilterExtractor extractor = new FilterExtractor("Test", user, "?jql=project=TEST");
		assertEquals(0, extractor.getAllElementsMatchingQuery().size());
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
		assertEquals(0, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	public void testGetGraphsMatchingQueryNull() {
		FilterExtractor extractor = new FilterExtractor("Test", user, "?jql=project=TEST");
		assertEquals(0, extractor.getGraphsMatchingQuery(null).size());
	}

	@Test
	public void testGetGraphsMatchingQueryEmpty() {
		FilterExtractor extractor = new FilterExtractor("Test", user, "?jql=project=TEST");
		assertEquals(0, extractor.getGraphsMatchingQuery("").size());
	}

	@Test
	public void testGetGraphsMatchingQueryFilled() {
		FilterExtractor extractor = new FilterExtractor("Test", user, "?jql=project=TEST");
		assertEquals(0, extractor.getGraphsMatchingQuery("Test").size());
	}
	
	@Test
	public void testGetFilterSettings() {
		FilterExtractor extractor = new FilterExtractor("Test", user, "?jql=project=TEST");
		assertEquals("?jql=project=TEST", extractor.getFilterSettings().getSearchString());
	}
}
