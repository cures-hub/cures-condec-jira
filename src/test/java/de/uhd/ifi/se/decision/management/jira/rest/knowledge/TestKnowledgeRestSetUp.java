package de.uhd.ifi.se.decision.management.jira.rest.knowledge;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import net.java.ao.EntityManager;

public class TestKnowledgeRestSetUp extends TestSetUp {
	protected EntityManager entityManager;
	protected KnowledgeRest knowledgeRest;
	protected DecisionKnowledgeElement decisionKnowledgeElement;
	protected HttpServletRequest request;

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("3");
		decisionKnowledgeElement = new DecisionKnowledgeElementImpl(issue);
		decisionKnowledgeElement.setId(1);
		decisionKnowledgeElement.setProject("TEST");
		decisionKnowledgeElement.setType(KnowledgeType.SOLUTION);
		request = new MockHttpServletRequest();
	}
}
