package ut.de.uhd.ifi.se.decdoc.jira.db.strategy.impl.issueStrategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decdoc.jira.rest.decisions.model.LinkRepresentation;

/**
 * @author Tim Kuchenbuch
 */
public class TestCreateLink extends TestIssueStrategySetUp {
	
	@Test
	(expected = NullPointerException.class)
	public void testLinkRepresNullUserNull() {
		issueStrategy.createLink(null, null);
	}
	
	@Test
	public void testLinkRepresFilledUserNull() {
		LinkRepresentation link = new LinkRepresentation();
		link.setIngoingId(1);
		link.setLinkType("Contains");
		link.setOutgoingId(2);
		assertEquals(0,issueStrategy.createLink(link, null),0.0);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testLinkRepresNullUserFilled() {
		ApplicationUser user = new MockApplicationUser("Test");
		issueStrategy.createLink(null,user);
	}
	
	@Test
	public void testLinkRepresFilledUserFilled() {
		LinkRepresentation link = new LinkRepresentation();
		link.setIngoingId(1);
		link.setLinkType("Contains");
		link.setOutgoingId(2);
		ApplicationUser user = new MockApplicationUser("Test");
		issueStrategy.createLink(link,user);
	}
	
	@Test
	public void testLinkRepresFilledUserFilledIssueLinkNull() {
		LinkRepresentation link = new LinkRepresentation();
		link.setIngoingId(2);
		link.setLinkType("Contains");
		link.setOutgoingId(3);
		ApplicationUser user = new MockApplicationUser("Test");
		assertEquals(0,issueStrategy.createLink(link,user),0.0);
	}
	
	@Test
	public void testCreateException() {
		LinkRepresentation link = new LinkRepresentation();
		link.setIngoingId(2);
		link.setLinkType("Contains");
		link.setOutgoingId(3);
		ApplicationUser user = new MockApplicationUser("CreateExecption");
		assertEquals(0,issueStrategy.createLink(link,user),0.0);
	}
	
	@Test
	public void testMoreInwardLinks() {
		LinkRepresentation link = new LinkRepresentation();
		link.setIngoingId(30);
		link.setLinkType("Contains");
		link.setOutgoingId(3);
		ApplicationUser user = new MockApplicationUser("Test");
		assertEquals(0,issueStrategy.createLink(link,user),0.0);
	}
	
	@Test
	public void testMoreOutwardLinks() {
		LinkRepresentation link = new LinkRepresentation();
		link.setIngoingId(10);
		link.setLinkType("Contains");
		link.setOutgoingId(30);
		ApplicationUser user = new MockApplicationUser("Test");
		assertEquals(0,issueStrategy.createLink(link,user),0.0);
	}
}
