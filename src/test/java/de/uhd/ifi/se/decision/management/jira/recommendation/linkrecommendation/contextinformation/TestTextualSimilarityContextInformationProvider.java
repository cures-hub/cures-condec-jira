package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestTextualSimilarityContextInformationProvider extends TestSetUp {

	private TextualSimilarityContextInformationProvider textualSimilarityContextInformationProvider;

	@Before
	public void setUp() {
		init();
		textualSimilarityContextInformationProvider = new TextualSimilarityContextInformationProvider();
	}

	@Test
	public void testAssessRelation() {
		RecommendationScore score = textualSimilarityContextInformationProvider
				.assessRelation(KnowledgeElements.getAlternative(), KnowledgeElements.getProArgument());
		assertEquals(0.44, score.getValue(), 0.1);
		assertEquals("TextualSimilarityContextInformationProvider (JaroWinklerDistance)", score.getExplanation());
	}

	@Test
	public void testCalculateSimilarityValid() {
		assertEquals(0.96, textualSimilarityContextInformationProvider.calculateSimilarity("MySQL", "MySQL@en"), 0.1);
		assertEquals(1.0, textualSimilarityContextInformationProvider
				.calculateSimilarity("How can we implement the feature?", "How to implement the feature?"), 0.0);
	}

	@Test
	public void testCalculateSimilarityNull() {
		assertEquals(0, textualSimilarityContextInformationProvider.calculateSimilarity(null, ""), 0);
		assertEquals(0, textualSimilarityContextInformationProvider.calculateSimilarity("", null), 0);
	}

	@Test
	public void testGetAndSetWeightValue() {
		assertEquals(1, textualSimilarityContextInformationProvider.getWeightValue(), 0.0);
		textualSimilarityContextInformationProvider.setWeightValue(42);
		assertEquals(42, textualSimilarityContextInformationProvider.getWeightValue(), 0.0);
	}

	@Test
	public void testExplanation() {
		assertNotNull(textualSimilarityContextInformationProvider.getExplanation());
	}

	@Test
	public void testDescription() {
		assertNotNull(textualSimilarityContextInformationProvider.getDescription());
	}
}
