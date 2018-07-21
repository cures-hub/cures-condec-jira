package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLink;
import de.uhd.ifi.se.decision.management.jira.persistence.LinkEntity;

public class TestLinkImpl {

	private Link linkImpl;

	@Before
	public void setUp() {
		linkImpl = new LinkImpl();
		linkImpl.setType("Test");
		linkImpl.setSourceElement(14);
		linkImpl.setDestinationElement(15);
		DecisionKnowledgeElement elementIn = new DecisionKnowledgeElementImpl();
		elementIn.setId(14);
		elementIn.setKey("TestIn");
		DecisionKnowledgeElement elementOut = new DecisionKnowledgeElementImpl();
		elementOut.setId(15);
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
		elementIn.setId(14);
		elementIn.setKey("TestInCons");
		DecisionKnowledgeElement elementOut = new DecisionKnowledgeElementImpl();
		elementOut.setId(15);
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
		assertEquals("Test", linkImpl.getType());
	}

	@Test
	public void testGetOutGoingId() {
		assertEquals(15, linkImpl.getDestinationElement().getId());
	}

	@Test
	public void testGetIngoingId() {
		assertEquals(14, linkImpl.getSourceElement().getId());
	}

	@Test
	public void testSetIngoingId() {
		linkImpl.setSourceElement(323);
		assertEquals(323, linkImpl.getSourceElement().getId());
	}

	@Test
	public void testSetOutGoingId() {
		linkImpl.setDestinationElement(323);
		assertEquals(323, linkImpl.getDestinationElement().getId());
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
