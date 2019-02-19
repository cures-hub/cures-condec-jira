package de.uhd.ifi.se.decision.management.jira.quality.commentmetriccalculator;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.CommentMetricCalculator;
import net.java.ao.EntityManager;
import org.junit.BeforeClass;

import java.io.*;

public class TestSetupCalculator extends TestSetUpWithIssues {
	private EntityManager entityManager;
	protected CommentMetricCalculator calculator;

	@BeforeClass
	public static void createFolders(){
		File repo = new File(System.getProperty("user.home") + File.separator + "repository" + File.separator + "projectKey");
		if(repo.exists()){
			repo.delete();
			repo.mkdirs();
		}
		try {
			Git git = Git.init().setDirectory(repo.getAbsoluteFile()).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("NoSysAdmin");
		addElementToDataBase(user);
		calculator = new CommentMetricCalculator((long) 1, user, "16");

	}

	@AfterClass
	public static void removeFolder(){
		File repo = new File(System.getProperty("user.home") + File.separator + "repository");
		if(repo.exists()){
			repo.delete();
		}
	}

	protected void addElementToDataBase(ApplicationUser user) {
		Sentence element;
		element = new SentenceImpl();
		element.setProject("TEST");
		element.setJiraIssueId(12);
		element.setId(1);
		element.setKey("TEST-12231");
		element.setType("Argument");
		element.setProject("TEST");
		element.setDescription("Old");
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUECOMMENT);
		JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(element, user);
	}
}
