package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class TestDecisionGuidanceConfiguration {

	private DecisionGuidanceConfiguration config;

	@Before
	public void setUp() {
		config = new DecisionGuidanceConfiguration();
	}

	@Test
	public void testSetAndGetMaxRecommendations() {
		config.setMaxNumberOfRecommendations(10);
		assertEquals(10, config.getMaxNumberOfRecommendations());
	}

	@Test
	public void testSetAndGetRecommendationInput() {
		config.setRecommendationInput("KEYWORD", true);
		assertEquals(1, config.getInputTypes().size());
	}

	@Test
	public void testSetAndGetSimilarityThreshold() {
		config.setSimilarityThreshold(0.5);
		assertEquals(0.5, config.getSimilarityThreshold(), 0.0);
	}

	@Test
	public void testSetAndGetIrrelevantWords() {
		config.setIrrelevantWords("WHICH;WHAT;COULD;SHOULD");
		assertEquals("WHICH;WHAT;COULD;SHOULD", config.getIrrelevantWords());
	}

	@Test
	public void testSetAndGetAddRecommendationDirectly() {
		config.setRecommendationAddedToKnowledgeGraph(true);
		assertTrue(config.isRecommendationAddedToKnowledgeGraph());
		config.setRecommendationAddedToKnowledgeGraph(false);
		assertFalse(config.isRecommendationAddedToKnowledgeGraph());
	}

	@Test
	public void testSetAndGetRDFKnowledgeSource() {
		RDFSource rdfSource = new RDFSource("TEST", "service", "query", "RDF Name", "30000", 100, "");
		ConfigPersistenceManager.addRdfKnowledgeSource("TEST", rdfSource);
		assertEquals("Number of Knowledge sources should be 1", 1,
				ConfigPersistenceManager.getDecisionGuidanceConfiguration("TEST").getRdfKnowledgeSources().size());

		RDFSource rdfSourceUpdated = new RDFSource("TEST", "service2", "query2", "RDF Name2", "10000", 100, "");
		ConfigPersistenceManager.updateKnowledgeSource("TEST", "RDF Name", rdfSourceUpdated);
		rdfSource = ConfigPersistenceManager.getDecisionGuidanceConfiguration("TEST").getRdfKnowledgeSources().get(0);
		assertEquals("service2", rdfSource.getService());
		assertEquals("query2", rdfSource.getQueryString());
		assertEquals("10000", rdfSource.getTimeout());
		assertEquals("RDF Name2", rdfSource.getName());

		// Test invalid Source
		ConfigPersistenceManager.addRdfKnowledgeSource("TEST", null);
		assertEquals("Size of existing Knowledge sources should be 1: No error!", 1,
				ConfigPersistenceManager.getDecisionGuidanceConfiguration("TEST").getRdfKnowledgeSources().size());

		// Test deactivation
		ConfigPersistenceManager.setRDFKnowledgeSourceActivation("TEST", "RDF Name2", false);
		assertFalse("The knowledge source should be dectivated!", ConfigPersistenceManager
				.getDecisionGuidanceConfiguration("TEST").getRdfKnowledgeSources().get(0).isActivated());

		// Delete KnowledgeSource
		ConfigPersistenceManager.deleteKnowledgeSource("TEST", "RDF Name2");
		assertEquals("The knowledge source should be 0!", 0,
				ConfigPersistenceManager.getDecisionGuidanceConfiguration("TEST").getRdfKnowledgeSources().size());
	}

}
