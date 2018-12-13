package de.uhd.ifi.se.decision.management.jira.view.treant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestTreant.AoSentenceTestDatabaseUpdater.class)
public class TestNode extends TestSetUpWithIssues {

	private Map<String, String> nodeContent;
	private Map<String, String> link;
	private String htmlClass;
	private long htmlId;
	private String innerHTML;
	private List<Node> children;
	private boolean isCollapsed;
	private boolean isHyperlinked;

	private Node node;
	private EntityManager entityManager;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		nodeContent = new HashMap<>();
		link = new HashMap<>();
		htmlClass = "Test";
		htmlId = 100;
		innerHTML = "Test";
		children = new ArrayList<>();

		node = new Node();
		node.setChildren(children);
		node.setHtmlClass(htmlClass);
		node.setHtmlId(htmlId);
		node.setInnerHTML(innerHTML);
		node.setLink(link);
		node.setNodeContent(nodeContent);

		element = new DecisionKnowledgeElementImpl();
		element.setId(1);
		element.setKey("Test-1");
		element.setType("Argument");
		element.setDescription("Test");
		element.setProject("TEST");
		element.setSummary("TESTfwf");
		element.setDocumentationLocation("i");

		isCollapsed = false;
		isHyperlinked = true;
	}

	@Test
	public void testConstructor() {
		this.node = new Node(element, isCollapsed, isHyperlinked);
		assertNotNull(node);
	}

	@Test
	public void testElementLinkEmptyConstructor() {
		Link link = new LinkImpl();
		link.setType("Test");
		Node newNode = new Node(element, link, isCollapsed, isHyperlinked);
		assertNotNull(newNode);
	}

	@Test
	public void testElementLinkSupportConstructor() {
		Link link = new LinkImpl(element, element);
		link.setType("support");
		Node newNode = new Node(element, link, isCollapsed, isHyperlinked);
		assertNotNull(newNode);
	}

	@Test
	public void testElementLinkAttackConstructor() {
		Link link = new LinkImpl(element, element);
		link.setType("attack");
		Node newNode = new Node(element, link, isCollapsed, isHyperlinked);
		assertNotNull(newNode);
	}

	@Test
	public void testGetNodeContent() {
		assertEquals(nodeContent, this.node.getNodeContent());
	}

	@Test
	public void testGetLink() {
		assertEquals(link, this.node.getLink());
	}

	@Test
	public void testGetHtmlClass() {
		assertEquals(htmlClass, this.node.getHtmlClass());
	}

	@Test
	public void testGetHtmlId() {
		assertEquals(htmlId, this.node.getHtmlId(), 0.0);
	}

	@Test
	public void testGetInnerHtml() {
		assertEquals(innerHTML, this.node.getInnerHTML());
	}

	@Test
	public void testGetChildren() {
		assertEquals(children, this.node.getChildren());
	}

	@Test
	public void testSetNodeContent() {
		Map<String, String> newNodeContent = new ConcurrentHashMap<>();
		this.node.setNodeContent(newNodeContent);
		assertEquals(newNodeContent, this.node.getNodeContent());
	}

	@Test
	public void testSetLink() {
		Map<String, String> newLink = new ConcurrentHashMap<>();
		this.node.setLink(newLink);
		assertEquals(newLink, this.node.getLink());
	}

	@Test
	public void testSetHtmlClass() {
		String newhtmlClass = htmlClass + "New";
		this.node.setHtmlClass(newhtmlClass);
		assertEquals(newhtmlClass, this.node.getHtmlClass());
	}

	@Test
	public void testSetHtmlId() {
		long newHtmlId = htmlId + 1;
		this.node.setHtmlId(newHtmlId);
		assertEquals(newHtmlId, this.node.getHtmlId(), 0.0);
	}

	@Test
	public void testSetInnerHtml() {
		String newInnerHtml = innerHTML + "New";
		this.node.setInnerHTML(newInnerHtml);
		assertEquals(newInnerHtml, this.node.getInnerHTML());
	}

	@Test
	public void testSetChildren() {
		List<Node> newchildren = new ArrayList<Node>();
		this.node.setChildren(newchildren);
		assertEquals(newchildren, this.node.getChildren());
	}

	@Test
	public void testGetConnectors() {
		assertEquals(ImmutableMap.of("style", ImmutableMap.of("stroke", "#000000")), node.getConnectors());
	}

	@Test
	public void testSetConnectors() {
		Map<String, Map<String, String>> newConnectors = ImmutableMap.of("style", ImmutableMap.of("stroke", "#000001"));
		node.setConnectors(newConnectors);
		assertEquals(newConnectors, node.getConnectors());
	}
}
