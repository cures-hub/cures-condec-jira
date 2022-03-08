package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.recommendation.DiscardedRecommendationPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestContextInformation extends TestSetUp {

	private LinkRecommendationConfiguration linkRecommendationConfiguration;

	@Before
	public void setUp() {
		linkRecommendationConfiguration = new LinkRecommendationConfiguration();
		linkRecommendationConfiguration.setMinProbability(0.3);
		init();
	}

	@Test
	public void testGetLinkRecommendations() {
		ContextInformation contextInformation = new ContextInformation(KnowledgeElements.getDecision(),
				linkRecommendationConfiguration);
		List<LinkRecommendation> linkRecommendations = contextInformation.getLinkRecommendations();
		assertTrue(linkRecommendations.size() > 4);

		linkRecommendationConfiguration.setMaxRecommendations(1);
		contextInformation = new ContextInformation(KnowledgeElements.getDecision(), linkRecommendationConfiguration);
		linkRecommendations = contextInformation.getLinkRecommendations();

		assertTrue(linkRecommendations.size() == 1);
	}

	@Test
	public void testGetLinkRecommendationsNegativeRuleWeight() {
		List<ContextInformationProvider> contextInformationProviders = new LinkedList<ContextInformationProvider>();
		contextInformationProviders.add(new TextualSimilarityContextInformationProvider());

		linkRecommendationConfiguration.setContextInformationProviders(contextInformationProviders);
		linkRecommendationConfiguration.getContextInformationProviders().get(0).setWeightValue(-1.0f);
		ContextInformation contextInformation = new ContextInformation(KnowledgeElements.getDecision(),
				linkRecommendationConfiguration);
		List<LinkRecommendation> linkRecommendations = contextInformation.getLinkRecommendations();

		assertFalse(linkRecommendations.isEmpty());
	}

	@Test
	public void testFilterLinkRecommendationsByScore() {
		linkRecommendationConfiguration.setMinProbability(0.5);
		ContextInformation contextInformation = new ContextInformation(KnowledgeElements.getDecision(),
				linkRecommendationConfiguration);
		List<LinkRecommendation> linkRecommendations = contextInformation.getLinkRecommendations();
		assertTrue(linkRecommendations.size() > 2);
	}

	@Test
	@NonTransactional
	public void testLinkRecommendationsNotGeneratedForIrrelevantPartsOfText() {
		JiraIssues.getIrrelevantSentence();
		ContextInformation contextInformation = new ContextInformation(KnowledgeElements.getDecision(),
				linkRecommendationConfiguration);
		List<LinkRecommendation> linkRecommendations = contextInformation.getLinkRecommendations();
		assertTrue(linkRecommendations.size() > 4);
	}

	@Test
	@NonTransactional
	public void testMarkRecommendationAsDiscarded() {
		ContextInformation contextInformation = new ContextInformation(KnowledgeElements.getDecision(),
				linkRecommendationConfiguration);
		LinkRecommendation recommendation = (LinkRecommendation) contextInformation.getLinkRecommendations().get(0);
		DiscardedRecommendationPersistenceManager.saveDiscardedRecommendation(recommendation);
		List<LinkRecommendation> linkRecommendations = contextInformation.getLinkRecommendations();
		assertTrue(linkRecommendations.stream().filter(rec -> rec.isDiscarded()).count() > 0);
	}

	@Test
	public void testDefaultExplanation() {
		ContextInformation contextInformation = new ContextInformation(KnowledgeElements.getDecision(),
				linkRecommendationConfiguration);
		assertNotNull(contextInformation.getExplanation());
	}

	@Test
	public void testDefaultDescription() {
		ContextInformation contextInformation = new ContextInformation(KnowledgeElements.getDecision(),
				linkRecommendationConfiguration);
		assertNotNull(contextInformation.getDescription());
	}

	@After
	public void tearDown() {
		// reset plugin settings to default settings
		ConfigPersistenceManager.saveLinkRecommendationConfiguration("TEST", new LinkRecommendationConfiguration());
	}
}
