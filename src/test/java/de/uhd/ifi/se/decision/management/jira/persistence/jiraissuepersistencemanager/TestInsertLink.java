package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;

public class TestInsertLink extends TestJiraIssuePersistenceManagerSetUp {

	@Test(expected = NullPointerException.class)
	public void testLinkNullUserNull() {
		AbstractPersistenceManager.insertLink(null, null);
	}

	@Test
	@Ignore
	public void testLinkFilledUserNull() {
		Link link = new LinkImpl();
		link.setSourceElement(1);
		link.setType("Contains");
		link.setDestinationElement(2);
		assertEquals(0, AbstractPersistenceManager.insertLink(link, null), 0.0);
	}

	@Test(expected = NullPointerException.class)
	public void testLinkNullUserFilled() {
		ApplicationUser user = new MockApplicationUser("Test");
		AbstractPersistenceManager.insertLink(null, user);
	}

	@Test
	@Ignore
	public void testLinkFilledUserFilled() {
		Link link = new LinkImpl();
		link.setSourceElement(1);
		link.setType("Contains");
		link.setDestinationElement(2);
		ApplicationUser user = new MockApplicationUser("Test");
		long linkId = AbstractPersistenceManager.insertLink(link, user);
		assertNotNull(linkId);
	}

	@Test
	@Ignore
	public void testLinkFilledUserFilledIssueLinkNull() {
		Link link = new LinkImpl();
		link.setSourceElement(2);
		link.setDocumentationLocationOfSourceElement("a");
		link.setType("Contains");
		link.setDestinationElement(3);
		link.setDocumentationLocationOfDestinationElement("a");
		ApplicationUser user = new MockApplicationUser("Test");
		assertEquals(0, AbstractPersistenceManager.insertLink(link, user));
	}

	@Test
	@Ignore
	public void testCreateException() {
		Link link = new LinkImpl();
		link.setSourceElement(2);
		link.setType("Contains");
		link.setDestinationElement(3);
		ApplicationUser user = new MockApplicationUser("CreateExecption");
		assertEquals(0, AbstractPersistenceManager.insertLink(link, user));
	}

	@Test
	@Ignore
	public void testMoreInwardLinks() {
		Link link = new LinkImpl();
		link.setSourceElement(30);
		link.setType("Contains");
		link.setDestinationElement(3);
		ApplicationUser user = new MockApplicationUser("Test");
		assertEquals(0, AbstractPersistenceManager.insertLink(link, user));
	}

	@Test
	@Ignore
	public void testMoreOutwardLinks() {
		Link link = new LinkImpl();
		link.setSourceElement(10);
		link.setType("Contains");
		link.setDestinationElement(30);
		ApplicationUser user = new MockApplicationUser("Test");
		assertEquals(0, AbstractPersistenceManager.insertLink(link, user));
	}
}
