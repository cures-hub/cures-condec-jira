package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestVisDataProvider extends TestSetUp {

	private ApplicationUser user;
	private FilterSettings filterSettings;
	private List<DecisionKnowledgeElement> allDecisions;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.BLACK_HEAD.getApplicationUser();
		filterSettings = new FilterSettingsImpl("TEST", "");
		allDecisions = new ArrayList<>();
	}

	@Test
	public void testConstUserNullFilterNull() {
		assertNotNull(new VisGraph((ApplicationUser) null, (FilterSettings) null));
	}

	@Test
	public void testConstUserFilledFilterNull() {
		assertNotNull(new VisGraph(user, (FilterSettings) null));
	}

	@Test
	public void testConstUserNullFilterFilled() {
		assertNotNull(new VisGraph((ApplicationUser) null, filterSettings));
	}

	@Test
	public void testConstUserFilterFilledElementsFilled() {
		assertNotNull(new VisGraph(user, filterSettings, allDecisions));
	}

	@Test
	public void testConstUserFilterFilledNoElements() {
		assertNotNull(new VisGraph(user, filterSettings, null));
	}

	@Test
	public void testConstUserFilledFilterFilled() {
		assertNotNull(new VisGraph(user, filterSettings));
	}

}
