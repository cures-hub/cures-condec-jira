package de.uhd.ifi.se.decision.management.jira.rest.configRest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestIsKnowledgeExtractedFromGit extends TestConfigSuper {
	@Test
	public void testIsKnowledgeExtractedNull(){
		assertEquals(Status.BAD_REQUEST.getStatusCode(),confRest.isKnowledgeExtractedFromGit(null).getStatus());
	}

	@Test
	public void testIsKnowledgeExtractedNonExistend(){
		assertEquals(Status.OK.getStatusCode(),confRest.isKnowledgeExtractedFromGit("NotTEST").getStatus());
	}

	@Test
	public void testIsKnowledgeExtratedExistend(){
		assertEquals(Status.OK.getStatusCode(),confRest.isKnowledgeExtractedFromGit("TEST").getStatus());
	}
}
