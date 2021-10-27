package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestProjectSourceRecommender extends TestSetUp {

	private ProjectSourceRecommender projectSourceRecommender;

	@Before
	public void setUp() {
		init();
		ProjectSource projectSource = new ProjectSource("TEST", true);
		projectSourceRecommender = new ProjectSourceRecommender("TEST", projectSource);
	}

	@Test
	public void testKeywordsOnly() {
		List<Recommendation> recommendations = projectSourceRecommender
				.getRecommendations("How can we implement the feature?");
		assertEquals(2, recommendations.size());
	}

	@Test
	public void testKeywordsNull() {
		List<Recommendation> recommendations = projectSourceRecommender.getRecommendations((String) null);
		assertEquals(0, recommendations.size());
	}

	@Test
	public void testDecisionProblem() {
		KnowledgeElement decisionProblem = KnowledgeElements.getSolvedDecisionProblem();
		List<Recommendation> recommendations = projectSourceRecommender.getRecommendations(decisionProblem);
		assertEquals(2, recommendations.size());
	}

	@Test
	public void testDecisionProblemAndKeywords() {
		KnowledgeElement decisionProblem = KnowledgeElements.getSolvedDecisionProblem();
		List<Recommendation> recommendations = projectSourceRecommender
				.getRecommendations("How can we implement the feature?", decisionProblem);
		assertEquals(2, recommendations.size());
	}

	@Test
	@NonTransactional
	public void testWithArgument() {
		KnowledgeElement alternative = KnowledgeElements.getDecision();
		KnowledgeElement con = JiraIssues.addElementToDataBase(42, KnowledgeType.CON);
		KnowledgePersistenceManager.getInstance("TEST").insertLink(alternative, con,
				JiraUsers.SYS_ADMIN.getApplicationUser());

		KnowledgeElement decision = KnowledgeElements.getDecision();
		KnowledgeElement pro = JiraIssues.addElementToDataBase(123, KnowledgeType.PRO);
		KnowledgePersistenceManager.getInstance("TEST").insertLink(decision, pro,
				JiraUsers.SYS_ADMIN.getApplicationUser());

		KnowledgeElement decisionProblem = KnowledgeElements.getSolvedDecisionProblem();

		List<Recommendation> recommendations = projectSourceRecommender.getRecommendations(decisionProblem);
		assertFalse(recommendations.isEmpty());
		assertFalse(((ElementRecommendation) recommendations.get(0)).getArguments().isEmpty());
		assertFalse(((ElementRecommendation) recommendations.get(1)).getArguments().isEmpty());
	}
}
