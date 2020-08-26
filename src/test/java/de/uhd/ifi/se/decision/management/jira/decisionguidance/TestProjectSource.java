package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.*;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
