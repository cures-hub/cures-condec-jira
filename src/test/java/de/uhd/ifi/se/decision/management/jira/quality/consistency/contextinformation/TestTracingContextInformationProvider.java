package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.LinkSuggestionConfiguration;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestTracingContextInformationProvider extends TestSetUp {

	private ContextInformationProvider tracingContextInformationProvider;
	private List<KnowledgeElement> knowledgeElements;

	@Before
	public void setUp() {
		init();
		LinkSuggestionConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
				.getLinkSuggestionConfiguration("TEST");
		linkSuggestionConfiguration.setMinProbability(0);
		ConfigPersistenceManager.saveLinkSuggestionConfiguration("TEST", linkSuggestionConfiguration);
		tracingContextInformationProvider = new TracingContextInformationProvider();
		knowledgeElements = KnowledgeElements.getTestKnowledgeElements();
	}

	@Test
	public void testSameElement() {
		assertEquals(1.0,
				tracingContextInformationProvider.assessRelation(knowledgeElements.get(0), knowledgeElements.get(0)),
				0);
	}

	@Test
	public void testDirectlyLinked() {
		assertEquals(0.5,
				tracingContextInformationProvider.assessRelation(knowledgeElements.get(0), knowledgeElements.get(1)),
				0);
	}

	@Test
	public void testId() {
		assertEquals("TracingCIP_BFS", tracingContextInformationProvider.getId());
	}

	@Test
	public void testIndirectlyLinked() {
		assertEquals(0.333,
				tracingContextInformationProvider.assessRelation(knowledgeElements.get(0), knowledgeElements.get(2)),
				0.1);
	}
}
