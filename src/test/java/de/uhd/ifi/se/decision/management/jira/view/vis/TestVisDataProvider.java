package de.uhd.ifi.se.decision.management.jira.view.vis;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class TestVisDataProvider extends TestSetUp {

	private ApplicationUser user;
	private FilterSettings filterSettings;
	private VisDataProvider provider;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.BLACK_HEAD.getApplicationUser();
		filterSettings = new FilterSettingsImpl("TEST", "");
		provider = new VisDataProvider(user, filterSettings);
	}

	@Test
	public void testConstUserNullFilterNull() {
		assertNull(new VisDataProvider((ApplicationUser) null, (FilterSettings) null).getVisGraph());
	}

	@Test
	public void testConstUserFilledFilterNull() {
		assertNull(new VisDataProvider(user, (FilterSettings) null).getVisGraph());
	}

	@Test
	public void testConstUserNullFilterFilled() {
		assertNull(new VisDataProvider((ApplicationUser) null, filterSettings).getVisGraph());
	}

	@Test
	public void testConstUserFilledFilterFilled() {
		assertNotNull(provider.getVisGraph());
	}

	@Test
	public void testGetVisTimeLine(){
		assertNotNull(provider.getTimeLine());
	}
}
