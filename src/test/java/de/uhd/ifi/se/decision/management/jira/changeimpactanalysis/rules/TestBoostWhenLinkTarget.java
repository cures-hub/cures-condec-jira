package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestBoostWhenLinkTarget extends TestSetUp {

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
		assertEquals("Boost when element is the link target", ChangePropagationRuleType.BOOST_WHEN_LINK_TARGET.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_LINK_TARGET
			.getExplanation().contains("is the link target"));
	}

	@Test
	public void testPropagationTrueBecauseOutwardLink() {
		Link link = new Link(currentElement, nextElement, LinkType.RELATE);
		assertEquals(1.0, ChangePropagationRuleType.BOOST_WHEN_LINK_TARGET.getFunction().isChangePropagated(null,
				nextElement, link), 0.0);
	}

	@Test
	public void testPropagationFalseBecauseIncomingLink() {
		Link link = new Link(nextElement, currentElement, LinkType.RELATE);
		assertEquals(0.0, ChangePropagationRuleType.BOOST_WHEN_LINK_TARGET.getFunction().isChangePropagated(null,
				nextElement, link), 0.0);
	}
}