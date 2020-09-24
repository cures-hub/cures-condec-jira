package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class TestCompletenessHandler extends TestSetUp {
	private KnowledgeElement issue;
	private KnowledgeElement decision;
	private KnowledgeElement alternative;
	private KnowledgeElement proArgument;
	private KnowledgeElement workItem;
	private KnowledgeElement anotherWorkItem;
	private KnowledgeElement yetAnotherWorkItem;
	private KnowledgeElement conArgument;

	@Before
	public void setUp() {
		init();
		List<KnowledgeElement> elements = KnowledgeElements.getTestKnowledgeElements();
		issue = elements.get(3);
		decision = elements.get(6);
		alternative = elements.get(5);
		proArgument = elements.get(7);
		workItem = elements.get(2);
		anotherWorkItem = elements.get(1);
		yetAnotherWorkItem = elements.get(0);
		conArgument = elements.get(8);
	}

	@Test
	@NonTransactional
	public void testCompleteIssue() {
		assertTrue(CompletenessHandler.checkForCompletion(issue));
	}

	@Test
	@NonTransactional
	public void testCompleteDecision() {
		assertTrue(CompletenessHandler.checkForCompletion(decision));
	}

	@Test
	@NonTransactional
	public void testCompleteAlternative() {
		assertTrue(CompletenessHandler.checkForCompletion(alternative));
	}

	@Test
	@NonTransactional
	public void testCompleteArgument() {
		assertTrue(CompletenessHandler.checkForCompletion(proArgument));
	}


	/**
	 * Test with knowledge Element, that is incompletely documented
	 */
	@Test
	@NonTransactional
	public void testHasIncompleteKnowledgeLinkedIncompleteSource() {
		KnowledgeGraph.getOrCreate("TEST").removeEdge(alternative.getLink(issue));
		assertTrue(alternative.isIncomplete());
		assertTrue(CompletenessHandler.hasIncompleteKnowledgeLinked(alternative));
	}

	/**
	 * Test with knowledge Element, that has no Links to other elements.
	 */
	@Test
	@NonTransactional
	public void testHasIncompleteKnowledgeLinkedNoElementsLinked() {
		assertEquals(KnowledgeType.OTHER, workItem.getType());
		assertEquals(30, workItem.getId());
		assertNotEquals(0, workItem.isLinked());
		KnowledgeGraph.getOrCreate("TEST").removeEdge(workItem.getLink(anotherWorkItem));
		KnowledgeGraph.getOrCreate("TEST").removeEdge(workItem.getLink(decision));
		assertEquals(0, workItem.isLinked());
		assertFalse(CompletenessHandler.hasIncompleteKnowledgeLinked(workItem));
	}

	/**
	 * Test with knowledge Element, that is only directly linked to one other element.
	 * The target element is completely documented and has no links to other elements.
	 */
	@Test
	@NonTransactional
	public void testHasIncompleteKnowledgeLinkedCompleteLinkDistanceOne() {
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
		assertTrue(CompletenessHandler.checkForCompletion(workItem));
		assertTrue(CompletenessHandler.checkForCompletion(anotherWorkItem));
		assertFalse(CompletenessHandler.hasIncompleteKnowledgeLinked(workItem));
	}

	/**
	 * Test with knowledge Element, that is only directly linked to one other element.
	 * The target element is incompletely documented and has no links to other elements.
	 */
	@Test
	@NonTransactional
	public void testHasIncompleteKnowledgeLinkedIncompleteDistanceOne() {
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
	 * Test with knowledge Element, that linked to incomplete elements with a link-distance of two.
	 */
	@Test
	@NonTransactional
	public void testHasIncompleteKnowledgeLinkedIncompleteDistanceTwo() {
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

	@Test
	@NonTransactional
	public void testHasIncompleteKnowledgeLinkedIncompleteDistanceThree() {
		KnowledgeGraph.getOrCreate("TEST").removeEdge(workItem.getLink(anotherWorkItem));
		assertFalse(alternative.isIncomplete());
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setAlternativeLinkedToArgument(true);
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);
		KnowledgeGraph.getOrCreate("TEST").removeEdge(alternative.getLink(conArgument));
		assertTrue(alternative.isIncomplete());
		assertTrue(CompletenessHandler.hasIncompleteKnowledgeLinked(workItem));
	}

	@After
	public void tearDown() {
		// restore default
		ConfigPersistenceManager.setDefinitionOfDone("TEST", new DefinitionOfDone());
	}

}
