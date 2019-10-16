package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager.TestJiraIssuePersistenceManagerSetUp;
import org.junit.Test;

import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLink;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;

/**
 * Test class for links between decision knowledge elements
 */
public class TestLink extends TestJiraIssuePersistenceManagerSetUp {

	@Test
	public void testGetType() {
		assertEquals(LinkType.RELATE.toString(), link.getType().toLowerCase());
	}

	@Test
	public void testSetType() {
		link.setType(LinkType.RELATE.toString() + "New");
		assertEquals(LinkType.RELATE.toString() + "New", link.getType());
	}

	@Test
	public void testGetIdOfSourceElement() {
		assertEquals(1, link.getSourceElement().getId(), 0.0);
	}

	@Test
	public void testGetIdOfDestinationElement() {
		assertEquals(4, link.getDestinationElement().getId(), 0.0);
	}

	@Test
	public void testSetSourceElementById() {
		link.setSourceElement(2, "i");
		assertEquals(2, link.getSourceElement().getId(), 0.0);
	}

	@Test
	public void testSetDestinationElementById() {
		long oldId = link.getDestinationElement().getId();
		link.setDestinationElement(5, "i");
		assertEquals(5, link.getDestinationElement().getId(), 0.0);
		link.setDestinationElement(oldId, "i");
	}

	@Test
	public void testGetSourceElement() {
		assertEquals("TEST-1", link.getSourceElement().getKey());
	}

	@Test
	public void testSetSourceElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		DecisionKnowledgeElement oldElement = link.getSourceElement();
		element.setKey("TestNew");
		link.setSourceElement(element);
		assertEquals(element.getKey(), link.getSourceElement().getKey());
		link.setSourceElement(oldElement);
	}

	@Test
	public void testGetDestinationElement() {
		assertEquals("TEST-4", link.getDestinationElement().getKey());
	}

	@Test
	public void testSetDestinationElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		DecisionKnowledgeElement oldElement = link.getDestinationElement();
		element.setKey("TestNew");
		link.setDestinationElement(element);
		assertEquals(element.getKey(), link.getDestinationElement().getKey());
		link.setDestinationElement(oldElement);
	}

	@Test
	public void testConstructorLinkEntity() {
		LinkInDatabase linkInDatabase = mock(LinkInDatabase.class);
		Link link = new LinkImpl(linkInDatabase);
		assertNotNull(link);
	}

	@Test
	public void testConstructorDecisionKnowledgeElement() {
		DecisionKnowledgeElement sourceElement = new DecisionKnowledgeElementImpl();
		sourceElement.setId(14);
		sourceElement.setKey("TestSourceElement");
		DecisionKnowledgeElement destinationElement = new DecisionKnowledgeElementImpl();
		destinationElement.setId(15);
		destinationElement.setKey("TestDestinationElement");
		Link link = new LinkImpl(sourceElement, destinationElement);
		assertEquals("TestSourceElement", link.getSourceElement().getKey());
	}

	@Test
	public void testConstructorIssueLink() {
		IssueLink issueLink = new MockIssueLink(1, 2,1);
		Link link = new LinkImpl(issueLink);
		assertNotNull(link);
	}

	@Test
	public void testSetIdOfSourceElement() {
		long oldId = link.getSource().getId();
		link.setIdOfSourceElement(231);
		assertEquals(231, link.getSource().getId());
		link.setIdOfSourceElement(oldId);
	}

	@Test
	public void testSetIdOfDestinationElement() {
		long oldId = link.getTarget().getId();
		link.setIdOfDestinationElement(231);
		assertEquals(231, link.getTarget().getId());
		link.setIdOfDestinationElement(oldId);
	}

	@Test
	public void testGetSource(){
		assertEquals(link.getSourceElement().getId(), link.getSource().getId());
	}

	@Test
	public void testGetTarget() {
		assertEquals(link.getDestinationElement().getId(), link.getTarget().getId());
	}

	@Test
	public void testGetWeight() {
		assertEquals(1.0, link.getWeight(), 0.0);
	}

	@Test
	public void testEqualsNull() {
		assertFalse(link.equals((Object)null));
	}

	@Test
	public void testEqualsNotLink() {
		assertFalse(link.equals(new DecisionKnowledgeElementImpl()));
	}

	@Test
	public void testEqualsSelf() {
		assertTrue(link.equals(link));
	}

	@Test
	public void testEqualsEquals() {
		Link linkEquals = new LinkImpl(link.getSourceElement(), link.getDestinationElement());
		assertTrue(link.equals(linkEquals));
	}

	@Test
	public void testLinkIdZeroElementSame() {
		Link newLink = new LinkImpl(link.getSourceElement(), link.getDestinationElement());
		long linkId = newLink.getId();
		newLink.setId(0);
		assertEquals(linkId, newLink.getId());
	}
}