package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestVisTimeLine extends TestSetUp {
	private VisTimeLine visTimeLine;

	@Before
	public void setUp() {
		init();
		Set<KnowledgeElement> elements = new HashSet<KnowledgeElement>();
		KnowledgeElement element = new KnowledgeElement(JiraIssues.getTestJiraIssues().get(0));
		elements.add(element);
		visTimeLine = new VisTimeLine(elements);
	}

	@Test
	public void testConstructorElementsNull() {
		VisTimeLine timeLine = new VisTimeLine((Set<KnowledgeElement>) null);
		assertEquals(0, timeLine.getTimeLineNodes().size());
	}

	@Test
	public void testConstructorElementsFilled() {
		assertEquals(1, visTimeLine.getTimeLineNodes().size());
	}

	@Test
	public void testAddElement() {
		KnowledgeElement element = new KnowledgeElement(JiraIssues.getTestJiraIssues().get(1));
		visTimeLine.addElement(element);
		assertEquals(2, visTimeLine.getTimeLineNodes().size());
	}

	@Test
	public void testGetGroups() {
		assertEquals(1, visTimeLine.getGroups().size());
	}

	@Test
	public void testSetGroupSet() {
		HashSet<VisTimeLineGroup> groups = new HashSet<VisTimeLineGroup>();
		visTimeLine.setGroups(groups);
		assertEquals(0, visTimeLine.getGroups().size());
	}

	@Test
	public void testConstructorUserValidFilterSettingsValid() {
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		VisTimeLine visTimeLine = new VisTimeLine(user, filterSettings, true, true);
		assertNotNull(visTimeLine);
		assertEquals(1, visTimeLine.getGroups().size());
		assertEquals(18, visTimeLine.getTimeLineNodes().size());
	}

	@Test
	public void testConstructorUserNullFilterSettingsNull() {
		VisTimeLine visTimeLine = new VisTimeLine(null, null, false, false);
		assertNotNull(visTimeLine);
		assertEquals(0, visTimeLine.getGroups().size());
		assertEquals(0, visTimeLine.getTimeLineNodes().size());
	}
}
