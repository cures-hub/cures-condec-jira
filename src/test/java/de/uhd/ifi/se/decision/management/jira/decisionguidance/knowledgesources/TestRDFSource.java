package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSourceInputKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSourceInputString;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestRDFSource extends TestSetUp {

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
	private final static int LIMIT = 1;

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testRDFSourceConstructor() {
		RDFSource rdfSource = new RDFSource();
		assertNotNull(rdfSource);
		rdfSource.setService(SERVICE);
		rdfSource.setQueryString(QUERY);
		rdfSource.setTimeout(TIMEOUT);
		rdfSource.setLimit(LIMIT);

		assertEquals(SERVICE, rdfSource.getService());
		assertEquals("DBPedia", rdfSource.getName());
		assertEquals(QUERY, rdfSource.getQueryString());
		assertEquals(TIMEOUT, rdfSource.getTimeout());

		assertEquals(1, rdfSource.getLimit());
		assertEquals("aui-iconfont-download", rdfSource.getIcon());
	}

	@Test
	public void testRDFSourceWithStringInput() {
		RDFSourceInputString rdfSourceInputString = new RDFSourceInputString();
		rdfSourceInputString
				.setKnowledgeSource(new RDFSource(NAME, SERVICE, QUERY, TIMEOUT, LIMIT, "Lizenz=dbo:license"));
		assertEquals(34, rdfSourceInputString.getRecommendations("MySQL").size());
		assertEquals(0, rdfSourceInputString.getRecommendations("").size());
		assertEquals(0, rdfSourceInputString.getRecommendations(null).size());

		rdfSourceInputString
				.setKnowledgeSource(new RDFSource(NAME, "WRONG SERVICE", "INVALID QUERY", TIMEOUT, LIMIT, ""));
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
		RDFSourceInputKnowledgeElement rdfSourceInputKnowledgeElement = new RDFSourceInputKnowledgeElement();
		rdfSourceInputKnowledgeElement.setKnowledgeSource(new RDFSource(NAME, SERVICE, QUERY, TIMEOUT, LIMIT, ""));
		assertEquals(34,
				rdfSourceInputKnowledgeElement.getRecommendations(KnowledgeElements.getTestKnowledgeElement()).size());
		assertEquals(0, rdfSourceInputKnowledgeElement.getRecommendations(null).size());
		assertEquals(0, rdfSourceInputKnowledgeElement.getRecommendations(new KnowledgeElement()).size());
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
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", 10000, 10, "");
		assertEquals("TEST", rdfSource.getService());
		assertEquals("TEST", rdfSource.getQueryString());
		assertEquals("TEST", rdfSource.getName());
		assertEquals("10000", rdfSource.getTimeout());
	}

	@Test
	public void testHashCode() {
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", 10000, 10, "");
		assertEquals(Objects.hash("TEST"), rdfSource.hashCode());
	}

	@Test
	public void testEquals() {
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", 10000, 10, "");
		RDFSource rdfSourceother = new RDFSource("TEST", "TEST", "TEST", 10000, 10, "");
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
