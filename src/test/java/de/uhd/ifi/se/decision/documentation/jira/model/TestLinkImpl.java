package de.uhd.ifi.se.decision.documentation.jira.model;

import com.atlassian.jira.issue.link.IssueLink;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueLink;
import de.uhd.ifi.se.decision.documentation.jira.persistence.LinkEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TestLinkImpl {

	private LinkImpl linkImpl;

	@Before
	public void setUp() {
		linkImpl = new LinkImpl();
		linkImpl.setLinkType("Test");
		linkImpl.setIngoingId((long) 14);
		linkImpl.setOutgoingId((long) 15);
		DecisionKnowledgeElement elementIn = new DecisionKnowledgeElementImpl();
		elementIn.setId((long)14);
		elementIn.setKey("TestIn");
		DecisionKnowledgeElement elementOut = new DecisionKnowledgeElementImpl();
		elementOut.setId((long)15);
		elementOut.setKey("TestOut");
		linkImpl.setIngoingElement(elementIn);
		linkImpl.setOutgoingElement(elementOut);
	}

	@Test
	public void testConstructureEntity() {
		LinkEntity link;
		link = mock(LinkEntity.class);
		LinkImpl linkImp = new LinkImpl(link);
		assertNotNull(linkImp);
	}

	@Test
	public void testConstructorDecisionKnowledgeElement(){
		DecisionKnowledgeElement elementIn = new DecisionKnowledgeElementImpl();
		elementIn.setId((long)14);
		elementIn.setKey("TestInCons");
		DecisionKnowledgeElement elementOut = new DecisionKnowledgeElementImpl();
		elementOut.setId((long)15);
		elementOut.setKey("TestOutCons");
		LinkImpl impl = new LinkImpl(elementIn,elementOut);
		assertEquals("TestInCons", impl.getIngoingElement().getKey());
	}

	@Test
	public void testConstructorIssueLink() {
		IssueLink link = new MockIssueLink((long) 54);
		LinkImpl linkImp = new LinkImpl(link);
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
	public void testSetIngoingId(){
		linkImpl.setIngoingId((long)323);
		assertEquals((long) 323, linkImpl.getIngoingId());
	}

	@Test
	public void testSetOutGoingId(){
		linkImpl.setOutgoingId((long) 323);
		assertEquals((long) 323, linkImpl.getOutgoingId());
	}

	@Test
	public void testGetInGoingElement(){
		assertEquals("TestIn",linkImpl.getIngoingElement().getKey());
	}

	@Test
	public void testSetInGoingElement(){
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setKey("TestNew");
		linkImpl.setIngoingElement(element);
		assertEquals(element.getKey(),linkImpl.getIngoingElement().getKey());
	}

	@Test
	public void testGetOutGoingElement(){
		assertEquals("TestOut",linkImpl.getOutgoingElement().getKey());
	}

	@Test
	public void testSetOutGoingElement(){
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setKey("TestNew");
		linkImpl.setOutgoingElement(element);
		assertEquals(element.getKey(),linkImpl.getOutgoingElement().getKey());
	}
}
