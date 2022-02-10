package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestBoostWhenMoreOutboundLinks extends TestSetUp {

	private KnowledgeElement currentElement;
	private KnowledgeElement nextElement;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		filterSettings = new FilterSettings();
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element has more outbound than inbound links",
				ChangePropagationRuleType.BOOST_WHEN_MORE_OUTBOUND_THAN_INBOUND.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_MORE_OUTBOUND_THAN_INBOUND.getExplanation()
				.contains("has more outbound links than inbound links"));
	}

	@Test
	public void testPropagationOneIncomingTwoOutgoingLinks() {
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(0);
		nextElement = KnowledgeElements.getTestKnowledgeElements().get(4);

		Link link = currentElement.getLink(nextElement);
		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_MORE_OUTBOUND_THAN_INBOUND.getFunction()
				.isChangePropagated(filterSettings, nextElement, link));
	}

	@Test
	public void testPropagationTwoIncomingZeroOutgoingLinks() {
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(10);
		nextElement = KnowledgeElements.getTestKnowledgeElements().get(2);

		Link link = currentElement.getLink(nextElement);
		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_MORE_OUTBOUND_THAN_INBOUND.getFunction()
				.isChangePropagated(filterSettings, nextElement, link));
	}

	@Test
	public void testPropagationOneIncomingZeroOutgoingLinks() {
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(9);
		nextElement = KnowledgeElements.getTestKnowledgeElements().get(16);

		Link link = currentElement.getLink(nextElement);
		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_MORE_OUTBOUND_THAN_INBOUND.getFunction()
				.isChangePropagated(filterSettings, nextElement, link));
	}
}
