package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.RDFSource;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestSimpleRecommender extends TestSetUp {

	private ProjectSource projectSource;
	private RDFSource rdfSource;

	@Before
	public void setUp() {
		init();
		projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true); //search for solutions in the same project
		rdfSource = new RDFSource(JiraProjects.getTestProject().getKey());
	}

	@Test
	public void testSimpleRecommender() {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		BaseRecommender simpleRecommender = new SimpleRecommender("feature");
		simpleRecommender.addKnowledgeSource(knowledgeSources);
		List<Recommendation> recommendations = simpleRecommender.getRecommendation();

		assertNotEquals(null, recommendations);
	}

	@Test
	public void testSimpleRecommenderNoKnowledgeSources() {
		BaseRecommender simpleRecommender = new SimpleRecommender("feature");

		List<Recommendation> recommendations = simpleRecommender.getRecommendation();

		assertEquals(0, recommendations.size());
		assertEquals(0, simpleRecommender.getKnowledgeSources().size());
	}

	@Test
	public void testSimpleRecommenderGetKnowledgeSources() {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		BaseRecommender simpleRecommender = new SimpleRecommender("feature");
		simpleRecommender.addKnowledgeSource(knowledgeSources);
		assertEquals(2, simpleRecommender.getKnowledgeSources().size());
	}

	@Test
	public void testSimpleRecommenderAddKnowledgeSources() {
		BaseRecommender simpleRecommender = new SimpleRecommender("feature");
		simpleRecommender.addKnowledgeSource(projectSource);
		simpleRecommender.addKnowledgeSource(rdfSource);
		assertEquals(2, simpleRecommender.getKnowledgeSources().size());
	}


	@Test
	public void testRecommenderEvaluation() {
		BaseRecommender simpleRecommender = new SimpleRecommender("feature");
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);
		simpleRecommender.addKnowledgeSourceForEvaluation(knowledgeSources, "TEST");
		assertEquals(1, simpleRecommender.getKnowledgeSources().size());
		assertEquals(2, simpleRecommender.evaluate().size());
	}

	@Test
	public void testRemoveDuplicates() {

		List<Recommendation> recommendations = new ArrayList<>();

		Recommendation recommendation = new Recommendation("SOURCE A", "SUMMARY 1", 0, "");
		Recommendation recommendation2 = new Recommendation("SOURCE A", "SUMMARY 1", 0, "");

		recommendations.add(recommendation);
		recommendations.add(recommendation2);

		BaseRecommender recommender = new SimpleRecommender("");

		assertEquals(1, recommender.removeDuplicated(recommendations).size());
	}

	@Test
	public void testRemoveDuplicatesValid() {

		List<Recommendation> recommendations = new ArrayList<>();

		Recommendation recommendation = new Recommendation("SOURCE A", "SUMMARY 1", 0, "");
		Recommendation recommendation2 = new Recommendation("SOURCE B", "SUMMARY 1", 0, "");

		recommendations.add(recommendation);
		recommendations.add(recommendation2);

		BaseRecommender recommender = new SimpleRecommender("");

		assertEquals(2, recommender.removeDuplicated(recommendations).size());
	}

}
