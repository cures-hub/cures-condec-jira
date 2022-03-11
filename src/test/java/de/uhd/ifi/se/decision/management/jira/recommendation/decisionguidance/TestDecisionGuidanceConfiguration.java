package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSource;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;

import static org.junit.Assert.*;

public class TestDecisionGuidanceConfiguration extends TestSetUp {

	private DecisionGuidanceConfiguration config;

	@Before
	public void setUp() {
		init();
		config = new DecisionGuidanceConfiguration();
	}

	@Test
	public void testSetAndGetMaxRecommendations() {
		config.setMaxNumberOfRecommendations(10);
		assertEquals(10, config.getMaxNumberOfRecommendations());
	}

	@Test
	public void testSetAndGetSimilarityThreshold() {
		config.setSimilarityThreshold(0.5);
		assertEquals(0.5, config.getSimilarityThreshold(), 0.0);
	}

	@Test
	public void testSetAndGetAddRecommendationDirectly() {
		config.setRecommendationAddedToKnowledgeGraph(true);
		assertTrue(config.isRecommendationAddedToKnowledgeGraph());
		config.setRecommendationAddedToKnowledgeGraph(false);
		assertFalse(config.isRecommendationAddedToKnowledgeGraph());
	}

	@Test
	public void testSetAndGetRDFKnowledgeSources() {
		List<RDFSource> rdfSources = new ArrayList<>();
		rdfSources.add(new RDFSource("RDF Name", "service", "query", 30000, ""));
		config.setRDFKnowledgeSources(rdfSources);
		assertEquals(1, config.getRDFKnowledgeSources().size());
	}

	@Test
	public void testSetAndGetRDFKnowledgeSource() {
		RDFSource rdfSource = new RDFSource("RDF Name", "service", "query", 30000, "");
		config.addRDFKnowledgeSource(rdfSource);
		assertEquals(2, config.getRDFKnowledgeSources().size());

		RDFSource rdfSourceUpdated = new RDFSource("RDF Name2", "service2", "query2", 10000, "");
		config.updateRDFKnowledgeSource("RDF Name", rdfSourceUpdated);
		rdfSource = config.getRDFKnowledgeSources().get(1);
		assertEquals("service2", rdfSource.getService());
		assertEquals("query2", rdfSource.getQuery());
		assertEquals(10000, rdfSource.getTimeout());
		assertEquals("RDF Name2", rdfSource.getName());

		// Test deactivation
		config.setRDFKnowledgeSourceActivation("RDF Name2", false);
		assertFalse(config.getRDFKnowledgeSources().get(1).isActivated());

		// Delete KnowledgeSource
		config.deleteRDFKnowledgeSource("RDF Name2");
		assertEquals(1, config.getRDFKnowledgeSources().size());
	}

	@Test
	public void testSetProjectKnowledgeSources() {
		ProjectSource source = new ProjectSource(null, true);
		ArrayList<ProjectSource> sources = new ArrayList<>();
		sources.add(source);
		config.setProjectKnowledgeSources(sources);
		config.setRDFKnowledgeSources(new ArrayList<>());
		assertEquals(1, config.getAllActivatedKnowledgeSources().size());
	}

	@Test
	public void testAddRDFKnowledgeSourceNull() {
		config.addRDFKnowledgeSource(null);
		assertEquals(1, config.getRDFKnowledgeSources().size());
	}

	@Test
	public void testGetAllKnowledgeSources() {
		List<RDFSource> rdfSources = new ArrayList<>();
		RDFSource source1 = new RDFSource("RDF Name", "service", "query", 30000, "");
		RDFSource source2 = new RDFSource("RDF Name2", "service", "query", 30000, "");
		source1.setActivated(true);
		source2.setActivated(false);
		rdfSources.add(source1);
		rdfSources.add(source2);
		config.setRDFKnowledgeSources(rdfSources);
		config.setProjectKnowledgeSources(new ArrayList<>());
		assertEquals(2, config.getAllKnowledgeSources().size());
		assertEquals(1, config.getAllActivatedKnowledgeSources().size());
	}

	@Test
	public void testGetKnowledgeSourceByName() {
		RDFSource rdfSource = new RDFSource("DBPedia Frameworks", "service", "query", 30000, "");
		config.addRDFKnowledgeSource(rdfSource);
		assertEquals(rdfSource, config.getKnowledgeSourceByName("DBPedia Frameworks"));
		assertNull(config.getKnowledgeSourceByName("Unknown knowledge source"));
	}

	@Test
	public void testSetAndGetProjectKnowledgeSource() {
		config.setProjectKnowledgeSource("TEST", true);
		assertTrue(config.getProjectSource("TEST").isActivated());
		config.setProjectKnowledgeSource("TEST", false);
		assertFalse(config.getProjectSource("TEST").isActivated());
		config.setProjectKnowledgeSources(new ArrayList<>());
		config.setProjectKnowledgeSource("TEST", false);
		assertFalse(config.getProjectSource("TEST").isActivated());
		config.setProjectKnowledgeSources(null);
		assertNull(config.getProjectSource(""));
	}

	@Test
	public void testAddAllPossibleProjectKnowledgeSources() {
		config.setProjectKnowledgeSources(null);
		config.addAllPossibleProjectKnowledgeSources();
		assertNotNull(config.getProjectKnowledgeSources());
	}

	@Test
	public void testGetProjectKnowledgeSources() {
		config.setProjectKnowledgeSources(null);
		assertNotNull(config.getProjectKnowledgeSources());
	}
}
