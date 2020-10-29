package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class TestRDFSource extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testRDFSource() {
		KnowledgeSource source = new RDFSource("Test");
		source.setName("RDFSource");
		((RDFSource) source).setLimit(10);
		List<Recommendation> recommendations = source.getResults("");
		assertEquals(10, recommendations.size());
		assertEquals(true, source.isActivated());
	}

	@Test
	public void testRDFSourceWithInput() {
		KnowledgeSource source = new RDFSource("Test");
		source.setName("RDFSource");
		List<Recommendation> recommendations = source.getResults("Test 123");
		assertEquals(30, recommendations.size());
	}

	//The method to handle knowledge elements is not implemented yet, therefore should return zero as default
	@Test
	public void testRDFSourceWithKnowledgeElement() {
		KnowledgeSource source = new RDFSource("Test");
		source.setName("RDFSource");
		List<Recommendation> recommendations = source.getResults(new KnowledgeElement());
		assertEquals(0, recommendations.size());
		KnowledgeElement knowledgeElement = null;
		recommendations = source.getResults(knowledgeElement);
		assertEquals(0, recommendations.size());
		source.setActivated(false);
		recommendations = source.getResults(new KnowledgeElement());
		assertEquals(0, recommendations.size());
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
