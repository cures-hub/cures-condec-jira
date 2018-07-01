package de.uhd.ifi.se.decision.management.jira.model;

import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLink;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.LinkEntity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TestLinkImpl {

	private Link linkImpl;

	@Before
	public void setUp() {
		linkImpl = new LinkImpl();
		linkImpl.setLinkType("Test");
		linkImpl.setIngoingId((long) 14);
		linkImpl.setOutgoingId((long) 15);
		DecisionKnowledgeElement elementIn = new DecisionKnowledgeElementImpl();
		elementIn.setId((long) 14);
		elementIn.setKey("TestIn");
		DecisionKnowledgeElement elementOut = new DecisionKnowledgeElementImpl();
		elementOut.setId((long) 15);
		elementOut.setKey("TestOut");
		linkImpl.setSourceObject(elementIn);
		linkImpl.setDestinationObject(elementOut);
	}

	@Test
	public void testConstructureEntity() {
		LinkEntity link;
		link = mock(LinkEntity.class);
		Link linkImp = new LinkImpl(link);
		assertNotNull(linkImp);
	}

	@Test
	public void testConstructorDecisionKnowledgeElement() {
		DecisionKnowledgeElement elementIn = new DecisionKnowledgeElementImpl();
		elementIn.setId((long) 14);
		elementIn.setKey("TestInCons");
		DecisionKnowledgeElement elementOut = new DecisionKnowledgeElementImpl();
		elementOut.setId((long) 15);
		elementOut.setKey("TestOutCons");
		Link impl = new LinkImpl(elementIn, elementOut);
		assertEquals("TestInCons", impl.getSourceObject().getKey());
	}

	@Test
	public void testConstructorIssueLink() {
		IssueLink link = new MockIssueLink((long) 54);
		Link linkImp = new LinkImpl(link);
		assertNotNull(linkImp);
	}

	@Test
	public void testGetLinkType() {
		assertEquals("Test", linkImpl.getLinkType());
	}

	@Test
	public void testGetOutGoingId() {
		assertEquals((long) 15, linkImpl.getOutgoingId());
	}

	@Test
	public void testGetIngoingId() {
		assertEquals((long) 14, linkImpl.getIngoingId());
	}

	@Test
	public void testSetIngoingId() {
		linkImpl.setIngoingId((long) 323);
		assertEquals((long) 323, linkImpl.getIngoingId());
	}

	@Test
	public void testSetOutGoingId() {
		linkImpl.setOutgoingId((long) 323);
		assertEquals((long) 323, linkImpl.getOutgoingId());
	}

	@Test
	public void testGetInGoingElement() {
		assertEquals("TestIn", linkImpl.getSourceObject().getKey());
	}

	@Test
	public void testSetInGoingElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setKey("TestNew");
		linkImpl.setSourceObject(element);
		assertEquals(element.getKey(), linkImpl.getSourceObject().getKey());
	}

	@Test
	public void testGetOutGoingElement() {
		assertEquals("TestOut", linkImpl.getDestinationObject().getKey());
	}

	@Test
	public void testSetOutGoingElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setKey("TestNew");
		linkImpl.setDestinationObject(element);
		assertEquals(element.getKey(), linkImpl.getDestinationObject().getKey());
	}
}
