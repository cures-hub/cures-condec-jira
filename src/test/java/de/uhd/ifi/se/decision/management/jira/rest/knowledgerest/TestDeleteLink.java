package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestDeleteLink extends TestKnowledgeRestSetUp {

	private final static String DELETION_LINK_ERROR = "Deletion of link failed.";

	@Test
	public void testProjectKeyNullReqNullLinkNull() {
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
				knowledgeRest.deleteLink(null, null, null).getEntity());

	}

	@Test
	public void testProjectKeyFilledReqNullLinkNull() {
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
				knowledgeRest.deleteLink("TEST", null, null).getEntity());

	}

	@Test
	public void testProjectKeyNullReqFilledLinkNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
				knowledgeRest.deleteLink(null, request, null).getEntity());

	}

	@Test
	public void testProjectKeyNullReqNullLinkFilled() {
		Link link = new LinkImpl();
		link.setSourceElement(14);
		link.setDestinationElement(15);
		link.setType("Test");
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
				knowledgeRest.deleteLink(null, null, link).getEntity());
	}

	@Test
	public void testProjectKeyFilledReqWithFailsLinkFilled() {
		Link link = new LinkImpl();
		link.setSourceElement(14);
		link.setDestinationElement(15);
		link.setType("Test");
		request.setAttribute("WithFails", true);
		request.setAttribute("NoFails", false);
		// TODO
//		assertEquals(
//				Response.status(Response.Status.BAD_REQUEST)
//						.entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
//				knowledgeRest.deleteLinks("TEST", request, link).getEntity());
	}

	@Test
	public void testProjectKeyFilledReqNoFailsLinkFilled() {
		Link link = new LinkImpl();
		link.setSourceElement(1);
		link.setDestinationElement(15);
		link.setType("Test");
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		// TODO
//		assertEquals(Response.Status.OK.getStatusCode(), knowledgeRest.deleteLinks("TEST", request, link).getStatus());
	}
}
