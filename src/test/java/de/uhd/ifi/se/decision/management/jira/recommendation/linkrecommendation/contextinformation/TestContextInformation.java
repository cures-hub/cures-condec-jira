package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestContextInformation extends TestSetUp {

	@Before
	public void setUp() {
		init();
		LinkRecommendationConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
				.getLinkRecommendationConfiguration("TEST");
		linkSuggestionConfiguration.setMinProbability(0);
		ConfigPersistenceManager.saveLinkSuggestionConfiguration("TEST", linkSuggestionConfiguration);
	}

	@Test
	public void testGetLinkRecommendations() {
		ContextInformation contextInformation = new ContextInformation(KnowledgeElements.getDecision());
		List<Recommendation> linkRecommendations = contextInformation.getLinkRecommendations();
		assertEquals(18, linkRecommendations.size());
	}
}
