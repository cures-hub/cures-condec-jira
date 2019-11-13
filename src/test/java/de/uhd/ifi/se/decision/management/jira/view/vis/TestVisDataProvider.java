package de.uhd.ifi.se.decision.management.jira.view.vis;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class TestVisDataProvider extends TestSetUp {

	private ApplicationUser user;
	private FilterSettings filterSettings;
	private VisDataProvider provider;
	private List<DecisionKnowledgeElement> allDecisions;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.BLACK_HEAD.getApplicationUser();
		filterSettings = new FilterSettingsImpl("TEST", "");
		provider = new VisDataProvider(user, filterSettings);
		allDecisions = new ArrayList<>();
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
	public void testConstUserFilterFilledElementsFilled() {
		assertNotNull(new VisDataProvider(user, filterSettings, allDecisions).getVisGraph());
	}

	@Test
	public void testConstUserFilterFilledNoElements() {
		assertNull(new VisDataProvider(user, filterSettings, null).getVisGraph());
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
