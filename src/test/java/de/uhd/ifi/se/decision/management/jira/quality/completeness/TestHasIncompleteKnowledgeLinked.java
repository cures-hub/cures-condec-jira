package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.Set;

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

public class TestHasIncompleteKnowledgeLinked extends TestSetUp {
	private KnowledgeElement issue;
	private KnowledgeElement decision;
	private KnowledgeElement alternative;
	private KnowledgeElement workItem;
	private KnowledgeElement anotherWorkItem;

	@Before
	public void setUp() {
		init();
		List<KnowledgeElement> elements = KnowledgeElements.getTestKnowledgeElements();
		issue = elements.get(4);
		issue.setStatus(KnowledgeStatus.RESOLVED);
		decision = elements.get(10);
		alternative = elements.get(7);
		workItem = elements.get(2);
		anotherWorkItem = elements.get(1);
	}

	@Test
	@NonTransactional
	public void testIncompleteRootElement() {
		KnowledgeGraph.getOrCreate("TEST").removeEdge(alternative.getLink(issue));
		assertTrue(alternative.isIncomplete());
		assertTrue(CompletenessHandler.hasIncompleteKnowledgeLinked(alternative));
	}

	/**
	 * Test with complete root element (workItem), that is not linked to other
	 * elements.
	 */
	@Test
	@NonTransactional
	public void testCompleteRootElementNoLinks() {
		assertEquals(KnowledgeType.OTHER, workItem.getType());
		assertEquals(30, workItem.getId());
		assertNotEquals(0, workItem.isLinked());
		KnowledgeGraph.getOrCreate("TEST").removeEdge(workItem.getLink(anotherWorkItem));
		KnowledgeGraph.getOrCreate("TEST").removeEdge(workItem.getLink(decision));
		assertEquals(0, workItem.isLinked());
		assertFalse(CompletenessHandler.hasIncompleteKnowledgeLinked(workItem));
	}

	/**
	 * Test with complete root element (workItem), that is linked to one other
	 * element (anotherWorkItem). The other element is also complete and is not
	 * linked to any elements.
	 */
	@Test
	@NonTransactional
	public void testCompleteLinkDistanceOne() {
		assertNotEquals(0, workItem.isLinked());
		// remove all links, that are not needed.
		KnowledgeGraph.getOrCreate("TEST").removeEdge(workItem.getLink(decision));
		Set<Link> links = anotherWorkItem.getLinks();
		for (Link link : links) {
			if (!link.getTarget().equals(workItem)) {
				KnowledgeGraph.getOrCreate("TEST").removeEdge(link);
			}
		}
		assertEquals(1, workItem.getLinks().size());
		assertEquals(1, anotherWorkItem.getLinks().size());
		assertTrue(CompletenessHandler.checkForCompleteness(workItem));
		assertTrue(CompletenessHandler.checkForCompleteness(anotherWorkItem));
		assertFalse(CompletenessHandler.hasIncompleteKnowledgeLinked(workItem));
	}

	/**
	 * Test with complete root element (workItem), that is linked to one other
	 * element (decision). The other element is incomplete.
	 */
	@Test
	@NonTransactional
	public void testIncompleteLinkDistanceOne() {
		KnowledgeGraph.getOrCreate("TEST").removeEdge(workItem.getLink(anotherWorkItem));
		Set<Link> links = decision.getLinks();
		for (Link link : links) {
			if (link.getOppositeElement(decision).getType() == KnowledgeType.ISSUE) {
				KnowledgeGraph.getOrCreate("TEST").removeEdge(link);
			}
		}
		assertTrue(decision.isIncomplete());
		assertNotNull(workItem.getLink(decision));
		assertTrue(CompletenessHandler.hasIncompleteKnowledgeLinked(workItem));
	}

	/**
	 * Test with complete root element (workItem), that is linked to one other
	 * element (decision). The other element is complete, but is linked to another
	 * element (issue), which is incomplete.
	 */
	@Test
	@NonTransactional
	public void testIncompleteLinkDistanceTwo() {
		KnowledgeGraph.getOrCreate("TEST").removeEdge(workItem.getLink(anotherWorkItem));
		assertFalse(decision.isIncomplete());
		assertNotNull(decision.getLink(issue));
		assertFalse(issue.isIncomplete());
		KnowledgeGraph.getOrCreate("TEST").removeEdge(issue.getLink(alternative));
		assertNull(issue.getLink(alternative));
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setIssueLinkedToAlternative(true);
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);
		assertTrue(issue.isIncomplete());
		assertTrue(CompletenessHandler.hasIncompleteKnowledgeLinked(workItem));
	}

	@After
	public void tearDown() {
		// restore default
		ConfigPersistenceManager.setDefinitionOfDone("TEST", new DefinitionOfDone());
	}

}
