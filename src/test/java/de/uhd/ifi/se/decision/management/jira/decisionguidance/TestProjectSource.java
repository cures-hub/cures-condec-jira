package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;

public class TestProjectSource extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testSource() {

		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey());

		List<KnowledgeElement> recommendations = projectSource.getResults("feature");

		assertEquals(0, recommendations.size());
		// assertEquals("We could do it like this!",
		// recommendations.get(0).getSummary());
		// assertEquals(KnowledgeType.ALTERNATIVE, recommendations.get(0).getType());

	}

}
