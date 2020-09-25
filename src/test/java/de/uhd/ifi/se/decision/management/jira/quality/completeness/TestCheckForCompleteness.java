package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestCheckForCompleteness extends TestSetUp {
	private KnowledgeElement issue;
	private KnowledgeElement decision;
	private KnowledgeElement alternative;
	private KnowledgeElement proArgument;

	@Before
	public void setUp() {
		init();
		List<KnowledgeElement> elements = KnowledgeElements.getTestKnowledgeElements();
		issue = elements.get(3);
		decision = elements.get(6);
		alternative = elements.get(5);
		proArgument = elements.get(7);
	}

	@Test
	@NonTransactional
	public void testCompleteIssue() {
		assertTrue(CompletenessHandler.checkForCompleteness(issue));
	}

	@Test
	@NonTransactional
	public void testCompleteDecision() {
		assertTrue(CompletenessHandler.checkForCompleteness(decision));
	}

	@Test
	@NonTransactional
	public void testCompleteAlternative() {
		assertTrue(CompletenessHandler.checkForCompleteness(alternative));
	}

	@Test
	@NonTransactional
	public void testCompleteArgument() {
		assertTrue(CompletenessHandler.checkForCompleteness(proArgument));
	}

}
