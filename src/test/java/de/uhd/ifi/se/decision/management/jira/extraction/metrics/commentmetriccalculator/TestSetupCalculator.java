package de.uhd.ifi.se.decision.management.jira.extraction.metrics.commentmetriccalculator;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.metrics.CommentMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import net.java.ao.EntityManager;
import org.junit.Before;

public class TestSetupCalculator extends TestSetUpWithIssues {
	private EntityManager entityManager;
	protected CommentMetricCalculator calculator;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockUserManager());
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("NoSysAdmin");
		calculator = new CommentMetricCalculator((long)1,user, "decision");
	}
}
