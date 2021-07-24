package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestRDFSourceRecommender extends TestSetUp {

	private final static String PROJECTKEY = "TEST";
	private final static String NAME = "TESTSOURCE";
	private final static String SERVICE = "http://dbpedia.org/sparql";
	private static final String PREFIX = "PREFIX dbo: <http://dbpedia.org/ontology/>"
			+ "PREFIX dct: <http://purl.org/dc/terms/>" + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>";
	private final static String QUERY = PREFIX + " select distinct ?subject ?url count(?link)   where { "
			+ "%variable% dbo:genre ?genre. " + "?subject dbo:genre ?genre. " + "?subject foaf:isPrimaryTopicOf ?url. "
			+ "?subject dbo:wikiPageExternalLink ?link.} GROUP BY ?subject ?url ";
	private final static int TIMEOUT = 50000;
	private RDFSource rdfSource;

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testRDFSourceWithStringInput() {
		RDFSourceRecommender rdfSourceInputString = new RDFSourceRecommender("TEST", rdfSource);
		rdfSourceInputString.setKnowledgeSource(new RDFSource(NAME, SERVICE, QUERY, TIMEOUT, "Lizenz=dbo:license"));
		assertTrue(rdfSourceInputString.getRecommendations("MySQL").size() > 30);
		assertEquals(0, rdfSourceInputString.getRecommendations("").size());
		assertEquals(0, rdfSourceInputString.getRecommendations((String) null).size());

		rdfSourceInputString.setKnowledgeSource(new RDFSource(NAME, "WRONG SERVICE", "INVALID QUERY", TIMEOUT, ""));
		assertEquals(0, rdfSourceInputString.getRecommendations("Does not matter").size());
	}

	@Test
	public void testRDFSourceWithKnowledgeElement() {
		KnowledgeElement alternative = new KnowledgeElement();
		alternative.setType(KnowledgeType.ALTERNATIVE);
		alternative.setSummary("MySQL");
		alternative.setId(123);
		Link link = new Link(KnowledgeElements.getTestKnowledgeElement(), alternative);
		KnowledgeGraph graph = KnowledgeGraph.getInstance(PROJECTKEY);
		graph.addEdge(link);
		RDFSourceRecommender rdfSourceInputString = new RDFSourceRecommender("TEST", rdfSource);
		rdfSourceInputString.setKnowledgeSource(new RDFSource(NAME, SERVICE, QUERY, TIMEOUT, ""));
		assertTrue(rdfSourceInputString.getRecommendations(KnowledgeElements.getTestKnowledgeElement()).size() > 30);
		assertEquals(0, rdfSourceInputString.getRecommendations((String) null).size());
		assertEquals(0, rdfSourceInputString.getRecommendations(new KnowledgeElement()).size());
	}
}
