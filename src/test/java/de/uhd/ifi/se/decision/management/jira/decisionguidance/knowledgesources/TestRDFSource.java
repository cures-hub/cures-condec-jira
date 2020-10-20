package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestRDFSource extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testRDFSource() {
		KnowledgeSource source = new RDFSource("Test");
		source.setName("RDFSource");
		List<Recommendation> recommendations = source.getResults("");
		assertEquals(46, recommendations.size());
		assertEquals("RDFSource", recommendations.get(0).getKnowledgeSourceName());
		assertEquals(true, source.isActivated());
	}

	@Test
	public void testActivation() {
		KnowledgeSource source = new RDFSource("Test");
		source.setName("RDFSource");
		source.setActivated(false);
		List<Recommendation> recommendations = source.getResults("");
		assertEquals(0, recommendations.size());
	}

	@Test
	public void testRDFSourceWithInput() {
		KnowledgeSource source = new RDFSource("Test");
		source.setName("RDFSource");
		List<Recommendation> recommendations = source.getResults("Test 123");
		assertEquals(138, recommendations.size()); // 3*46 since there are 3 combinations possible with the input
		assertEquals("RDFSource", recommendations.get(0).getKnowledgeSourceName());
		assertEquals(true, source.isActivated());
		source.setActivated(false);
		recommendations = source.getResults("Test 123");
		assertEquals(0, recommendations.size());
	}

	@Test
	public void testRDFSourceWithKnowledgeElement() {
		KnowledgeSource source = new RDFSource("Test");
		source.setName("RDFSource");
		List<Recommendation> recommendations = source.getResults(new KnowledgeElement());
		assertEquals(46, recommendations.size());
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

	@Test
	public void testRDFqueryDataBaseExecption() {
		RDFSource source = new RDFSource("Test");
		source.setName("RDFSource");
		source.setActivated(true);
		assertEquals(null, source.queryDatabase("aöslkdjasdkjhasd###111///**", "TEST")); //expcet QueryParseException to be catched
	}

	@Test
	public void testToString() {
		RDFSource source = new RDFSource("Test");
		source.setName("One Two Three");
		assertEquals("One-Two-Three", source.toString());
	}


}
