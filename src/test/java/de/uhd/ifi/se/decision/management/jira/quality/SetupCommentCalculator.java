package de.uhd.ifi.se.decision.management.jira.quality;

import org.junit.Before;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public abstract class SetupCommentCalculator extends TestSetUp {
	protected CommentMetricCalculator calculator;
	protected ApplicationUser user;
	protected KnowledgeElement decisionElement;
	protected KnowledgeElement argumentElement;
	protected KnowledgeElement issueElement;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();

		calculator = new CommentMetricCalculator(1, user);
		issueElement = JiraIssues.addElementToDataBase(17, "Issue");
		decisionElement = JiraIssues.addElementToDataBase(18, "Decision");
		argumentElement = JiraIssues.addElementToDataBase(19, "Argument");
	}
}
