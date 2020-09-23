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

	@Test
	@NonTransactional
	public void testHasIncompleteKnowledgeLinkedNoLinks() {
		assertEquals(KnowledgeType.OTHER, workItem.getType());
		assertEquals(30, workItem.getId());
		assertNotEquals(0, workItem.isLinked());
		KnowledgeGraph.getOrCreate("TEST").removeEdge(workItem.getLink(anotherWorkItem));
		KnowledgeGraph.getOrCreate("TEST").removeEdge(workItem.getLink(decision));
		assertEquals(0, workItem.isLinked());
		assertFalse(CompletenessHandler.hasIncompleteKnowledgeLinked(workItem));
	}

}
