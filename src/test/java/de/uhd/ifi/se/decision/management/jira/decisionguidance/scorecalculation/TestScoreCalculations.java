package de.uhd.ifi.se.decision.management.jira.decisionguidance.scorecalculation;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.ProjectScoreCalculator;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.ScoreCalculator;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.ScoreCalculatorFactory;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
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
		scoreCalculator = new ProjectScoreCalculator();
		KnowledgeElement rootIssue = KnowledgeElements.getTestKnowledgeElements().get(3);

		List<String> keywords = new ArrayList<>();

		keywords.add("feature");
		assertEquals(100, scoreCalculator.calculateScore(keywords, rootIssue));

		keywords.add("feature otherword");
		assertEquals(50, scoreCalculator.calculateScore(keywords, rootIssue));
	}


}
