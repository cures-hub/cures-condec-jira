package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

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

		Recommendation recommendations = projectSource.getResults("feature");

		assertEquals(1, recommendations.getRecommendations().size());
		assertEquals("We could do it like this!", recommendations.getRecommendations().get(0).getSummary());
		assertEquals(KnowledgeType.ALTERNATIVE, recommendations.getRecommendations().get(0).getType());
		assertEquals("ProjectSource", recommendations.getKnowledgeSourceName());

	}


}
