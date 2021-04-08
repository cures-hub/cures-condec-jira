package de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.KnowledgeSource;
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
	public void testRDFSourceConstructor() {
		rdfSource = new RDFSource();
		assertNotNull(rdfSource);
		rdfSource.setService(SERVICE);
		rdfSource.setQuery(QUERY);
		rdfSource.setTimeout(TIMEOUT);

		assertEquals(SERVICE, rdfSource.getService());
		assertEquals("DBPedia", rdfSource.getName());
		assertEquals(QUERY, rdfSource.getQuery());
		assertEquals(TIMEOUT, rdfSource.getTimeout());

		assertEquals("aui-iconfont-download", rdfSource.getIcon());
	}

	@Test
	public void testRDFSourceWithStringInput() {
		RDFSourceRecommender rdfSourceInputString = new RDFSourceRecommender("TEST", rdfSource);
		rdfSourceInputString.setKnowledgeSource(new RDFSource(NAME, SERVICE, QUERY, TIMEOUT, "Lizenz=dbo:license"));
		assertEquals(34, rdfSourceInputString.getRecommendations("MySQL").size());
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
		assertEquals(34, rdfSourceInputString.getRecommendations(KnowledgeElements.getTestKnowledgeElement()).size());
		assertEquals(0, rdfSourceInputString.getRecommendations((String) null).size());
		assertEquals(0, rdfSourceInputString.getRecommendations(new KnowledgeElement()).size());
	}

	@Test
	public void testRDFSourceActivated() {
		KnowledgeSource source = new RDFSource();
		source.setName("RDFSource");
		assertEquals(true, source.isActivated());
	}

	@Test
	public void testRDFSourceDeactivated() {
		KnowledgeSource source = new RDFSource();
		source.setName("RDFSource");
		source.setActivated(false);
		assertEquals(false, source.isActivated());
	}

	@Test
	public void testConstructor() {
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", 10000, "");
		assertEquals("TEST", rdfSource.getService());
		assertEquals("TEST", rdfSource.getQuery());
		assertEquals("TEST", rdfSource.getName());
		assertEquals(10000, rdfSource.getTimeout());
	}

	@Test
	public void testHashCode() {
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", 10000, "");
		assertEquals(Objects.hash("TEST"), rdfSource.hashCode());
	}

	@Test
	public void testEquals() {
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", 10000, "");
		RDFSource rdfSourceother = new RDFSource("TEST", "TEST", "TEST", 10000, "");
		assertTrue(rdfSourceother.equals(rdfSource));
		assertTrue(rdfSourceother.equals(rdfSourceother));
		assertFalse(rdfSourceother.equals(null));
	}

	@Test
	public void testToString() {
		RDFSource source = new RDFSource();
		source.setName("One Two Three");
		assertEquals("One-Two-Three", source.toString());
	}

}
