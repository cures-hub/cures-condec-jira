package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSourceInputKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSourceInputString;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

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
	@NonTransactional
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
		assertEquals("aui-iconfont-download", rdfSource.getIcon());
	}

	@Test
	public void testGetInputMethodAndSetData() {
		KnowledgeSource rdfSource = new RDFSource();
		rdfSource.setRecommenderType(RecommenderType.KEYWORD);
		assertEquals(RDFSourceInputString.class, rdfSource.getInputMethod().getClass());
	}

	@Test
	@NonTransactional
	@Ignore
	public void testRDFSourceWithStringInput() {
		RDFSourceInputString rdfSourceInputString = new RDFSourceInputString();
		rdfSourceInputString.setData(new RDFSource(PROJECTKEY, SERVICE, QUERY, NAME, TIMEOUT, LIMIT));
		assertEquals(2, rdfSourceInputString.getResults("MySQL").size());
		assertEquals(0, rdfSourceInputString.getResults("").size());
		assertEquals(0, rdfSourceInputString.getResults(null).size());

		rdfSourceInputString.setData(new RDFSource(PROJECTKEY, "WRONG SERVICE", "INVALID QUERY", NAME, TIMEOUT, LIMIT));
		assertEquals(0, rdfSourceInputString.getResults("MySQL").size());


	}

	@Test
	@NonTransactional
	@Ignore
	public void testRDFSourceWithKnowledgeElement() {
		RDFSourceInputKnowledgeElement rdfSourceInputKnowledgeElement = new RDFSourceInputKnowledgeElement();
		rdfSourceInputKnowledgeElement.setData(new RDFSource(PROJECTKEY, SERVICE, QUERY, NAME, TIMEOUT, LIMIT));
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
	@NonTransactional
	public void testRDFSourceActivated() {
		KnowledgeSource source = new RDFSource("Test");
		source.setName("RDFSource");
		assertEquals(true, source.isActivated());
	}

	@Test
	@NonTransactional
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
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", "TEST", "10000", 10);
		assertEquals("TEST", rdfSource.getProjectKey());
		assertEquals("TEST", rdfSource.getService());
		assertEquals("TEST", rdfSource.getQueryString());
		assertEquals("TEST", rdfSource.getName());
		assertEquals("10000", rdfSource.getTimeout());
	}

	@Test
	public void testgetInputMethod() {
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", "TEST", "10000", 10);
		rdfSource.setRecommenderType(RecommenderType.KEYWORD);
		assertEquals(RDFSourceInputString.class, rdfSource.getInputMethod().getClass());
		rdfSource.setRecommenderType(RecommenderType.ISSUE);
		assertEquals(RDFSourceInputKnowledgeElement.class, rdfSource.getInputMethod().getClass());
	}

	@Test
	public void testHashCode() {
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", "TEST", "10000", 10);
		assertEquals(Objects.hash("TEST"), rdfSource.hashCode());
	}

	@Test
	public void testEquals() {
		RDFSource rdfSource = new RDFSource("TEST", "TEST", "TEST", "TEST", "10000", 10);
		RDFSource rdfSourceother = new RDFSource("TEST", "TEST", "TEST", "TEST", "10000", 10);
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
