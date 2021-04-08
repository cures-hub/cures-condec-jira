package de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;

public class TestProjectSource extends TestSetUp {

	ProjectSource projectSource;

	@Before
	public void setUp() {
		init();
		projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
	}

	@Test
	public void testConstructorWithProject() {
		ProjectSource projectSourceFromProjectObject = new ProjectSource(
				new DecisionKnowledgeProject(JiraProjects.getTestProject()));
		assertEquals(projectSource.getName(), projectSourceFromProjectObject.getName());
	}

	@Test
	public void testSetAndGetName() {
		projectSource.setName("TEST");
		assertEquals("TEST", projectSource.getName());
	}

	@Test
	public void testSetAndGetIcon() {
		projectSource.setIcon("aui-iconfont-jira");
		assertEquals("aui-iconfont-jira", projectSource.getIcon());
	}

	@Test
	public void testSetAndGetProjectKey() {
		projectSource.setProjectKey("TEST-OTHER");
		assertEquals("TEST-OTHER", projectSource.getProjectKey());
	}

	@Test
	public void testIsActivated() {
		projectSource.setActivated(true);
		assertTrue(projectSource.isActivated());
	}
}
