package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLink;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

/**
 * Test class for links between decision knowledge elements
 */
public class TestLink extends TestSetUp {

	protected static ApplicationUser user;
	protected Link link;

	@BeforeClass
	public static void setUpBeforeClass() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Before
	public void setUp() {
		link = new LinkImpl(1, 4, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType(LinkType.RELATE.toString());
	}

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
		assertEquals(1, link.getSource().getId());
	}

	@Test
	public void testGetIdOfDestinationElement() {
		assertEquals(4, link.getTarget().getId());
	}

	@Test
	public void testSetSourceElementById() {
		link.setSourceElement(2, "i");
		assertEquals(2, link.getSource().getId(), 0.0);
	}

	@Test
	public void testSetDestinationElementById() {
		long oldId = link.getTarget().getId();
		link.setDestinationElement(5, "i");
		assertEquals(5, link.getTarget().getId(), 0.0);
		link.setDestinationElement(oldId, "i");
	}

	@Test
	public void testGetSourceElement() {
		assertEquals("TEST-1", link.getSource().getKey());
	}

	@Test
	public void testSetSourceElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		DecisionKnowledgeElement oldElement = link.getSource();
		element.setKey("TestNew");
		link.setSourceElement(element);
		assertEquals(element.getKey(), link.getSource().getKey());
		link.setSourceElement(oldElement);
	}

	@Test
	public void testGetDestinationElement() {
		assertEquals("TEST-4", link.getTarget().getKey());
	}

	@Test
	public void testSetDestinationElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		DecisionKnowledgeElement oldElement = link.getTarget();
		element.setKey("TestNew");
		link.setDestinationElement(element);
		assertEquals(element.getKey(), link.getTarget().getKey());
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
		assertEquals("TestSourceElement", link.getSource().getKey());
	}

	@Test
	public void testConstructorIssueLink() {
		IssueLink issueLink = new MockIssueLink(1, 2, 1);
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
	public void testGetSource() {
		assertEquals(link.getSource().getId(), link.getSource().getId());
	}

	@Test
	public void testGetTarget() {
		assertEquals(link.getTarget().getId(), link.getTarget().getId());
	}

	@Test
	public void testGetWeight() {
		assertEquals(1.0, link.getWeight(), 0.0);
	}

	@Test
	public void testEqualsNull() {
		assertFalse(link.equals((Object) null));
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
		Link linkEquals = new LinkImpl(link.getSource(), link.getTarget());
		assertTrue(link.equals(linkEquals));
	}

	@Test
	public void testLinkIdZeroElementSame() {
		Link newLink = new LinkImpl(link.getSource(), link.getTarget());
		long linkId = newLink.getId();
		newLink.setId(0);
		assertEquals(linkId, newLink.getId());
	}
}