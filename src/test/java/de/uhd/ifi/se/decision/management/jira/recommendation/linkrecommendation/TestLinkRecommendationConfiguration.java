package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.TextualSimilarityContextInformationProvider;

public class TestLinkRecommendationConfiguration extends TestSetUp {

	private LinkRecommendationConfiguration config;

	@Before
	public void setUp() {
		init();
		config = new LinkRecommendationConfiguration();
	}

	@Test
	public void testSetAndGetSimilarityThreshold() {
		config.setMinProbability(0.9);
		assertEquals(0.9, config.getMinProbability(), 0.0);
	}

	@Test
	public void testSetAndGetContextInformationProviders() {
		config.setContextInformationProviders(LinkRecommendationConfiguration.getAllContextInformationProviders());
		assertEquals(9, config.getContextInformationProviders().size());

		config.setContextInformationProviders(List.of(new TextualSimilarityContextInformationProvider()));
		// All rules are returned
		assertEquals(9, config.getContextInformationProviders().size());
	}

}
