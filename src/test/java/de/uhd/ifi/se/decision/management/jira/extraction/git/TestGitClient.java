package de.uhd.ifi.se.decision.management.jira.extraction.git;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.util.FS;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;

public class TestGitClient {

	private String projectKey0;
	private String projectKey1;
	private String directory0;
	private String directory1;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Before
	public void setUp() throws IllegalStateException, GitAPIException, IOException, JSONException {
		ProjectManager a = ComponentAccessor.getProjectManager();
		Project testingProject = new MockProject(4, "TESTING");
		((MockProject) testingProject).setKey("TESTING");
		((MockProjectManager) a).addProject(testingProject);

		Project testedProject = new MockProject(5, "TESTED");
		((MockProject) testedProject).setKey("TESTED");
		((MockProjectManager) a).addProject(testedProject);

		projectKey0 = a.getProjectByCurrentKey("TESTING").getKey();
		projectKey1 = a.getProjectByCurrentKey("TESTED").getKey();

		// Create a folder in the temp folder that will not act as a remote repository
		File remoteDir0 = File.createTempFile("remote0", "");
		remoteDir0.delete();
		remoteDir0.mkdirs();
		FileKey fileKey0 = FileKey.exact(remoteDir0, FS.DETECTED);
		Repository remoteRepo0 = fileKey0.open(false);

		directory0 = remoteRepo0.getDirectory().getAbsolutePath();

		// Create a folder in the temp folder that will act as the remote repository
		File remoteDir1 = File.createTempFile("remote1", "");
		remoteDir1.delete();
		remoteDir1.mkdirs();

		// Create a bare repository
		FileKey fileKey1 = FileKey.exact(remoteDir1, FS.DETECTED);
		Repository remoteRepo1 = fileKey1.open(false);
		remoteRepo1.create(true);

		directory1 = remoteRepo1.getDirectory().getAbsolutePath();
	}

	@Ignore
	@Test
	public void testNotCloningNotExistingRepo() throws InterruptedException {
		new GitClient(directory0, projectKey0);
		Thread.sleep(2000);
		File file = new File(
				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey0);
		assertFalse(file.exists());
	}

	@Ignore
	@Test
	public void testCloneRepo() throws JSONException, InterruptedException {
		GitClient gitClient = new GitClient(directory1, projectKey1);
		Thread.sleep(2000);
		File file = new File(
				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey1);
		assertTrue(file.exists());
		gitClient.deleteRepo();
	}

	@Ignore
	@Test
	public void testUpdateClonedRepo() throws InterruptedException {
		GitClient gitClient = new GitClient(directory1, projectKey1);
		Thread.sleep(2000);
		gitClient = new GitClient(directory1, projectKey1);
		Thread.sleep(2000);
		File file = new File(
				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey1);
		File file1 = new File(
				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey1 + "1");
		File file2 = new File(
				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey1 + "2");
		assertTrue(file.exists());
		assertFalse(file1.exists());
		assertFalse(file2.exists());
		gitClient.deleteRepo();
	}
}
