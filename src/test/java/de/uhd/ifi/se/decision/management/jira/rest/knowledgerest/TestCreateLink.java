package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestCreateLink extends TestKnowledgeRestSetUp {
	private EntityManager entityManager;
	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;
	private LinkImpl link;

	private final static String CREATION_ERROR = "Link could not be created due to a bad request.";

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());

		request = new MockHttpServletRequest();
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);

		link = new LinkImpl();
		link.setType("contain");
		link.setSourceElement(1);
		link.setDestinationElement(4);
	}

	// TODO Why does the following test case fail?
	// @Test
	// public void testProjectKeyFilledRequestFilledLinkIdFilled() {
	// assertEquals(Status.OK.getStatusCode(), knowledgeRest.createLink("TEST",
	// request, link).getStatus());
	// }

	@Test
	public void testProjectKeyNullRequestNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, null, null, 4, "i", 1, "i").getEntity());
	}

	@Test
	public void testProjectKeyNullRequestNullLinkIdZero() {
		link.setType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, null, null, 4, "i", 1, "i").getEntity());
	}

	public void testProjectKeyNullRequestNullLinkIdFilled() {
		link.setType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, null, null, 4, "i", 1, "i").getEntity());
	}

	@Test
	public void testProjectKeyNullRequestFilledLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(request, null, null, 4, "i", 1, "i").getEntity());
	}

	@Test
	public void testProjectKeyNullRequestFilledLinkIdZero() {
		link.setType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(request, null, null, 4, "i", 1, "i").getEntity());
	}

	@Test
	public void testProjectKeyNullRequestFilledLinkIdFilled() {
		link.setType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(request, null, null, 4, "i", 1, "i").getEntity());
	}

	@Test
	public void testProjectKeyFilledRequestNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, "TEST", null, 4, "i", 1, "i").getEntity());
	}

	@Test
	public void testProjectKeyFilledRequestNullLinkIdZero() {
		link.setType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, "TEST", null, 4, "i", 1, "i").getEntity());
	}

	@Test
	public void testProjectKeyFilledRequestNullLinkIdFilled() {
		link.setType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, "TEST", null, 4, "i", 1, "i").getEntity());
	}

	@Test
	public void testProjectKeyFilledRequestFilledLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(request, "TEST", null, 0, "i", 1, "i").getEntity());
	}

	// TODO
	@Ignore
	@Test
	@NonTransactional
	public void testProjectKeyFilledRequestFilledLinkIdZero() {
		link.setType("Zero");
		link.setSourceElement(3);
		link.getSourceElement().setProject("TEST");
		link.setDocumentationLocationOfDestinationElement("s");
		link.setDocumentationLocationOfDestinationElement("s");
		link.getSourceElement().setType("Decision");
		link.getDestinationElement().setType("Decision");
		assertEquals(
				Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
						.getEntity(),
				knowledgeRest.createLink(request, "create", "Decision", 4, "s", 3, "s").getEntity());
	}

	@Test
	public void testProjectKeyNonExistentRequestNullLinkNull() {
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
						.getEntity(),
				knowledgeRest.createLink(null, "notCreate", "Decision", 4, "s", 3, "s").getEntity());
	}

	@Test
	public void testProjectKeyNonExistentRequestNullLinkIdZero() {
		link.setType("Zero");
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
						.getEntity(),
				knowledgeRest.createLink(null, "notCreate", "Decision", 4, "s", 3, "s").getEntity());
	}

	private Link newGenericLink() {
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		return GenericLinkManager.getLinksForElement("s" + id).get(0);
	}

	@Test
	@NonTransactional
	public void testRequestNullElementNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, null, "Decision", 4, "s", 3, "s").getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestNullElementFilled() {
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("This is a test sentence.");
		decisionKnowledgeElement = comment.getSentences().get(0);
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
						.getEntity(),
				knowledgeRest.createLink(null, "TEST", "Decision", comment.getIssueId(), "s", 3, "s").getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(request, "TEST", null, 0, "s", 3, "s").getEntity());
	}

	@Ignore
	@Test
	@NonTransactional
	public void testRequestFilledElementFilled() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		Link gl = newGenericLink();
		gl.setId(0);
		GenericLinkManager.insertLink(gl, null);
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", "Decision", 0, "s", 3, "s").getStatus());
	}
}
