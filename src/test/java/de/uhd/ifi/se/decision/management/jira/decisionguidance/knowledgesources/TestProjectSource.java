package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSourceInputKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSourceInputString;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestProjectSource extends TestSetUp {


	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testSource() {
		KnowledgeSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
		projectSource.setName("TEST");

		projectSource.setRecommenderType(RecommenderType.KEYWORD);
		List<Recommendation> recommendations = projectSource.getResults("How can we implement the feature");

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
	public void testGetInputMethod() {
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
		projectSource.setRecommenderType(RecommenderType.KEYWORD);
		assertEquals(ProjectSourceInputString.class, projectSource.getInputMethod().getClass());
		projectSource.setRecommenderType(RecommenderType.ISSUE);
		assertEquals(ProjectSourceInputKnowledgeElement.class, projectSource.getInputMethod().getClass());
	}

	@Test
	public void testKnowledgeSource() {
		KnowledgeSource knowledgeSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
		assertEquals("TEST", knowledgeSource.getName());
		assertEquals("TEST", knowledgeSource.getProjectKey());
		assertEquals(true, knowledgeSource.isActivated());
		knowledgeSource.setActivated(false);
		assertEquals(false, knowledgeSource.isActivated());
	}
}
