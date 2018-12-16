package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestDeleteLink extends TestKnowledgeRestSetUp {

	private final static String DELETION_ERROR = "Deletion of link failed.";

	@Test
	public void testProjectKeyFilledReqNoFailsLinkFilled() {
		Link link = new LinkImpl();
		link.setSourceElement(1);
		link.setDestinationElement(15);
		link.setType("Test");
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		// TODO
		// assertEquals(Response.Status.OK.getStatusCode(),
		// knowledgeRest.deleteLinks("TEST", request, link).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestNullElementNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
				.getEntity(), knowledgeRest.deleteLink(null, null, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestNullElementFilled() {
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
						.getEntity(),
				knowledgeRest.deleteLink("TEST", null, new LinkImpl(decisionKnowledgeElement, sentence)).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
				.getEntity(), knowledgeRest.deleteLink("TEST", request, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilled() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);

		TestComment tc = new TestComment();
		Comment comment = tc.getComment("this is atest sentence");
		sentence = comment.getSentences().get(0);

		Link newLink = new LinkImpl(decisionKnowledgeElement, sentence);
		GenericLinkManager.insertLink(newLink, null);

		// Test Element existing in AO
		assertEquals(1, GenericLinkManager.getLinksForElement("i" + decisionKnowledgeElement.getId()).size());

		assertEquals(Status.OK.getStatusCode(), knowledgeRest.deleteLink("TEST", request, newLink).getStatus());

		GenericLinkManager.insertLink(newLink, null);
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.deleteLink("TEST", request, newLink).getStatus());

		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
				.getEntity(), knowledgeRest.deleteLink("TEST", request, newLink).getEntity());

		// Test element not more exisitng in AO
		assertEquals(0, GenericLinkManager.getLinksForElement("i" + decisionKnowledgeElement.getId()).size());

	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithFlippedLink() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);

		TestComment tc = new TestComment();
		Comment comment = tc.getComment("this is atest sentence");
		sentence = comment.getSentences().get(0);

		Link newLink = new LinkImpl(decisionKnowledgeElement, sentence);
		GenericLinkManager.insertLink(newLink, null);
		// Flip link
		newLink = new LinkImpl(sentence, decisionKnowledgeElement);

		// Test Element existing in AO
		assertEquals(1, GenericLinkManager.getLinksForElement("i" + decisionKnowledgeElement.getId()).size());

		assertEquals(Status.OK.getStatusCode(), knowledgeRest.deleteLink("TEST", request, newLink).getStatus());

		GenericLinkManager.insertLink(newLink, null);
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.deleteLink("TEST", request, newLink).getStatus());

		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
				.getEntity(), knowledgeRest.deleteLink("TEST", request, newLink).getEntity());

		// Test element not more exisitng in AO
		assertEquals(0, GenericLinkManager.getLinksForElement("i" + decisionKnowledgeElement.getId()).size());

	}

	@Test
	public void testProjectKeyNullReqNullLinkNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteLink(null, null, null).getEntity());

	}

	@Test
	public void testProjectKeyFilledReqNullLinkNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteLink("TEST", null, null).getEntity());
	}

	@Test
	public void testProjectKeyNullReqFilledLinkNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteLink(null, request, null).getEntity());
	}

	@Test
	public void testProjectKeyNullReqNullLinkFilled() {
		Link link = new LinkImpl();
		link.setSourceElement(14);
		link.setDestinationElement(15);
		link.setType("Test");
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteLink(null, null, link).getEntity());
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
		// assertEquals(
		// Response.status(Response.Status.BAD_REQUEST)
		// .entity(ImmutableMap.of("error", DELETION_LINK_ERROR)).build().getEntity(),
		// knowledgeRest.deleteLinks("TEST", request, link).getEntity());
	}
}
