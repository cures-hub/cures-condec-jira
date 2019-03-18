package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.TestCommentSplitter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.model.impl.SentenceImpl;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestCommentSplitter.AoSentenceTestDatabaseUpdater.class)
public class TestClassificationTrainer extends TestSetUpWithIssues {

	private EntityManager entityManager;
	private CommentManager commentManager;
	private Issue issue;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		commentManager = ComponentAccessor.getCommentManager();
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		issue =issueManager.getIssueByCurrentKey("TEST-10");
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		String comment = "Das ist der Test Kommentrar";
		commentManager.create(issue,  user, comment, true);

	}

	@Test
	public void testClassificationTrainerARFFDataValid(){
		ClassificationTrainer trainer = new ClassificationTrainerImpl("TEST");
		List<Sentence> values = new ArrayList<>();
		for(KnowledgeType type : KnowledgeType.values()){
			Sentence newEntry = new SentenceImpl();
			newEntry.setType(type);
			newEntry.setCommentId(commentManager.getLastComment(issue).getId());
			newEntry.setStartSubstringCount(0);
			newEntry.setEndSubstringCount(12);
			values.add(newEntry);
		}
		((ClassificationTrainerImpl) trainer).buildDatasetForMeka(values);
		trainer.train();
	}
}
