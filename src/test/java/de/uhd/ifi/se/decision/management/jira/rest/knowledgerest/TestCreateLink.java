package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestCreateLink extends TestSetUp {
	private EntityManager entityManager;
	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;
	private LinkImpl link;

	private final static String CREATION_ERROR = "Creation of link failed.";

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());

		request = new MockHttpServletRequest();
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);

		link = new LinkImpl();
		link.setSourceElement(1);
		link.setDestinationElement(4);
	}

	@Test
	public void testProjectKeyNullRequestNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, null, null).getEntity());
	}

	@Test
	public void testProjectKeyNullRequestNullLinkIdZero() {
		link.setType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, null, link).getEntity());
	}

	public void testProjectKeyNullRequestNullLinkIdFilled() {
		link.setType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, null, link).getEntity());
	}

	@Test
	public void testProjectKeyNullRequestFilledLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, request, null).getEntity());
	}

	@Test
	public void testProjectKeyNullRequestFilledLinkIdZero() {
		link.setType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, request, link).getEntity());
	}

	@Test
	public void testProjectKeyNullRequestFilledLinkIdFilled() {
		link.setType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, request, link).getEntity());
	}

	@Test
	public void testProjectKeyFilledRequestNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink("TEST", null, null).getEntity());
	}

	@Test
	public void testProjectKeyFilledRequestNullLinkIdZero() {
		link.setType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink("TEST", null, link).getEntity());
	}

	@Test
	public void testProjectKeyFilledRequestNullLinkIdFilled() {
		link.setType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink("TEST", null, link).getEntity());
	}

	@Test
	public void testProjectKeyFilledRequestFilledLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink("TEST", request, null).getEntity());
	}

	@Test
	public void testProjectKeyFilledRequestFilledLinkIdZero() {
		link.setType("Zero");
		link.setSourceElement(3);
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", CREATION_ERROR))
				.build().getEntity(), knowledgeRest.createLink("create", request, link).getEntity());
	}

	@Test
	public void testProjectKeyFilledRequestFilledLinkIdFilled() {
		link.setType("Ok");
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.createLink("create", request, link).getStatus());
	}

	@Test
	public void testProjectKeyNonExistentRequestNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink("notCreate", null, null).getEntity());
	}

	@Test
	public void testProjectKeyNonExistentRequestNullLinkIdZero() {
		link.setType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink("notCreate", null, link).getEntity());
	}

	@Test
	public void testactionTypeNotCreateKeyNullReqNullLinkIdFilled() {
		link.setType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink("notCreate", null, link).getEntity());
	}

	@Test
	public void testactionTypeNotCreateKeyNullReqFilledLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink("notCreate", request, null).getEntity());
	}

	@Test
	public void testactionTypeNotCreateKeyFilledReqNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink("notCreate", null, null).getEntity());
	}

	@Test
	public void testactionTypeNotCreateKeyFilledReqNullLinkIdZero() {
		link.setType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink("notCreate", null, link).getEntity());
	}

	@Test
	public void testactionTypeNotCreateKeyFilledReqNullLinkIdFilled() {
		link.setType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink("notCreate", null, link).getEntity());
	}

	@Test
	public void testactionTypeNotCreateKeyFilledReqFilledLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink("notCreate", request, null).getEntity());
	}
}
