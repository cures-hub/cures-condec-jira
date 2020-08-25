package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDecisionCompletenessCheck extends TestSetUp {
	private List<KnowledgeElement> elements;
	private KnowledgeElement decision;

	@Before
	public void setUp() {
		init();
		elements = KnowledgeElements.getTestKnowledgeElements();
		decision = elements.get(6);
	}

	@Test
	@NonTransactional
	public void testIsLinkedToIssue() {
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());
		KnowledgeElement issue = elements.get(3);
		assertEquals(KnowledgeType.ISSUE, issue.getType());
		assertEquals(2, issue.getId());
		assertNotNull(decision.getLink(issue));
		assertTrue(new DecisionCompletenessCheck().execute(decision));
	}

	@Test
	@NonTransactional
	public void testIsNotLinkedToIssue() {
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());
		KnowledgeElement issue = elements.get(3);
		assertEquals(KnowledgeType.ISSUE, issue.getType());
		assertEquals(2, issue.getId());
		Link linkToIssue = decision.getLink(issue);
		KnowledgePersistenceManager.getOrCreate("TEST").deleteLink(linkToIssue,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		linkToIssue = decision.getLink(issue);
		assertNull(linkToIssue);

		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(decision.getProject());
		assertFalse(graph.containsEdge(linkToIssue));
		// assertEquals(2, Graphs.neighborSetOf(graph, decision).size());
		// assertFalse(new DecisionCompletenessCheck().execute(decision));
	}
}
