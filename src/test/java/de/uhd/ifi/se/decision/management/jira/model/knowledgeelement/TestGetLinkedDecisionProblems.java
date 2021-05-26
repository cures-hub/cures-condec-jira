package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetLinkedDecisionProblems extends TestSetUp {

	private KnowledgeElement decision;

	@Before
	public void setUp() {
		init();
		decision = KnowledgeElements.getDecision();
	}

	@Test
	public void testHasDecisionProblemLinked() {
		assertEquals(2, decision.getLinkedDecisionProblems().size());
	}
}
