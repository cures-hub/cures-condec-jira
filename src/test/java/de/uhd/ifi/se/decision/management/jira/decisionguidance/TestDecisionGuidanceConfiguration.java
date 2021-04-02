package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;

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
		config.addRdfKnowledgeSource(rdfSource);
		assertEquals(1, config.getRdfKnowledgeSources().size());

		RDFSource rdfSourceUpdated = new RDFSource("TEST", "service2", "query2", "RDF Name2", "10000", 100, "");
		config.updateKnowledgeSource("RDF Name", rdfSourceUpdated);
		rdfSource = config.getRdfKnowledgeSources().get(0);
		assertEquals("service2", rdfSource.getService());
		assertEquals("query2", rdfSource.getQueryString());
		assertEquals("10000", rdfSource.getTimeout());
		assertEquals("RDF Name2", rdfSource.getName());

		// Test deactivation
		config.setRdfKnowledgeSourceActivation("RDF Name2", false);
		assertFalse(config.getRdfKnowledgeSources().get(0).isActivated());

		// Delete KnowledgeSource
		config.deleteKnowledgeSource("RDF Name2");
		assertEquals(0, config.getRdfKnowledgeSources().size());
	}

	@Test
	public void testAddRDFKnowledgeSourceNull() {
		config.addRdfKnowledgeSource(null);
		assertEquals(0, config.getRdfKnowledgeSources().size());
	}

	@Test
	public void testGetAllKnowledgeSources() {
		assertEquals(0, config.getAllKnowledgeSources().size());
	}

	@Test
	public void testSetAndGetProjectKnowledgeSources() {
		config.setProjectSource("OTHERPRORJECT", true);
		assertTrue(config.getProjectSource("OTHERPRORJECT").isActivated());
		config.setProjectSource("OTHERPRORJECT", false);
		assertFalse(config.getProjectSource("OTHERPRORJECT").isActivated());
	}

	@Test
	public void testGetActiveProjects() {
		config.setProjectSource("TEST", true);
		assertEquals(1, config.getProjectKnowledgeSources().size());
		config.setProjectSource("TEST", false);
		assertEquals(0, config.getProjectKnowledgeSources().size());
	}

}
