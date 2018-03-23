package de.uhd.ifi.se.decision.documentation.jira.persistence.DecisionRest;

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

import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.Link;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.documentation.jira.persistence.DecisionsRest;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestPutLink extends TestSetUp {
	private EntityManager entityManager;
	private DecisionsRest decRest;
	private HttpServletRequest req;
	private Link link;
	
	@Before
	public void setUp() {
		decRest= new DecisionsRest();
		initialisation();
		new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());
		
		req = new MockHttpServletRequest();
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		
		link = new Link();
		link.setIngoingId(1);
		link.setOutgoingId(4);
	}

	@Test
	public void testactionTypeNullKeyNullReqNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink(null, null, null, null).getEntity());
	}
	
	@Test
	public void testactionTypeNullKeyNullReqNullLinkIdZero() {		
		link.setLinkType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink(null, null, null, link).getEntity());
	}
	
	@Test
	public void testactionTypeNullKeyNullReqNullLinkIdFilled() {
		link.setLinkType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink(null, null, null, link).getEntity());
	}
	
	@Test
	public void testactionTypeNullKeyNullReqFilledLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink(null, null, req, null).getEntity());
	}
	
	@Test
	public void testactionTypeNullKeyNullReqFilledLinkIdZero() {
		link.setLinkType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink(null, null, req, link).getEntity());
	}
	
	@Test
	public void testactionTypeNullKeyNullReqFilledLinkIdFilled() {
		link.setLinkType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink(null, null, req, link).getEntity());
	}
	
	@Test
	public void testactionTypeNullKeyFilledReqNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink(null, "TEST", null, null).getEntity());
	}
	
	@Test
	public void testactionTypeNullKeyFilledReqNullLinkIdZero() {
		link.setLinkType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink(null, "TEST", null, link).getEntity());
	}
	
	@Test
	public void testactionTypeNullKeyFilledReqNullLinkIdFilled() {
		link.setLinkType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink(null, "TEST", null, link).getEntity());
	}
	
	@Test
	public void testactionTypeNullKeyFilledReqFilledLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink(null, "TEST", req, null).getEntity());
	}
	
	@Test
	public void testactionTypeNullKeyFilledReqFilledLinkIdZero() {
		link.setLinkType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink(null, "TEST", req, link).getEntity());
	}
	
	@Test
	public void testactionTypeNullKeyFilledReqFilledLinkIdFilled() {
		link.setLinkType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink(null, "TEST", req, link).getEntity());
	}
	
	@Test
	public void testactionTypeCreateKeyNullReqNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("create", null, null, null).getEntity());
	}
	
	@Test
	public void testactionTypeCreateKeyNullReqNullLinkIdZero() {
		link.setLinkType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("create", null, null, link).getEntity());
	}
	
	@Test
	public void testactionTypeCreateKeyNullReqNullLinkIdFilled() {
		link.setLinkType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("create", null, null, link).getEntity());
	}
	
	@Test
	public void testactionTypeCreateKeyNullReqFilledLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("create", null, req, null).getEntity());
	}
	
	@Test
	public void testactionTypeCreateKeyNullReqFilledLinkIdZero() {
		link.setLinkType("Zero");
		link.setIngoingId(3);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("create", null, req, link).getEntity());
	}
	
	@Test
	public void testactionTypeCreateKeyNullReqFilledLinkIdFilled() {
		link.setLinkType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("create", null, req, link).getEntity());
	}
	
	@Test
	public void testactionTypeCreateKeyFilledReqNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("create", "TEST", null, null).getEntity());
	}
	
	@Test
	public void testactionTypeCreateKeyFilledReqNullLinkIdZero() {
		link.setLinkType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("create", "TEST", null, link).getEntity());
	}
	
	@Test
	public void testactionTypeCreateKeyFilledReqNullLinkIdFilled() {
		link.setLinkType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("create", "TEST", null, link).getEntity());
	}
	
	@Test
	public void testactionTypeCreateKeyFilledReqFilledLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("create", "TEST", req, null).getEntity());
	}
	
	@Test
	public void testactionTypeCreateKeyFilledReqFilledLinkIdZero() {
		link.setLinkType("Zero");
		link.setIngoingId(3);
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Creation of Link failed.")).build().getEntity(),decRest.putLink("create", "TEST", req, link).getEntity());
	}
	
	@Test
	public void testactionTypeCreateKeyFilledReqFilledLinkIdFilled() {
		link.setLinkType("Ok");
		assertEquals(Status.OK.getStatusCode(),decRest.putLink("create", "TEST", req, link).getStatus());
	}
	
	@Test
	public void testactionTypeNotCreateKeyNullReqNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("notCreate", null, null, null).getEntity());
	}
	
	@Test
	public void testactionTypeNotCreateKeyNullReqNullLinkIdZero() {
		link.setLinkType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("notCreate", null, null, link).getEntity());
	}
	
	@Test
	public void testactionTypeNotCreateKeyNullReqNullLinkIdFilled() {
		link.setLinkType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("notCreate", null, null, link).getEntity());
	}
	
	@Test
	public void testactionTypeNotCreateKeyNullReqFilledLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("notCreate", null, req, null).getEntity());
	}
	
	@Test
	public void testactionTypeNotCreateKeyNullReqFilledLinkIdZero() {
		link.setLinkType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("notCreate", null, req, link).getEntity());
	}
	
	@Test
	public void testactionTypeNotCreateKeyNullReqFilledLinkIdFilled() {
		link.setLinkType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("notCreate", null, req, link).getEntity());
	}
	
	@Test
	public void testactionTypeNotCreateKeyFilledReqNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("notCreate", "TEST", null, null).getEntity());
	}
	
	@Test
	public void testactionTypeNotCreateKeyFilledReqNullLinkIdZero() {
		link.setLinkType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("notCreate", "TEST", null, link).getEntity());
	}
	
	@Test
	public void testactionTypeNotCreateKeyFilledReqNullLinkIdFilled() {
		link.setLinkType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("notCreate", "TEST", null, link).getEntity());
	}
	
	@Test
	public void testactionTypeNotCreateKeyFilledReqFilledLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build().getEntity(),decRest.putLink("notCreate", "TEST", req, null).getEntity());
	}
	
	@Test
	public void testactionTypeNotCreateKeyFilledReqFilledLinkIdZero() {
		link.setLinkType("Zero");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Unknown actionType. Pick either 'create' or 'delete'")).build().getEntity(),decRest.putLink("notCreate", "TEST", req, link).getEntity());
	}
	
	@Test
	public void testactionTypeNotCreateKeyFilledReqFilledLinkIdFilled() {
		link.setLinkType("Ok");
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Unknown actionType. Pick either 'create' or 'delete'")).build().getEntity(),decRest.putLink("notCreate", "TEST", req, link).getEntity());
	}
	
}
