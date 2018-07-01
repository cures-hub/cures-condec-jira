package de.uhd.ifi.se.decision.management.jira.rest.decisionsrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestDeleteLinks extends TestDecisionSetUp {

	private final static String DELETION_LINK_ERROR = "Deletion of link failed.";

	@Test
	public void testProjectKeyNullReqNullLinkNull() {
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
				decRest.deleteLinks(null, null, null).getEntity());

	}

	@Test
	public void testProjectKeyFilledReqNullLinkNull() {
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
				decRest.deleteLinks("TEST", null, null).getEntity());

	}

	@Test
	public void testProjectKeyNullReqFilledLinkNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
				decRest.deleteLinks(null, req, null).getEntity());

	}

	@Test
	public void testProjectKeyNullReqNullLinkFilled() {
		Link link = new LinkImpl();
		link.setIngoingId(14);
		link.setOutgoingId(15);
		link.setLinkType("Test");
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
				decRest.deleteLinks(null, null, link).getEntity());
	}

	@Test
	public void testProjectKeyFilledReqWithFailsLinkFilled() {
		Link link = new LinkImpl();
		link.setIngoingId(14);
		link.setOutgoingId(15);
		link.setLinkType("Test");
		req.setAttribute("WithFails", true);
		req.setAttribute("NoFails", false);
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
				decRest.deleteLinks("TEST", req, link).getEntity());
	}

	@Test
	public void testProjectKeyFilledReqNoFailsLinkFilled() {
		Link link = new LinkImpl();
		link.setIngoingId(1);
		link.setOutgoingId(15);
		link.setLinkType("Test");
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Response.Status.OK.getStatusCode(), decRest.deleteLinks("TEST", req, link).getStatus());
	}
}
