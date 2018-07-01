package de.uhd.ifi.se.decision.documentation.jira.persistence.issuestrategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;

public class TestCreateLink extends TestIssueStrategySetUp {

	@Test
	(expected = NullPointerException.class)
	public void testLinkRepresNullUserNull() {
		issueStrategy.insertLink(null, null);
	}
//
//	@Test
//	public void testLinkRepresFilledUserNull() {
//		LinkImpl link = new LinkImpl();
//		link.setIngoingId(1);
//		link.setLinkType("Contains");
//		link.setOutgoingId(2);
//		assertEquals(0,issueStrategy.insertLink(link, null),0.0);
//	}
//
	@Test
	(expected = NullPointerException.class)
	public void testLinkRepresNullUserFilled() {
		ApplicationUser user = new MockApplicationUser("Test");
		issueStrategy.insertLink(null,user);
	}
//
//	@Test
//	public void testLinkRepresFilledUserFilled() {
//		LinkImpl link = new LinkImpl();
//		link.setIngoingId(1);
//		link.setLinkType("Contains");
//		link.setOutgoingId(2);
//		ApplicationUser user = new MockApplicationUser("Test");
//		issueStrategy.insertLink(link,user);
//	}
//
//	@Test
//	public void testLinkRepresFilledUserFilledIssueLinkNull() {
//		LinkImpl link = new LinkImpl();
//		link.setIngoingId(2);
//		link.setLinkType("Contains");
//		link.setOutgoingId(3);
//		ApplicationUser user = new MockApplicationUser("Test");
//		assertEquals(0,issueStrategy.insertLink(link,user),0.0);
//	}
//
//	@Test
//	public void testCreateException() {
//		LinkImpl link = new LinkImpl();
//		link.setIngoingId(2);
//		link.setLinkType("Contains");
//		link.setOutgoingId(3);
//		ApplicationUser user = new MockApplicationUser("CreateExecption");
//		assertEquals(0,issueStrategy.insertLink(link,user),0.0);
//	}
//
//	@Test
//	public void testMoreInwardLinks() {
//		LinkImpl link = new LinkImpl();
//		link.setIngoingId(30);
//		link.setLinkType("Contains");
//		link.setOutgoingId(3);
//		ApplicationUser user = new MockApplicationUser("Test");
//		assertEquals(0,issueStrategy.insertLink(link,user),0.0);
//	}
//
//	@Test
//	public void testMoreOutwardLinks() {
//		LinkImpl link = new LinkImpl();
//		link.setIngoingId(10);
//		link.setLinkType("Contains");
//		link.setOutgoingId(30);
//		ApplicationUser user = new MockApplicationUser("Test");
//		assertEquals(0,issueStrategy.insertLink(link,user),0.0);
//	}
}
