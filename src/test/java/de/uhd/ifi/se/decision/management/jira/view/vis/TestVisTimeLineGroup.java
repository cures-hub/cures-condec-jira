package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestVisTimeLineGroup extends TestSetUp {
	private VisTimeLineGroup visTimeLineGroup;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.BLACK_HEAD.getApplicationUser();
		visTimeLineGroup = new VisTimeLineGroup(user);
	}

	@Test
	public void testGetId() {
		assertEquals(user.getId(), visTimeLineGroup.getId(), 0.0);
	}

	@Test
	public void testSetId() {
		visTimeLineGroup.setId(12132);
		assertEquals(12132, visTimeLineGroup.getId());
	}

	@Test
	public void testGetContent() {
		assertEquals(user.getName(), visTimeLineGroup.getContent());
	}

	@Test
	public void testSetContent() {
		visTimeLineGroup.setContent("TestContent");
		assertEquals("TestContent", visTimeLineGroup.getContent());
	}
}
