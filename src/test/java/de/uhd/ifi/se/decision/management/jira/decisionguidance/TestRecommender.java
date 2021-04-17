package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRecommender extends TestSetUp {

	private ProjectSource projectSource;
	private RDFSource rdfSource;

	@Before
	public void setUp() {
		init();
		// search for solutions in the same project
		projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), true);
		rdfSource = new RDFSource();
	}

	@Test
	@NonTransactional
	public void testAddToKnowledgeGraph() {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		KnowledgeElement decisionProblem = KnowledgeElements.getSolvedDecisionProblem();

		List<Recommendation> recommendations = Recommender.getAllRecommendations("TEST", knowledgeSources,
				decisionProblem, "");
		assertEquals(2, recommendations.size());

		KnowledgePersistenceManager manager = KnowledgePersistenceManager.getOrCreate("TEST");
		Recommender.addToKnowledgeGraph(KnowledgeElements.getTestKnowledgeElement(),
				JiraUsers.SYS_ADMIN.getApplicationUser(), recommendations);

		assertEquals(20, manager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testRecommenderProperties() {
		Recommender<?> recommender = Recommender.getRecommenderForKnowledgeSource("TEST", projectSource);
		assertEquals("TEST", recommender.getProjectKey());
		assertEquals(projectSource, recommender.getKnowledgeSource());
	}
}
