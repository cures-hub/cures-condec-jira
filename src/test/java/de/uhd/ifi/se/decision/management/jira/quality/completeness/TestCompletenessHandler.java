package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TestCompletenessHandler extends TestSetUp {
	private KnowledgeElement issue;
	private KnowledgeElement decision;
	private KnowledgeElement alternative;
	private KnowledgeElement proArgument;
	private KnowledgeElement workItem;
	private KnowledgeElement anotherWorkItem;
	private KnowledgeElement yetAnotherWorkItem;
	private AlternativeCompletenessCheck alternativeCompletenessCheck;

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
		alternativeCompletenessCheck = new AlternativeCompletenessCheck();
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
		assertFalse(alternativeCompletenessCheck.execute(alternative));
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
	 * Test with knowledge Element, that is only directly linked to one other (target) element.
	 * The target element is completely documented and has no links to other elements.
	 */
	@Test
	@NonTransactional
	public void testHasIncompleteKnowledgeLinkedCompleteLinkDistanceOne() {
		assertNotEquals(0, workItem.isLinked());
		// remove all links, that are not needed.
		KnowledgeGraph.getOrCreate("TEST").removeEdge(workItem.getLink(decision));
		KnowledgeGraph.getOrCreate("TEST").removeEdge(anotherWorkItem.getLink(alternative));
		KnowledgeGraph.getOrCreate("TEST").removeEdge(anotherWorkItem.getLink(yetAnotherWorkItem));
		KnowledgeGraph.getOrCreate("TEST").removeEdge(anotherWorkItem.getLink(decision));
		//assertFalse(CompletenessHandler.hasIncompleteKnowledgeLinked(workItem));
		assertEquals(1, workItem.getLinks().size());
		assertEquals(1, anotherWorkItem.getLinks().size());
		assertTrue(CompletenessHandler.checkForCompletion(workItem));
		assertTrue(CompletenessHandler.checkForCompletion(anotherWorkItem));
		assertFalse(CompletenessHandler.hasIncompleteKnowledgeLinked(workItem));
	}

}
