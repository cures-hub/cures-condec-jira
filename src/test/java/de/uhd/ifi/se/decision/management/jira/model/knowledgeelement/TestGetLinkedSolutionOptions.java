package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetLinkedSolutionOptions extends TestSetUp {

	private KnowledgeElement decisionProblem;

	@Before
	public void setUp() {
		init();
		decisionProblem = KnowledgeElements.getTestKnowledgeElements().get(4);
	}

	@Test
	public void testHasSolutionOptionsLinked() {
		assertEquals(2, decisionProblem.getLinkedSolutionOptions().size());
	}
}
