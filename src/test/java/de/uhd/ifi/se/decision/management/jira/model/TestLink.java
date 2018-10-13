package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.extraction.persistence.LinkBetweenDifferentEntitiesEntity;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLink;

/**
 * Test class for links between decision knowledge elements
 */
public class TestLink {
	private String type;
	private long idOfSourceElement;
	private long idOfDestinationElement;
	private Link link;

	@Before
	public void setUp() {
		type = "contain";
		idOfSourceElement = 14;
		idOfDestinationElement = 15;
		link = new LinkImpl();
		link.setType(type);
		DecisionKnowledgeElement sourceElement = new DecisionKnowledgeElementImpl();
		sourceElement.setId(idOfSourceElement);
		sourceElement.setKey("TestSourceElement");
		DecisionKnowledgeElement destinationElement = new DecisionKnowledgeElementImpl();
		destinationElement.setId(idOfDestinationElement);
		destinationElement.setKey("TestDestinationElement");
		link.setSourceElement(sourceElement);
		link.setDestinationElement(destinationElement);
	}

	@Test
	public void testGetType() {
		assertEquals(type, link.getType());
	}

	@Test
	public void testSetType() {
		link.setType(type + "New");
		assertEquals(type + "New", link.getType());
	}

	@Test
	public void testGetIdOfSourceElement() {
		assertEquals(idOfSourceElement, link.getSourceElement().getId(), 0.0);
	}

	@Test
	public void testGetIdOfDestinationElement() {
		assertEquals(idOfDestinationElement, link.getDestinationElement().getId(), 0.0);
	}

	@Test
	public void testSetSourceElementById() {
		link.setSourceElement(idOfSourceElement + 1);
		assertEquals(idOfSourceElement + 1, link.getSourceElement().getId(), 0.0);
	}

	@Test
	public void testSetDestinationElementById() {
		link.setDestinationElement(idOfDestinationElement + 1);
		assertEquals(idOfDestinationElement + 1, link.getDestinationElement().getId(), 0.0);
	}

	@Test
	public void testGetSourceElement() {
		assertEquals("TestSourceElement", link.getSourceElement().getKey());
	}

	@Test
	public void testSetSourceElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setKey("TestNew");
		link.setSourceElement(element);
		assertEquals(element.getKey(), link.getSourceElement().getKey());
	}

	@Test
	public void testGetDestinationElement() {
		assertEquals("TestDestinationElement", link.getDestinationElement().getKey());
	}

	@Test
	public void testSetDestinationElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setKey("TestNew");
		link.setDestinationElement(element);
		assertEquals(element.getKey(), link.getDestinationElement().getKey());
	}

	@Test
	public void testConstructorLinkEntity() {
		LinkBetweenDifferentEntitiesEntity linkEntity = mock(LinkBetweenDifferentEntitiesEntity.class);
		Link link = new LinkImpl(linkEntity);
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
		IssueLink issueLink = new MockIssueLink((long) 54);
		Link link = new LinkImpl(issueLink);
		assertNotNull(link);
	}

}