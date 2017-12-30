package ut.db.strategy.impl.issueStategay;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.atlassian.DecisionDocumentation.rest.Decisions.model.LinkRepresentation;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

public class TestCreateLink extends TestIssueStartegySup {
	
	@Test
	(expected = NullPointerException.class)
	public void testLinkRepresNullUserNull() {
		issueStrat.createLink(null, null);
	}
	
	@Test
	public void testLinkRepresFilledUserNull() {
		LinkRepresentation link = new LinkRepresentation();
		link.setIngoingId(1);
		link.setLinkType("Contains");
		link.setOutgoingId(2);
		assertEquals(0,issueStrat.createLink(link, null),0.0);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testLinkRepresNullUserFilled() {
		ApplicationUser user = new MockApplicationUser("Test");
		issueStrat.createLink(null,user);
	}
	
	@Test
	public void testLinkRepresFilledUserFilled() {
		LinkRepresentation link = new LinkRepresentation();
		link.setIngoingId(1);
		link.setLinkType("Contains");
		link.setOutgoingId(2);
		ApplicationUser user = new MockApplicationUser("Test");
		issueStrat.createLink(link,user);
	}
	
	@Test
	public void testLinkRepresFilledUserFilledIssueLinkNull() {
		LinkRepresentation link = new LinkRepresentation();
		link.setIngoingId(2);
		link.setLinkType("Contains");
		link.setOutgoingId(3);
		ApplicationUser user = new MockApplicationUser("Test");
		assertEquals(0,issueStrat.createLink(link,user),0.0);
	}
}
