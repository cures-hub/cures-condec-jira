package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.score.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

public class TestKeywordBasedRecommender extends TestSetUp {

	private ProjectSource projectSource;
	private RDFSource rdfSource;

	@Before
	public void setUp() {
		init();
		projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true); // search for solutions
		// in the same
		// project
		rdfSource = new RDFSource(JiraProjects.getTestProject().getKey());
	}

	@Test
	public void testKeywordbasedRecommender() {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		BaseRecommender<String> recommender = new KeywordBasedRecommender("feature");
		recommender.addKnowledgeSource(knowledgeSources);
		List<Recommendation> recommendations = recommender.getRecommendations();

		assertNotEquals(null, recommendations);
	}

	@Test
	public void testKeywordbasedRecommenderConstrutor() {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		BaseRecommender<String> recommender = new KeywordBasedRecommender("feature", knowledgeSources);
		assertNotEquals(null, recommender);
	}

	@Test
	public void testSimpleRecommenderNoKnowledgeSources() {
		BaseRecommender<String> simpleRecommender = new KeywordBasedRecommender("feature");

		List<Recommendation> recommendations = simpleRecommender.getRecommendations();

		assertEquals(0, recommendations.size());
		assertEquals(0, simpleRecommender.getKnowledgeSources().size());
	}

	@Test
	public void testSimpleRecommenderGetKnowledgeSources() {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		BaseRecommender<String> simpleRecommender = new KeywordBasedRecommender("How can we implement the feature");
		simpleRecommender.addKnowledgeSource(knowledgeSources);
		assertEquals(2, simpleRecommender.getKnowledgeSources().size());
	}

	@Test
	public void testSimpleRecommenderAddKnowledgeSources() {
		BaseRecommender<String> simpleRecommender = new KeywordBasedRecommender("How can we implement the feature");
		simpleRecommender.addKnowledgeSource(projectSource);
		simpleRecommender.addKnowledgeSource(rdfSource);
		assertEquals(2, simpleRecommender.getKnowledgeSources().size());
	}

	@Test
	public void testRemoveDuplicates() {

		List<Recommendation> recommendations = new ArrayList<>();

		KnowledgeSource knowledgeSource = new ProjectSource("TEST", "Source A", true);

		Recommendation recommendation = new Recommendation(knowledgeSource, "SUMMARY 1", new RecommendationScore(0, ""),
				"");
		Recommendation recommendation2 = new Recommendation(knowledgeSource, "SUMMARY 1",
				new RecommendationScore(0, ""), "");

		recommendations.add(recommendation);
		recommendations.add(recommendation2);

		BaseRecommender<String> recommender = new KeywordBasedRecommender("");

		assertEquals(1, recommender.removeDuplicated(recommendations).size());
	}

	@Test
	public void testRemoveDuplicatesValid() {

		List<Recommendation> recommendations = new ArrayList<>();

		KnowledgeSource knowledgeSourceA = new ProjectSource("TEST", "Source A", true);
		KnowledgeSource knowledgeSourceB = new ProjectSource("TEST", "Source B", true);

		Recommendation recommendation = new Recommendation(knowledgeSourceA, "SUMMARY 1",
				new RecommendationScore(0, ""), "");
		Recommendation recommendation2 = new Recommendation(knowledgeSourceB, "SUMMARY 1",
				new RecommendationScore(0, ""), "");

		recommendations.add(recommendation);
		recommendations.add(recommendation2);

		BaseRecommender<String> recommender = new KeywordBasedRecommender("");

		assertEquals(2, recommender.removeDuplicated(recommendations).size());
	}

	@Test
	public void testRecommenderType() {
		assertEquals(RecommenderType.KEYWORD, RecommenderType.getTypeByString("KEYWORD"));
		assertEquals(RecommenderType.ISSUE, RecommenderType.getTypeByString("ISSUE"));
		assertEquals(RecommenderType.KEYWORD, RecommenderType.getTypeByString("INVALID"));

		assertEquals(3, RecommenderType.getRecommenderTypes().size());
	}
}