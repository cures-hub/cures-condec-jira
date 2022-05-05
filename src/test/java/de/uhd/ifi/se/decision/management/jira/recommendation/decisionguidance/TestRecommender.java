package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

import static org.junit.Assert.*;

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
		System.out.println("START ADD TO KNOWLEDGE GRAPH");
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		KnowledgeElement decisionProblem = KnowledgeElements.getSolvedDecisionProblem();
		assertNotNull(decisionProblem);
		assertNotNull(decisionProblem.getJiraIssue());
		System.out.println("CRITICAL PART...");
		List<Recommendation> recommendations = Recommender.getAllRecommendations("TEST", knowledgeSources,
				decisionProblem, "");
		assertEquals(2, recommendations.size());

		KnowledgePersistenceManager manager = KnowledgePersistenceManager.getInstance("TEST");
		Recommender.addToKnowledgeGraph(decisionProblem, JiraUsers.SYS_ADMIN.getApplicationUser(), recommendations);

		assertTrue(manager.getKnowledgeElements().size() > 17);
		System.out.println("FINISH ADD TO KNOWLEDGE GRAPH");
	}

	@Test
	@NonTransactional
	public void testRecommenderProperties() {
		Recommender<?> recommender = Recommender.getRecommenderForKnowledgeSource("TEST", projectSource);
		assertEquals(projectSource, recommender.getKnowledgeSource());
		recommender.setProjectKey("TEST-CHANGE");
		assertEquals("TEST-CHANGE", recommender.getProjectKey());
	}

	@Test
	@NonTransactional
	public void testGetRecommendations() {
		Recommender<?> recommender = Recommender.getRecommenderForKnowledgeSource("TEST", projectSource);
		List<ElementRecommendation> recommendations = recommender.getRecommendations((KnowledgeElement) null);
		assertEquals(0, recommendations.size());
		assertEquals(projectSource, recommender.getKnowledgeSource());
		recommender.setProjectKey("TEST-CHANGE");
		assertEquals("TEST-CHANGE", recommender.getProjectKey());
	}

	@Test
	@NonTransactional
	public void testGetRecommendationsWithDiscardedStatus() {
		Recommender<?> recommender = Recommender.getRecommenderForKnowledgeSource("TEST", projectSource);
		KnowledgeSource source = new ProjectSource("TEST", true);
		ElementRecommendation recommendationA = new ElementRecommendation("RecommendationA", source, "TESTURL");
		ElementRecommendation recommendationB = new ElementRecommendation("RecommendationB", source, "TESTURL");
		ElementRecommendation recommendationC = new ElementRecommendation("RecommendationC", source, "TESTURL");
		List<ElementRecommendation> recommendations = new ArrayList<>();
		List<ElementRecommendation> discardedRecommendations = new ArrayList<>();
		recommendations.add(recommendationA);
		recommendations.add(recommendationB);
		discardedRecommendations.add(recommendationB);
		discardedRecommendations.add(recommendationC);
		recommendations = recommender.getRecommendationsWithDiscardedStatus(recommendations, discardedRecommendations);
		assertEquals(2, recommendations.size());
		assertEquals("RecommendationA", recommendations.get(0).getSummary());
		assertEquals("RecommendationB", recommendations.get(1).getSummary());
		assertFalse(recommendations.get(0).isDiscarded());
		assertTrue(recommendations.get(1).isDiscarded());
	}
}
