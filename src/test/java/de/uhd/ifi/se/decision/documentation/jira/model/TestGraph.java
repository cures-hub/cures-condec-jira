/*
TODO Refactor and retest
package de.uhd.ifi.se.decision.documentation.jira.model;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static org.junit.Assert.*;

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

        graph = new Graph();
        element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long)14));
        newElement = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long)15));
        elements = new HashSet<DecisionKnowledgeElement>();
        elements.add(element);
        links = new ArrayList<>();

    }

    @Test
    public void testRootElementConstructor(){
        Graph graphRoot = new Graph(element);
        assertNotNull(graphRoot.getElements());
    }

    @Test
    public void testRootElementLinkDistConstructor(){
        Graph graphRoot = new Graph(element,1);
        assertNotNull(graphRoot.getElements());
    }

    @Test
    public void testAddElementNull(){
        assertFalse(graph.addElement(null));
        assertEquals(0,graph.getElements().size(),0.0);
    }

    @Test
    public void testAddElementFilled(){
        assertTrue(graph.addElement(element));
        assertEquals(1,graph.getElements().size(),0.0);
    }

    @Test
    public void testAddElementsNull(){
        assertFalse(graph.addElements(null));
        assertEquals(0,graph.getElements().size(),0.0);
    }

    @Test
    public void testAddElementsFilled(){
        assertTrue(graph.addElements(elements));
        assertEquals(1,graph.getElements().size(),0.0);
    }

    @Test
    public void testRemoveElementNull(){
        assertFalse(graph.removeElement(null));
    }

    @Test
    public void testRemoveElementEmptyList(){
        assertFalse(graph.removeElement(element));
    }

    @Test
    public void testRemoveElementNotContained(){
        graph.addElements(elements);
        assertFalse(graph.removeElement(newElement));
    }

    @Test
    public void testRemoveElementContained(){
        graph.addElement(element);
        assertTrue(graph.removeElement(element));
        assertEquals(0,graph.getElements().size(),0.0);
    }

    @Test
    public void testAddLinkNull(){
        assertFalse(graph.addLink(null));
    }

    @Test
    public void testAddLinkAllreadyContained(){
        Link link = new LinkImpl();
        link.setLinkType("Test");
        link.setOutgoingId(14);
        link.setIngoingId(15);
        link.setIngoingElement(newElement);
        link.setOutgoingElement(element);
        graph.addLink(link);
        assertFalse(graph.addLink(link));
    }

    @Test
    public void testAddLinkFilled(){
        Link link = new LinkImpl();
        link.setLinkType("Test");
        link.setOutgoingId(14);
        link.setIngoingId(15);
        link.setIngoingElement(newElement);
        link.setOutgoingElement(element);

        assertTrue(graph.addLink(link));
        assertEquals(1,graph.getLinks().size(),0.0);
    }

    @Test
    public void testAddLinkElementsNull(){
        assertFalse(graph.addLink(null,null));
    }

    @Test
    public void testAddLinkElementsInNull(){
        assertFalse(graph.addLink(null,newElement));
    }

    @Test
    public void testAddLinkElementOutNull(){
        assertFalse(graph.addLink(element,null));
    }

    @Test
    public void testAddLinkElementFilled(){
        assertTrue(graph.addLink(element,newElement));
    }

    @Test
    public void testAddLinksNull(){
        assertFalse(graph.addLinks(null));
    }

    @Test
    public void testAddLinksEmptyList(){
        assertFalse(graph.addLinks(new ArrayList<Link>()));
    }

    @Test
    public void testAddLinksOneElement(){
        Link link = new LinkImpl();
        link.setLinkType("Test");
        link.setOutgoingId(14);
        link.setIngoingId(15);
        link.setIngoingElement(newElement);
        link.setOutgoingElement(element);
        links.add(link);
        assertTrue(graph.addLinks(links));
    }

    @Test
    public void testAddLinksMoreElement(){
        Link link = new LinkImpl();
        link.setLinkType("Test");
        link.setOutgoingId(14);
        link.setIngoingId(15);
        link.setIngoingElement(newElement);
        link.setOutgoingElement(element);
        links.add(link);
        Link link2 = new LinkImpl();
        link2.setLinkType("Test");
        link2.setIngoingId(12);
        link2.setOutgoingId(13);
        assertTrue(graph.addLinks(links));
    }

    @Test
    public void testSetLinksNull(){
        graph.setLinks(null);
        assertEquals(0, graph.getLinkedElements().size(),0.0);
    }

    @Test
    public void testSetLinksEmpty(){
        Set<Link> linksSet = new HashSet<>();
        graph.setLinks(linksSet);
        assertEquals(0, graph.getLinkedElements().size(),0.0);
    }

    @Test
    public void testSetLinksFilled(){
        Link link = new LinkImpl();
        link.setLinkType("Test");
        link.setOutgoingId(14);
        link.setIngoingId(15);
        link.setIngoingElement(newElement);
        link.setOutgoingElement(element);
        Set<Link> linksSet = new HashSet<>();
        linksSet.add(link);
        graph.setLinks(linksSet);
        assertEquals(0, graph.getLinkedElements().size(),0.0);
    }

    @Test
    public void testSetElementsNull(){
        graph.setElements(null);
        assertEquals(0, graph.getElements().size(),0.0);
    }

    @Test
    public void testSetElementsEmpty(){
        Set<DecisionKnowledgeElement> emptySet = new HashSet<>();
        graph.setElements(emptySet);
        assertEquals(0, graph.getElements().size(),0.0);
    }

    @Test
    public void testSetElementsFilled(){
        graph.setElements(elements);
        assertEquals(1,graph.getElements().size(),0.0);
    }

    @Test
    public void testSetLinkedElementsNull(){
        graph.setLinkedElements(null);
        assertEquals(0,graph.getLinkedElements().size(),0.0);
    }

    @Test
    public void testSetLinkedElementsEmpty(){
        Map<DecisionKnowledgeElement,Set<Link>> emptySet = new HashMap<>();
        graph.setLinkedElements(emptySet);
        assertEquals(0,graph.getLinkedElements().size(),0.0);
    }

    @Test
    public void testSetLinkedElementsFilled(){
        Link link = new LinkImpl();
        link.setLinkType("Test");
        link.setOutgoingId(14);
        link.setIngoingId(15);
        link.setIngoingElement(newElement);
        link.setOutgoingElement(element);
        Set<Link> linksSet = new HashSet<>();
        linksSet.add(link);
        Map<DecisionKnowledgeElement,Set<Link>> filledSet = new HashMap<>();
        filledSet.put(element,linksSet);
        graph.setLinkedElements(filledSet);
        assertEquals(1,graph.getLinkedElements().size(),0.0);
    }
}
*/
