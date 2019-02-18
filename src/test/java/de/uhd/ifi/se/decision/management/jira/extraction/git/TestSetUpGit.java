package de.uhd.ifi.se.decision.management.jira.extraction.git;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class TestSetUpGit extends TestSetUpWithIssues {

	protected String projectKey0;
	protected String projectKey1;
	protected String directory0;
	protected String directory1;

	protected GitClient gitClient;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Before
	public void setUp() throws IllegalStateException, IOException {
		initialization();
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
		RepositoryCache.FileKey fileKey0 = RepositoryCache.FileKey.exact(remoteDir0, FS.DETECTED);
		Repository remoteRepo0 = fileKey0.open(false);

		directory0 = remoteRepo0.getDirectory().getAbsolutePath();

		// Create a folder in the temp folder that will act as the remote repository
		File remoteDir1 = File.createTempFile("remote1", "");
		remoteDir1.delete();
		remoteDir1.mkdirs();

		// Create a bare repository
		RepositoryCache.FileKey fileKey1 = RepositoryCache.FileKey.exact(remoteDir1, FS.DETECTED);
		Repository remoteRepo1 = fileKey1.open(false);
		remoteRepo1.create(true);

		directory1 = remoteRepo1.getDirectory().getAbsolutePath();
		gitClient = new GitClientImpl(directory1, projectKey1);
	}

}
