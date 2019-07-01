package de.uhd.ifi.se.decision.management.jira.filtering;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import org.junit.Before;
import org.junit.Test;

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
		FilterExtractor extractor = new FilterExtractor(null, null,null);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringFilledNullNull() {
		FilterExtractor extractor = new FilterExtractor("TEST", null,null);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringNullFilledNull() {
		FilterExtractor extractor = new FilterExtractor(null, user ,null);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringNullNullEmpty() {
		FilterExtractor extractor = new FilterExtractor(null, user ,"");
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringNullNullFilled() {
		FilterExtractor extractor = new FilterExtractor(null, user ,null);
		assertNull(extractor.getFilteredDecisions());
	}
}
