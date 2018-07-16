package de.uhd.ifi.se.decision.management.jira.model;

import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLink;
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
		linkImpl.setIdOfSourceElement((long) 14);
		linkImpl.setIdOfDestinationElement((long) 15);
		DecisionKnowledgeElement elementIn = new DecisionKnowledgeElementImpl();
		elementIn.setId((long) 14);
		elementIn.setKey("TestIn");
		DecisionKnowledgeElement elementOut = new DecisionKnowledgeElementImpl();
		elementOut.setId((long) 15);
		elementOut.setKey("TestOut");
		linkImpl.setSourceElement(elementIn);
		linkImpl.setDestinationElement(elementOut);
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
		assertEquals("TestInCons", impl.getSourceElement().getKey());
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
		assertEquals((long) 15, linkImpl.getIdOfDestinationElement());
	}

	@Test
	public void testGetIngoingId() {
		assertEquals((long) 14, linkImpl.getIdOfSourceElement());
	}

	@Test
	public void testSetIngoingId() {
		linkImpl.setIdOfSourceElement((long) 323);
		assertEquals((long) 323, linkImpl.getIdOfSourceElement());
	}

	@Test
	public void testSetOutGoingId() {
		linkImpl.setIdOfDestinationElement((long) 323);
		assertEquals((long) 323, linkImpl.getIdOfDestinationElement());
	}

	@Test
	public void testGetInGoingElement() {
		assertEquals("TestIn", linkImpl.getSourceElement().getKey());
	}

	@Test
	public void testSetInGoingElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setKey("TestNew");
		linkImpl.setSourceElement(element);
		assertEquals(element.getKey(), linkImpl.getSourceElement().getKey());
	}

	@Test
	public void testGetOutGoingElement() {
		assertEquals("TestOut", linkImpl.getDestinationElement().getKey());
	}

	@Test
	public void testSetOutGoingElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setKey("TestNew");
		linkImpl.setDestinationElement(element);
		assertEquals(element.getKey(), linkImpl.getDestinationElement().getKey());
	}
}
