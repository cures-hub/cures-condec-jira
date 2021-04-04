package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSourceInputKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSourceInputString;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

public class TestProjectSource extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testSource() {
		KnowledgeSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
		projectSource.setName("TEST");

		List<Recommendation> recommendations = InputMethod.getKeywordBasedIn(projectSource)
				.getRecommendations("How can we implement the feature");

		assertEquals(2, recommendations.size());
		assertEquals("TEST", recommendations.get(0).getKnowledgeSourceName());

		assertEquals("aui-iconfont-jira", projectSource.getIcon());
		projectSource.setIcon("TEST");
		assertEquals("TEST", projectSource.getIcon());
	}

	@Test
	public void testStringInput() {
		ProjectSourceInputString input = new ProjectSourceInputString();
		input.setKnowledgeSource(new ProjectSource("TEST", "TEST", true));
		List<Recommendation> recommendations = input.getRecommendations("How can we implement the feature");
		assertEquals(2, recommendations.size());
	}

	@Test
	public void testStringKnowledgeElement() {
		ProjectSourceInputKnowledgeElement input = new ProjectSourceInputKnowledgeElement();
		input.setKnowledgeSource(new ProjectSource("TEST", "TEST", true));
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setId(123);
		knowledgeElement.setSummary("How can we implement the feature");
		List<Recommendation> recommendations = input.getRecommendations(knowledgeElement);
		assertEquals(2, recommendations.size());
	}

	@Test
	public void testKnowledgeSource() {
		ProjectSource knowledgeSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
		assertEquals("TEST", knowledgeSource.getName());
		assertEquals("TEST", knowledgeSource.getProjectKey());
		assertEquals(true, knowledgeSource.isActivated());
		knowledgeSource.setActivated(false);
		assertEquals(false, knowledgeSource.isActivated());
	}
}
