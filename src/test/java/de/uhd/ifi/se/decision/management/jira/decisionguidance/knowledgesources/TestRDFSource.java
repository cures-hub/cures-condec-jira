package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.resultmethods.RDFSourceInputKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.resultmethods.RDFSourceInputString;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class TestRDFSource extends TestSetUp {

	private final static String PROJECTKEY = "TEST";
	private final static String NAME = "TESTSOURCE";
	private final static String SERVICE = "http://dbpedia.org/sparql";
	private final static String QUERY = "PREFIX dbo: <http://dbpedia.org/ontology/> " +
		"PREFIX dct: <http://purl.org/dc/terms/> " +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +

		"select distinct ?alternative ?url ?link where { " +
		"%variable% dbo:genre ?genre. " +
		"?url dbo:genre ?genre. " +
		" ?url rdfs:label ?alternative. " +
		"FILTER(LANG(?alternative) = 'en'). " +
		"?url dbo:wikiPageExternalLink ?link. " +
		"} ";
	private final static String TIMEOUT = "50000";
	private final static int LIMIT = 10;

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testRDFSourceConstructor() {
		RDFSource rdfSource = new RDFSource();
		assertNotNull(rdfSource);
		rdfSource.setService(SERVICE);
		rdfSource.setProjectKey(PROJECTKEY);
		rdfSource.setQueryString(QUERY);
		rdfSource.setTimeout(TIMEOUT);
		rdfSource.setLimit(LIMIT);

		assertEquals(SERVICE, rdfSource.getService());
		assertEquals(PROJECTKEY, rdfSource.getProjectKey());
		assertEquals(QUERY, rdfSource.getQueryString());
		assertEquals(TIMEOUT, rdfSource.getTimeout());

		ConfigPersistenceManager.setMaxNumberRecommendations(PROJECTKEY, 10);

		assertEquals(LIMIT, rdfSource.getLimit());

	}

	@Test
	public void testGetInputMethodAndSetData() {
		KnowledgeSource rdfSource = new RDFSource();
		ConfigPersistenceManager.setRecommendationInput(PROJECTKEY, RecommenderType.KEYWORD.toString());
		assertEquals(RDFSourceInputString.class, rdfSource.getInputMethod().getClass() );
		rdfSource.setData();
	}

	@Test
	public void testRDFSourceWithStringInput() {
		RDFSourceInputString rdfSourceInputString = new RDFSourceInputString();
		rdfSourceInputString.setData(PROJECTKEY, NAME, SERVICE, QUERY, TIMEOUT, LIMIT);
		assertEquals(2, rdfSourceInputString.getResults("MySQL").size());
		assertEquals(0, rdfSourceInputString.getResults("").size());
		assertEquals(0, rdfSourceInputString.getResults(null).size());

		rdfSourceInputString.setData(PROJECTKEY, NAME, "WRONG SERVICE", "INVALID QUERY", TIMEOUT, LIMIT);
		assertEquals(0, rdfSourceInputString.getResults("MySQL").size());


	}

	@Test
	public void testRDFSourceWithKnowledgeElement() {
		RDFSourceInputKnowledgeElement rdfSourceInputKnowledgeElement = new RDFSourceInputKnowledgeElement();
		rdfSourceInputKnowledgeElement.setData(PROJECTKEY, NAME, SERVICE, QUERY, TIMEOUT, LIMIT);
		KnowledgeElement alternative = new KnowledgeElement();
		alternative.setType(KnowledgeType.ALTERNATIVE);
		alternative.setSummary("MySQL");
		alternative.setId(123);
		Link link = new Link(KnowledgeElements.getTestKnowledgeElement(), alternative);
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(PROJECTKEY);
		graph.addEdge(link);
		assertEquals(17, rdfSourceInputKnowledgeElement.getResults(KnowledgeElements.getTestKnowledgeElement()).size());
		assertEquals(0, rdfSourceInputKnowledgeElement.getResults(null).size());
		assertEquals(0, rdfSourceInputKnowledgeElement.getResults(new KnowledgeElement()).size());
	}


	@Test
	public void testRDFSourceActivated() {
		KnowledgeSource source = new RDFSource("Test");
		source.setName("RDFSource");
		assertEquals(true, source.isActivated());
	}

	@Test
	public void testRDFSourceDeactivated() {
		KnowledgeSource source = new RDFSource("Test");
		source.setName("RDFSource");
		source.setActivated(false);
		assertEquals(false, source.isActivated());
	}

	/*
	@Test
	public void testRDFqueryDataBaseExecption() {
		RDFSource source = new RDFSource("Test");
		source.setName("RDFSource");
		source.setActivated(true);
		assertEquals(null, source.queryDatabase("aöslkdjasdkjhasd###111///**", "TEST")); //expcet QueryParseException to be catched
	} */

	@Test
	public void testConstructor() {
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", "TEST", "10000");
		assertEquals("TEST", rdfSource.getProjectKey());
		assertEquals("TEST", rdfSource.getService());
		assertEquals("TEST", rdfSource.getQueryString());
		assertEquals("TEST", rdfSource.getName());
		assertEquals("10000", rdfSource.getTimeout());
	}

	@Test
	public void testgetInputMethod() {
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", "TEST", "10000");
		assertNotNull(rdfSource.getInputMethod());
	}

	@Test
	public void testHashCode() {
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", "TEST", "10000");
		assertEquals(Objects.hash("TEST"), rdfSource.hashCode());
	}

	@Test
	public void testEquals() {
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", "TEST", "10000");
		RDFSource rdfSourceother = new RDFSource("TEST", "TEST", "TEST", "TEST", "10000");
		assertTrue(rdfSourceother.equals(rdfSource));
		assertTrue(rdfSourceother.equals(rdfSourceother));
		assertFalse(rdfSourceother.equals(null));
	}


	@Test
	public void testToString() {
		RDFSource source = new RDFSource("Test");
		source.setName("One Two Three");
		assertEquals("One-Two-Three", source.toString());
	}


}
