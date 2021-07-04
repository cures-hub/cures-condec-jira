package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestIgnoreIncomingLinks extends TestSetUp {

	private KnowledgeElement currentElement;
	private KnowledgeElement nextElement;

	@Before
	public void setUp() {
		init();
		currentElement = KnowledgeElements.getTestKnowledgeElement();
		nextElement = KnowledgeElements.getSolvedDecisionProblem();
	}

	@Test
	public void testDescription() {
		assertEquals("Outward links only", ChangePropagationRule.IGNORE_INCOMING_LINKS.getDescription());
	}

	@Test
	public void testPropagationTrueBecauseOutwardLink() {
		Link link = new Link(currentElement, nextElement, LinkType.RELATE);
		assertEquals(1.0, ChangePropagationRule.IGNORE_INCOMING_LINKS.getFunction().isChangePropagated(null,
				currentElement, link), 0.0);
	}

	@Test
	public void testPropagationFalseBecauseIncomingLink() {
		Link link = new Link(nextElement, currentElement, LinkType.RELATE);
		assertEquals(0.0, ChangePropagationRule.IGNORE_INCOMING_LINKS.getFunction().isChangePropagated(null,
				currentElement, link), 0.0);
	}
}