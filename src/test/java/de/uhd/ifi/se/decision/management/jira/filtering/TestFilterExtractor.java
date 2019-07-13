package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.view.treant.TestTreant;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestTreant.AoSentenceTestDatabaseUpdater.class)
public class TestFilterExtractor extends TestSetUpWithIssues {

	private ApplicationUser user;
	private EntityManager entityManager;

	@Before
	public void setUp() {
		initialization();
		user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
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
