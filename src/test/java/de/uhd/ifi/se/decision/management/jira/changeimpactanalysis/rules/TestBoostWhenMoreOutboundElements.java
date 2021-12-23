package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestBoostWhenMoreOutboundElements extends TestSetUp {

    private KnowledgeElement currentElement;
	private KnowledgeElement nextElement;

    @Before
	public void setUp() {
		init();
	}

    @Test
    public void testDescription() {
        assertEquals("Boost when element has more outbound than inbound elements",
            ChangePropagationRule.BOOST_WHEN_MORE_OUTBOUND_THAN_INBOUND.getDescription());
    }
    
    @Test
    public void testPropagationOneIncomingTwoOutgoingLinks() {
        currentElement = KnowledgeElements.getTestKnowledgeElements().get(0);
        nextElement = KnowledgeElements.getTestKnowledgeElements().get(4);

        Link link = currentElement.getLink(nextElement);
        assertEquals(0.666, ChangePropagationRule.BOOST_WHEN_MORE_OUTBOUND_THAN_INBOUND.getFunction()
            .isChangePropagated(null, nextElement, link), 0.005);
    }

    @Test
    public void testPropagationTwoIncomingZeroOutgoingLinks() {
        currentElement = KnowledgeElements.getTestKnowledgeElements().get(10);
        nextElement = KnowledgeElements.getTestKnowledgeElements().get(2);

        Link link = currentElement.getLink(nextElement);
        assertEquals(0.707, ChangePropagationRule.BOOST_WHEN_MORE_OUTBOUND_THAN_INBOUND.getFunction()
            .isChangePropagated(null, nextElement, link), 0.005);
    }
}
