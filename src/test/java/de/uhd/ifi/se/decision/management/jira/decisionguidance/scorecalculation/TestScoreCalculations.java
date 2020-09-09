package de.uhd.ifi.se.decision.management.jira.decisionguidance.scorecalculation;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.ProjectScoreCalculator;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.ScoreCalculator;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.ScoreCalculatorFactory;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.BaseRecommender;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.SimpleRecommender;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestScoreCalculations extends TestSetUp {

	ScoreCalculator scoreCalculator;
	ScoreCalculatorFactory scoreCalculatorFactory;

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testScoreCalculatorFactory() {
		scoreCalculatorFactory = new ScoreCalculatorFactory(KnowledgeSourceType.PROJECT);
		scoreCalculator = scoreCalculatorFactory.createScoreCalculator();
		assertNotEquals(null, scoreCalculator);
	}

	@Test
	public void testCalculateProjectScore() {
		/*
		scoreCalculator = new ProjectScoreCalculator();

		KnowledgeElement rootIssue = KnowledgeElements.getTestKnowledgeElements().get(3);
		KnowledgeElement alternative =KnowledgeElements.getTestKnowledgeElements().get(5);


		List<String> keywords = new ArrayList<>();

		keywords.add("feature");
		assertEquals(100, scoreCalculator.calculateScore(keywords, alternative));

		keywords.add("feature otherword");
		assertEquals(50, scoreCalculator.calculateScore(keywords, alternative));
		*/

	}


}
