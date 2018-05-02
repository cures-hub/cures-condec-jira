package de.uhd.ifi.se.decision.documentation.jira.persistence.IssueStrategyTest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueLink;
import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.documentation.jira.model.Link;
import de.uhd.ifi.se.decision.documentation.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.documentation.jira.model.LinkImpl;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

public class TestGetChildren  extends TestIssueStrategySetUp{

    @Test
    (expected = NullPointerException.class)
    public void testDecisionKnowledgeElementNull(){
        issueStrategy.getChildren(null);
    }

    @Test
    public void testDecisionKnowledgeElementEmpty(){
        DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl();
        assertEquals(new ArrayList<DecisionKnowledgeElementImpl>(), issueStrategy.getChildren(decisionKnowledgeElement));
    }

    @Test
    public void testDecisionKnowledgeElementHasAllTypesOfChildren(){
        ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("NoFails");
        Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");

        long i = 2;
        DecisionKnowledgeElementImpl decision = null;
        decision = new DecisionKnowledgeElementImpl( (long) 5000, "TESTSummary", "TestDescription", KnowledgeType.DECISION, project.getKey(), "TEST-"+ 5000 );
        decision.setId((long) 5000);

        issueStrategy.insertDecisionKnowledgeElement(decision, user);
        for(KnowledgeType type : KnowledgeType.values()) {
            LinkImpl link = new LinkImpl();
            link.setLinkType("support");
            if(type!= KnowledgeType.DECISION){
                DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl( i, "TESTSummary", "TestDescription", type, project.getKey(), "TEST-"+ i );
                issueStrategy.insertDecisionKnowledgeElement(decisionKnowledgeElement,user);
                link.setOutgoingId(decision.getId());
                link.setIngoingId(decisionKnowledgeElement.getId());
                issueStrategy.insertLink(link, user);
            }
            i++;
        }
        System.out.println(issueStrategy.getChildren(decision));
    }

}
