package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettings;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.recommendation.DiscardedRecommendationPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestContextInformation extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testGetLinkRecommendations() {
		ContextInformation contextInformation = new ContextInformation(KnowledgeElements.getDecision());
		List<Recommendation> linkRecommendations = contextInformation.getLinkRecommendations();
		assertTrue(linkRecommendations.size() > 10);
	}

	@Test
	public void testFilterLinkRecommendationsByScore() {
		LinkRecommendationConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
				.getLinkRecommendationConfiguration("TEST");
		linkSuggestionConfiguration.setMinProbability(0.9);
		ConfigPersistenceManager.saveLinkRecommendationConfiguration("TEST", linkSuggestionConfiguration);

		ContextInformation contextInformation = new ContextInformation(KnowledgeElements.getDecision());
		List<Recommendation> linkRecommendations = contextInformation.getLinkRecommendations();
		assertEquals(9, linkRecommendations.size());
	}

	@Test
	@NonTransactional
	public void testLinkRecommendationsNotGeneratedForIrrelevantPartsOfText() {
		JiraIssues.getIrrelevantSentence();
		ContextInformation contextInformation = new ContextInformation(KnowledgeElements.getDecision());
		List<Recommendation> linkRecommendations = contextInformation.getLinkRecommendations();
		assertTrue(linkRecommendations.size() >= 12);
	}

	@Test
	@NonTransactional
	public void testMarkRecommendationAsDiscarded() {
		LinkRecommendation recommendation = new LinkRecommendation(KnowledgeElements.getDecision(),
				KnowledgeElements.getOtherWorkItem());
		DiscardedRecommendationPersistenceManager.saveDiscardedRecommendation(recommendation);
		ContextInformation contextInformation = new ContextInformation(KnowledgeElements.getDecision());
		List<Recommendation> linkRecommendations = contextInformation.getLinkRecommendations();
		linkRecommendations.stream().filter(rec -> rec.isDiscarded()).count();
		assertTrue(linkRecommendations.stream().filter(rec -> rec.isDiscarded()).count() > 0);
	}

	@After
	public void tearDown() {
		// reset plugin settings to default settings
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}
}
