package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jgrapht.Graphs;
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

public class TestIssueCompletenessCheck extends TestSetUp {

	private List<KnowledgeElement> elements;
	private KnowledgeElement issue;

	@Before
	public void setUp() {
		init();
		elements = KnowledgeElements.getTestKnowledgeElements();
		issue = elements.get(3);
	}

	@Test
	@NonTransactional
	public void testIsLinkedToDecision() {
		assertEquals(KnowledgeType.ISSUE, issue.getType());
		assertEquals(2, issue.getId());
		KnowledgeElement decision = elements.get(6);
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());
		assertNotNull(issue.getLink(decision));
		assertTrue(new IssueCompletenessCheck().execute(issue));
	}

	@Test
	@NonTransactional
	public void testIsLinkedToAlternative() {
		KnowledgeElement alternative = elements.get(5);
		assertEquals(KnowledgeType.ALTERNATIVE, alternative.getType());
		assertEquals(3, alternative.getId());
		assertNotNull(issue.getLink(alternative));
		assertTrue(new IssueCompletenessCheck().execute(issue));
	}

	@Test
	@NonTransactional
	public void testIsNotLinkedToDecision() {
		assertEquals(KnowledgeType.ISSUE, issue.getType());
		assertEquals(2, issue.getId());
		KnowledgeElement decision = elements.get(6);
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());

		Link linkToDecision = issue.getLink(decision);
		assertNotNull(linkToDecision);

		KnowledgePersistenceManager.getOrCreate("TEST").deleteLink(linkToDecision,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		linkToDecision = issue.getLink(decision);
		assertNull(linkToDecision);

		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(issue.getProject());
		assertFalse(graph.containsEdge(linkToDecision));
		assertEquals(2, Graphs.neighborSetOf(graph, issue).size());
		assertFalse(new IssueCompletenessCheck().execute(issue));
	}
}
