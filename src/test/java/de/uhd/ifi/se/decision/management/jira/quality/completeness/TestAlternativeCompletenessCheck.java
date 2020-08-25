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

public class TestAlternativeCompletenessCheck extends TestSetUp {

	private List<KnowledgeElement> elements;
	private KnowledgeElement alternative;

	@Before
	public void setUp() {
		init();
		elements = KnowledgeElements.getTestKnowledgeElements();
		alternative = elements.get(5);
	}

	@Test
	@NonTransactional
	public void testIsLinkedToIssue() {
		assertEquals(alternative.getType(), KnowledgeType.ALTERNATIVE);
		assertEquals(alternative.getId(), 3);
		KnowledgeElement issue = elements.get(3);
		assertEquals(issue.getType(), KnowledgeType.ISSUE);
		assertEquals(issue.getId(), 2);
		assertNotNull(alternative.getLink(issue));
		assertTrue(new AlternativeCompletenessCheck().execute(alternative));
	}

	@Test
	@NonTransactional
	public void testIsNotLinkedToIssue() {
		assertEquals(alternative.getType(), KnowledgeType.ALTERNATIVE);
		assertEquals(alternative.getId(), 3);
		KnowledgeElement issue = elements.get(3);
		assertEquals(issue.getType(), KnowledgeType.ISSUE);
		assertEquals(issue.getId(), 2);
		Link linkToIssue = alternative.getLink(issue);
		KnowledgePersistenceManager.getOrCreate("TEST").deleteLink(linkToIssue,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		linkToIssue = alternative.getLink(issue);
		assertNull(linkToIssue);

		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(alternative.getProject());
		assertFalse(graph.containsEdge(linkToIssue));
		assertEquals(2, Graphs.neighborSetOf(graph, alternative).size());
		assertFalse(new AlternativeCompletenessCheck().execute(alternative));
	}
}
