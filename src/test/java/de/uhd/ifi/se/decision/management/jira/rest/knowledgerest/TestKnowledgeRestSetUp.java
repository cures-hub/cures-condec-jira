package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import net.java.ao.EntityManager;

public class TestKnowledgeRestSetUp extends TestSetUpWithIssues {
	protected EntityManager entityManager;
	protected KnowledgeRest knowledgeRest;
	protected DecisionKnowledgeElement decisionKnowledgeElement;
	protected HttpServletRequest request;
	protected Sentence sentence;

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("3");
		decisionKnowledgeElement = new DecisionKnowledgeElementImpl(issue);
		decisionKnowledgeElement.setId(3);
		decisionKnowledgeElement.setProject("TEST");
		decisionKnowledgeElement.setType(KnowledgeType.SOLUTION);
		request = new MockHttpServletRequest();
	}
}
