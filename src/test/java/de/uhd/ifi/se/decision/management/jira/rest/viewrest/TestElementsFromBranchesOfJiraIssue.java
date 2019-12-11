package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;
import org.ofbiz.core.entity.GenericEntityException;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.ViewRestImpl;
import de.uhd.ifi.se.decision.management.jira.view.diffviewer.DiffViewer;

public class TestElementsFromBranchesOfJiraIssue extends TestSetUpGit {
    private ViewRest viewRest;

    private static final String INVALID_ISSUEKEY = "Decision knowledge elements cannot be shown since issue key is invalid.";
    protected HttpServletRequest request;

    @Before
    public void setUp() {
        viewRest = new ViewRestImpl();
        init();
        ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
        request = new MockHttpServletRequest();
        request.setAttribute("user", user);
    }

    @Test
    public void testEmptyIssueKey() throws GenericEntityException {
        try {
            assertEquals(400, viewRest.getFeatureBranchTree(request, "").getStatus());
        } catch (PermissionException e) {
            assertNull(e);
        }
    }

    @Test
    public void testUnknownIssueKey() throws GenericEntityException {
        try {
            assertEquals(400, viewRest.getFeatureBranchTree(request, "HOUDINI-1").getStatus());
            assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_ISSUEKEY))
                    .build().getEntity(), viewRest.getFeatureBranchTree(request, "HOUDINI-1").getEntity());
        } catch (PermissionException e) {
            assertNull(e);
        }
    }

    @Test
    public void testExistingIssueKey() throws GenericEntityException {
        try {
            assertEquals(200, viewRest.getFeatureBranchTree(request, "TEST-2").getStatus());
            Object receivedEntity = viewRest.getFeatureBranchTree(request, "TEST-2").getEntity();
			Object expectedEntity = new DiffViewer(null);
			assertEquals(expectedEntity.getClass(), receivedEntity.getClass());
        } catch (PermissionException e) {
            assertNull(e);
        }

    }
}
