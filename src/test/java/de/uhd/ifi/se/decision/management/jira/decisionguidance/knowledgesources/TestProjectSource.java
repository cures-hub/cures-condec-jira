package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.ProjectSourceSubstringAlgorithm;
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
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey());
		projectSource.setName("ProjectSource");

		List<Recommendation> recommendations = projectSource.getResults("feature");

		assertEquals(1, recommendations.size());
		assertEquals("We could do it like this!", recommendations.get(0).getRecommendations());
		assertEquals("ProjectSource", recommendations.get(0).getKnowledgeSourceName());
	}

	@Test
	public void testDefaultAlgorithm() { //default is SUBSTRING
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey());
		projectSource.setName("ProjectSource");
		projectSource.setKnowledgeSourceAlgorithmType(null);

		List<Recommendation> recommendations = projectSource.getResults("feature");

		assertEquals(1, recommendations.size());
		assertEquals("We could do it like this!", recommendations.get(0).getRecommendations());
		assertEquals("ProjectSource", recommendations.get(0).getKnowledgeSourceName());

	}

	@Test
	public void testScore() {
		ProjectSourceSubstringAlgorithm algorithm = new ProjectSourceSubstringAlgorithm("TEST", "Test Source", "feature");

		assertEquals(1, algorithm.getResults().size());
		assertEquals(100, algorithm.getResults().get(0).getScore());
	}


}
