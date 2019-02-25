package de.uhd.ifi.se.decision.management.jira.extraction.codesummarizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.CodeSummarizerImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestCodeSummarizer extends TestSetUpGit {

	private EntityManager entityManager;
	private CodeSummarizer summarizer;

	@Before
	public void setUp() {
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		initialization();
		summarizer = new CodeSummarizerImpl(gitClient, false);
	}

	@Test
	public void testEmptyInput() {
		CodeSummarizer codeSummarizer = new CodeSummarizerImpl(gitClient, false);
		String summary = codeSummarizer.createSummary("");
		assertEquals(summary, "");
	}

	@Test
	public void testConstWithProjectKey() {
		CodeSummarizer codeSummarizer = new CodeSummarizerImpl("TEST", false);
		assertNotNull(codeSummarizer);
	}

	@Test
	public void testProjectKeyConst() {
		CodeSummarizer codeSummarizer = new CodeSummarizerImpl("TEST");
		assertNotNull(codeSummarizer);
	}

	@Test
	public void testCreateSummaryStringNull() {
		assertEquals("", summarizer.createSummary((String) null));
	}

	@Test
	public void testCreateSummaryStringEmpty() {
		assertEquals("", summarizer.createSummary(""));
	}

	@Test
	@Ignore
	public void testCreateSummaryStringFilled() {
		assertEquals("The following classes were changed: ", summarizer.createSummary("TEST-12"));
	}

	@Test
	public void testCreateSummaryRevCommitNull() {
		assertEquals("", summarizer.createSummary((RevCommit) null));
	}

	@Test
	public void testCreateSummaryMapNull() {
		assertEquals("", summarizer.createSummary((Map<DiffEntry, EditList>) null));
	}

	@Test
	public void testCreateSummaryMapEmpty() {
		assertEquals("", summarizer.createSummary(new HashMap<DiffEntry, EditList>()));
	}

	@Test
	public void testCreateSummaryMapFilled() {
		List<RevCommit> commits = gitClient.getCommits("TEST-12");
		Map<DiffEntry, EditList> diff = gitClient.getDiff(commits.get(0));
		assertEquals("The following classes were changed: ", summarizer.createSummary(diff));
	}
}
