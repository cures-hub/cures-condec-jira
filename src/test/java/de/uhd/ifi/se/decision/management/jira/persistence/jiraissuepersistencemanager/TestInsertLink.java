package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestInsertLink extends TestJiraIssuePersistenceManagerSetUp {

	@Test(expected = NullPointerException.class)
	public void testLinkNullUserNull() {
		AbstractPersistenceManager.insertLink(null, null);
	}

	@Test
	public void testLinkFilledUserNull() {
		Link link = new LinkImpl();
		link.setSourceElement(1, "i");
		link.setType("contains");
		link.setDestinationElement(2, "i");
		link.getDestinationElement().setProject("TEST");
		link.getSourceElement().setProject("TEST");
		assertEquals(0, AbstractPersistenceManager.insertLink(link, null), 0.0);
	}

	@Test(expected = NullPointerException.class)
	public void testLinkNullUserFilled() {
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		AbstractPersistenceManager.insertLink(null, user);
	}

	@Test
	public void testLinkFilledUserFilled() {
		Link link = new LinkImpl();
		link.setSourceElement(1, "i");
		link.setType("contains");
		link.setDestinationElement(2, "i");
		link.getDestinationElement().setProject("TEST");
		link.getSourceElement().setProject("TEST");
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		long linkId = AbstractPersistenceManager.insertLink(link, user);
		assertNotNull(linkId);
	}

	@Test
	public void testLinkFilledUserFilledIssueLinkNull() {
		Link link = new LinkImpl();
		link.setSourceElement(2, "i");
		link.setDocumentationLocationOfSourceElement("i");
		link.setType("contains");
		link.setDestinationElement(3, "i");
		link.setDocumentationLocationOfDestinationElement("i");
		link.getDestinationElement().setProject("TEST");
		link.getSourceElement().setProject("TEST");
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		assertEquals(0, AbstractPersistenceManager.insertLink(link, user));
	}

	@Test
	public void testCreateException() {
		Link link = new LinkImpl();
		link.setSourceElement(2, "i");
		link.setType("contains");
		link.setDestinationElement(3, "i");
		link.getDestinationElement().setProject("TEST");
		link.getSourceElement().setProject("TEST");
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		assertEquals(0, AbstractPersistenceManager.insertLink(link, user));
	}

	@Test
	public void testMoreInwardLinks() {
		Link link = new LinkImpl();
		link.setSourceElement(30, "i");
		link.setType("Contains");
		link.setDestinationElement(3, "i");
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		assertEquals(0, AbstractPersistenceManager.insertLink(link, user));
	}

	@Test
	public void testMoreOutwardLinks() {
		Link link = new LinkImpl();
		link.setSourceElement(10, "i");
		link.setType("Contains");
		link.setDestinationElement(30, "i");
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		assertEquals(0, AbstractPersistenceManager.insertLink(link, user));
	}
}
