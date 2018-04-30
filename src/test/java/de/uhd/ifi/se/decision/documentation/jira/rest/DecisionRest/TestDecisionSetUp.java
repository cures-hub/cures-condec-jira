package de.uhd.ifi.se.decision.documentation.jira.rest.DecisionRest;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.documentation.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.documentation.jira.rest.DecisionsRest;
import net.java.ao.EntityManager;
import org.junit.Before;

import javax.servlet.http.HttpServletRequest;

public class TestDecisionSetUp extends TestSetUp{
    protected EntityManager entityManager;
    protected DecisionsRest decRest;
    protected DecisionKnowledgeElementImpl dec;
    protected HttpServletRequest req;

    @Before
    public void setUp() {
        decRest= new DecisionsRest();
        initialization();
        new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());

        Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("3");
        dec = new DecisionKnowledgeElementImpl(issue);
        dec.setId(1);
        dec.setProjectKey("TEST");
        dec.setType(KnowledgeType.SOLUTION);
        req = new MockHttpServletRequest();
    }
}
