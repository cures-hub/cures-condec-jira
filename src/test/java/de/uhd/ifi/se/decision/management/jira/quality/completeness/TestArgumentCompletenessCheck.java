package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;


import net.java.ao.test.jdbc.NonTransactional;

import java.util.List;

import org.jgrapht.Graphs;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestArgumentCompletenessCheck extends TestSetUp {

	private List<KnowledgeElement> elements;
	private KnowledgeElement proArgument;

	@Before
	public void setUp() {
		init();
		elements = KnowledgeElements.getTestKnowledgeElements();
		proArgument = elements.get(7);
	}

	@Test
	@NonTransactional
	public void testIsLinkedToDecision() {
		assertEquals(KnowledgeType.ARGUMENT, proArgument.getType().replaceProAndConWithArgument());
		assertEquals(5, proArgument.getId());
		KnowledgeElement decision = elements.get(6);
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());
		assertNotNull(proArgument.getLink(decision));
		assertTrue(new ArgumentCompletenessCheck().execute(proArgument));
	}

	@Test
	@NonTransactional
	public void testIsNotLinkedToDecision() {
		assertEquals(KnowledgeType.ARGUMENT, proArgument.getType());
		assertEquals(5, proArgument.getId());
		KnowledgeElement decision = elements.get(6);
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());

		Link linkToDecision = proArgument.getLink(decision);
		assertNotNull(linkToDecision);

		KnowledgePersistenceManager.getOrCreate("TEST").deleteLink(linkToDecision,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		linkToDecision = proArgument.getLink(decision);
		assertNull(linkToDecision);

		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(proArgument.getProject());
		assertFalse(graph.containsEdge(linkToDecision));
		assertEquals(2, Graphs.neighborSetOf(graph, proArgument).size());
		assertFalse(new ArgumentCompletenessCheck().execute(proArgument));
	}

}
