package de.uhd.ifi.se.decision.documentation.jira.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGraph extends TestSetUp {
    private EntityManager entityManager;

    private Graph graph;
    private Set<DecisionKnowledgeElement> elements;
    private List<Link> links;
    private DecisionKnowledgeElement element;
    private DecisionKnowledgeElement newElement;

    @Before
    public void setUp(){
        initialization();
        new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());


        element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long)14));
        newElement = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long)15));
        elements = new HashSet<DecisionKnowledgeElement>();
        elements.add(element);
        links = new ArrayList<>();
        graph = new Graph("TEST", element.getKey(), 3);
    }

    @Test
    public void testRootElementConstructor(){
        Graph graphRoot = new Graph(element.getProjectKey());
    }

    @Test
    public void testRootElementLinkDistConstructor(){
        Graph graphRoot = new Graph(element.getProjectKey(),element.getKey(),1);
    }

//    @Test
//    public void testGetNodeStructure(){
//        assertEquals(3,graph.getNodeStructure().getNodeContent().size(), 0.0);
//    }
//
//    @Test
//    public void testgetDataStructureNull() {
//        assertEquals(Data.class, graph.getDataStructure(null).getClass());
//    }
//
//    @Test
//    public void testGetDataStructureFilled(){
//        assertEquals("Assessment / Test", graph.getDataStructure(element).getText());
//    }
//
//    @Test
//    public void testGetDataStructureDifferentFilled(){
//        IssueStrategy issueStrategy = new IssueStrategy();
//        ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("NoFails");
//        Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
//
//        long i = 2;
//        DecisionKnowledgeElementImpl decision = null;
//        decision = new DecisionKnowledgeElementImpl((long) 5000, "TESTSummary", "TestDescription",
//                KnowledgeType.DECISION, project.getKey(), "TEST-" + 5000);
//        decision.setId((long) 5000);
//
//        issueStrategy.insertDecisionKnowledgeElement(decision, user);
//        for (KnowledgeType type : KnowledgeType.values()) {
//            LinkImpl link = new LinkImpl();
//            link.setLinkType("support");
//            if (type != KnowledgeType.DECISION) {
//                if(type.equals(KnowledgeType.ARGUMENT)){
//                    DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl(i,
//                            "TESTSummary", "TestDescription", type, project.getKey(), "TEST-" + i);
//                    issueStrategy.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
//                    link.setIngoingId(decision.getId());
//                    link.setOutgoingId(decisionKnowledgeElement.getId());
//                    issueStrategy.insertLink(link, user);
//                } else {
//                    DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl(i,
//                            "TESTSummary", "TestDescription", type, project.getKey(), "TEST-" + i);
//                    issueStrategy.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
//                    link.setLinkType("attack");
//                    link.setOutgoingId(decision.getId());
//                    link.setIngoingId(decisionKnowledgeElement.getId());
//                    issueStrategy.insertLink(link, user);
//                }
//            }
//            i++;
//        }
//        System.out.println(graph.getDataStructure(decision).getChildren().size());
//    }
}
