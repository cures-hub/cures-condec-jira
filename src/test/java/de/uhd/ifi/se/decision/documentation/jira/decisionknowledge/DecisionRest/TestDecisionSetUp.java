package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionRest;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.documentation.jira.persistence.DecisionsRest;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;
import net.java.ao.EntityManager;
import org.junit.Before;

import javax.servlet.http.HttpServletRequest;

public class TestDecisionSetUp extends TestSetUp{
    protected EntityManager entityManager;
    protected DecisionsRest decRest;
    protected DecisionKnowledgeElement dec;
    protected HttpServletRequest req;

    @Before
    public void setUp() {
        decRest= new DecisionsRest();
        initialisation();
        new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());

        Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("3");
        dec = new DecisionKnowledgeElement(issue);
        dec.setId(1);
        dec.setProjectKey("TEST");
        dec.setType("Solution");
        req = new MockHttpServletRequest();
    }
}
