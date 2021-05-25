package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestIssueCompletenessCheck extends TestSetUp {

	private List<KnowledgeElement> elements;
	private KnowledgeElement issue;
	private DecisionProblemCompletenessCheck issueCompletenessCheck;

	@Before
	public void setUp() {
		init();
		issueCompletenessCheck = new DecisionProblemCompletenessCheck();
		elements = KnowledgeElements.getTestKnowledgeElements();
		issue = KnowledgeElements.getSolvedDecisionProblem();
		issue.setStatus(KnowledgeStatus.RESOLVED);
	}

	@Test
	@NonTransactional
	public void testIsLinkedToDecision() {
		assertEquals(KnowledgeType.ISSUE, issue.getType());
		assertEquals(2, issue.getId());
		KnowledgeElement decision = elements.get(10);
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());
		assertNotNull(issue.getLink(decision));
		assertTrue(issueCompletenessCheck.execute(issue));
	}

	@Test
	@NonTransactional
	public void testIsLinkedToAlternative() {
		KnowledgeElement alternative = elements.get(7);
		assertEquals(KnowledgeType.ALTERNATIVE, alternative.getType());
		assertEquals(3, alternative.getId());
		assertNotNull(issue.getLink(alternative));
		assertTrue(new DecisionProblemCompletenessCheck().execute(issue));
	}

	@Test
	@NonTransactional
	public void testIsNotLinkedToDecision() {
		assertEquals(KnowledgeType.ISSUE, issue.getType());
		assertEquals(2, issue.getId());
		KnowledgeElement decision = KnowledgeElements.getDecision();
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());

		Link linkToDecision = issue.getLink(decision);
		assertNotNull(linkToDecision);

		KnowledgeGraph.getInstance("TEST").removeEdge(linkToDecision);
		linkToDecision = issue.getLink(decision);
		assertNull(linkToDecision);

		KnowledgeGraph graph = KnowledgeGraph.getInstance(issue.getProject());
		assertFalse(graph.containsEdge(linkToDecision));
		assertEquals(2, Graphs.neighborSetOf(graph, issue).size());
		assertFalse(issueCompletenessCheck.execute(issue));
	}

	@Test
	@NonTransactional
	public void testIsCompleteAccordingToSettings() {
		// set criteria "issue has to be linked to alternative" in definition of done
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setIssueLinkedToAlternative(true);
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);
		assertTrue(issueCompletenessCheck.execute(issue));
		// delete links between issue and alternatives
		Set<Link> links = issue.getLinks();
		for (Link link : links) {
			if (link.getOppositeElement(issue).getType() == KnowledgeType.ALTERNATIVE) {
				KnowledgeGraph.getInstance("TEST").removeEdge(link);
			}
		}
		assertFalse(issueCompletenessCheck.execute(issue));
	}

	@After
	public void tearDown() {
		// restore default
		ConfigPersistenceManager.setDefinitionOfDone("TEST", new DefinitionOfDone());
	}
}
