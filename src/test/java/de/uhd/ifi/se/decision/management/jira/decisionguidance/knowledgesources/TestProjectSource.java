package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
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
		assertEquals("We could do it like this!", recommendations.get(0).getRecommendations().getSummary());
		assertEquals(KnowledgeType.ALTERNATIVE, recommendations.get(0).getRecommendations().getType());
		assertEquals("ProjectSource", recommendations.get(0).getKnowledgeSourceName());
	}


}
