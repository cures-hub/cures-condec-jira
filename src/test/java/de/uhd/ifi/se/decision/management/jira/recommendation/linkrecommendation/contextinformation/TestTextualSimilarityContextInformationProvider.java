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
	public void testAssessRelationDifferentKnowledgeTypes() {
		RecommendationScore score = textualSimilarityContextInformationProvider
				.assessRelation(KnowledgeElements.getAlternative(), KnowledgeElements.getProArgument());
		assertEquals(0.44, score.getValue(), 0.1);
		assertNotNull(score.getExplanation());
	}

	@Test
	public void testAssessRelationPotentialDuplicate() {
		assertEquals(1.0, textualSimilarityContextInformationProvider
				.assessRelation("How can we implement the feature?", "How to implement the feature?").getValue(), 0.0);
	}

	@Test
	public void testCalculateSimilarityOneWord() {
		assertEquals(0.96, textualSimilarityContextInformationProvider.calculateSimilarity("MySQL", "MySQL@en"), 0.1);
	}

	@Test
	public void testCalculateSimilarityVerySimilarSentence() {
		assertEquals(1.0, textualSimilarityContextInformationProvider
				.calculateSimilarity("How can we implement the feature?", "How to implement the feature?"), 0.0);
	}

	@Test
	public void testCalculateSimilarityRelatedSentence() {
		String firstSentence = "Which data do we export about the players?";
		String secondSentence = "We could export it as a .txt file.";
		assertEquals(0.9,
				textualSimilarityContextInformationProvider.calculateSimilarity(firstSentence, secondSentence), 0.01);
	}

	@Test
	public void testCalculateSimilarityUnrelatedSentence() {
		String firstSentence = "In which file format do we export the team data?";
		String secondSentence = "We could filter the exercises in the frontend";
		String thirdSentence = "We could export it as a .txt file.";
		String forthSentence = "Which data do we export about the players?";
		assertEquals(0.753,
				textualSimilarityContextInformationProvider.calculateSimilarity(firstSentence, secondSentence), 0.01);
		assertEquals(0.83,
				textualSimilarityContextInformationProvider.calculateSimilarity(firstSentence, thirdSentence), 0.01);
		assertEquals(0.83,
				textualSimilarityContextInformationProvider.calculateSimilarity(firstSentence, forthSentence), 0.01);
	}

	@Test
	public void testCalculateSimilarityNull() {
		assertEquals(0, textualSimilarityContextInformationProvider.calculateSimilarity(null, ""), 0);
		assertEquals(0, textualSimilarityContextInformationProvider.calculateSimilarity("", null), 0);
	}

	@Test
	public void testCalculateSimilarityEmptyText() {
		assertEquals(0, textualSimilarityContextInformationProvider.calculateSimilarity("", ""), 0);
	}

	@Test
	public void testGetAndSetWeightValue() {
		assertEquals(2, textualSimilarityContextInformationProvider.getWeightValue(), 0.0);
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
