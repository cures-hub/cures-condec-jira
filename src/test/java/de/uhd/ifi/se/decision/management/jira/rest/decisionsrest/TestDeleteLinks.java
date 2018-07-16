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
				decisionsRest.deleteLinks(null, null, null).getEntity());

	}

	@Test
	public void testProjectKeyFilledReqNullLinkNull() {
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
				decisionsRest.deleteLinks("TEST", null, null).getEntity());

	}

	@Test
	public void testProjectKeyNullReqFilledLinkNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
				decisionsRest.deleteLinks(null, request, null).getEntity());

	}

	@Test
	public void testProjectKeyNullReqNullLinkFilled() {
		Link link = new LinkImpl();
		link.setIdOfSourceElement(14);
		link.setIdOfDestinationElement(15);
		link.setLinkType("Test");
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
				decisionsRest.deleteLinks(null, null, link).getEntity());
	}

	@Test
	public void testProjectKeyFilledReqWithFailsLinkFilled() {
		Link link = new LinkImpl();
		link.setIdOfSourceElement(14);
		link.setIdOfDestinationElement(15);
		link.setLinkType("Test");
		request.setAttribute("WithFails", true);
		request.setAttribute("NoFails", false);
		// TODO
//		assertEquals(
//				Response.status(Response.Status.BAD_REQUEST)
//						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
//				decisionsRest.deleteLinks("TEST", request, link).getEntity());
	}

	@Test
	public void testProjectKeyFilledReqNoFailsLinkFilled() {
		Link link = new LinkImpl();
		link.setIdOfSourceElement(1);
		link.setIdOfDestinationElement(15);
		link.setLinkType("Test");
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		// TODO
//		assertEquals(Response.Status.OK.getStatusCode(), decisionsRest.deleteLinks("TEST", request, link).getStatus());
	}
}
