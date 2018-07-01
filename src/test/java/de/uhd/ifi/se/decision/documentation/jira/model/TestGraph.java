package de.uhd.ifi.se.decision.documentation.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.IssueStrategy;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGraph extends TestSetUp {

	private EntityManager entityManager;
	private Graph graph;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		graph = new GraphImpl("TEST", element.getKey());
	}

	@Test
	public void testRootElementConstructor() {
		Graph graphRoot = new GraphImpl(element.getProjectKey());
		assertNotNull(graphRoot);
	}

	@Test
	public void testRootElementLinkDistConstructor() {
		Graph graphRoot = new GraphImpl(element.getProjectKey(), element.getKey());
		assertNotNull(graphRoot);
	}

	@Test
	public void testGetLinkedElementsNull() {
		assertEquals(0, graph.getLinkedElements(null).size());
	}

	@Test
	public void testGetLinkedElementsEmpty() {
		DecisionKnowledgeElement emptyElement = new DecisionKnowledgeElementImpl();
		assertEquals(0, graph.getLinkedElements(emptyElement).size());
	}

	@Test
	public void testGetLinkedElementsFilled() {
		IssueStrategy issueStrategy = new IssueStrategy();
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");

		long i = 2;
		DecisionKnowledgeElementImpl decision = null;
		decision = new DecisionKnowledgeElementImpl((long) 5000, "TESTSummary", "TestDescription",
				KnowledgeType.DECISION, project.getKey(), "TEST-" + 5000);
		decision.setId((long) 5000);

		issueStrategy.insertDecisionKnowledgeElement(decision, user);
		for (KnowledgeType type : KnowledgeType.values()) {
			LinkImpl link = new LinkImpl();
			link.setLinkType("support");
			if (type != KnowledgeType.DECISION) {
				if (type.equals(KnowledgeType.ARGUMENT)) {
					DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl(i,
							"TESTSummary", "TestDescription", type, project.getKey(), "TEST-" + i);
					issueStrategy.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
					link.setIngoingId(decision.getId());
					link.setOutgoingId(decisionKnowledgeElement.getId());
					issueStrategy.insertLink(link, user);
				} else {
					DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl(i,
							"TESTSummary", "TestDescription", type, project.getKey(), "TEST-" + i);
					issueStrategy.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
					link.setLinkType("attack");
					link.setOutgoingId(decision.getId());
					link.setIngoingId(decisionKnowledgeElement.getId());
					issueStrategy.insertLink(link, user);
				}
			}
			i++;
		}
		System.out.println(graph.getLinkedElements(decision).size());
	}

	@Test
	public void testSetRootElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setId(123);
		element.setSummary("Test New Element");
		graph.setRootElement(element);
		assertEquals(element.getSummary(), graph.getRootElement().getSummary());
	}
}
