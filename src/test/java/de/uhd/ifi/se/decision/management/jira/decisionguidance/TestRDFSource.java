package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
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
	public void testDBPediaSource() {
		KnowledgeSource source = new RDFSource("Test");
		source.setName("RDFSource");
		Recommendation recommendations = source.getResults(null);
		assertEquals(recommendations.getRecommendations().size(), 10);
		assertEquals("RDFSource", recommendations.getKnowledgeSourceName());
	}


}
