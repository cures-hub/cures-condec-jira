package de.uhd.ifi.se.decision.management.jira.extraction.git;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import net.java.ao.EntityManager;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.util.FS;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class TestSetUpGit {

	protected static String projectKey;

	protected static String directory;

	protected static Git git;

	protected static File cloneDir;

	protected static RefSpec refSpec;

	private EntityManager entityManager;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
//
//	@Ignore
//	@BeforeClass
//	public static void setUp() throws IOException, GitAPIException, InterruptedException {
//		ProjectManager projectManager = new MockProjectManager();
//		new MockComponentWorker().init().addMock(ProjectManager.class, projectManager);
//
//		ProjectManager a = ComponentAccessor.getProjectManager();
//		Project diffProject = new MockProject(5, "GETDIFF");
//		((MockProject) diffProject).setKey("GETDIFF");
//		((MockProjectManager) a).addProject(diffProject);
//
//		projectKey = a.getProjectByCurrentKey("GETDIFF").getKey();
//
//		File file = new File(
//				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey);
//		if (file.exists()) {
//			FileUtils.deleteDirectory(file);
//		}
//
//
//		Project sumProject = new MockProject(5, "GETSUM");
//		((MockProject) sumProject).setKey("GETSUM");
//		((MockProjectManager) a).addProject(sumProject);
//
//		projectKey = a.getProjectByCurrentKey("GETSUM").getKey();
//
////		File secfile = new File(
////				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey);
////		if (secfile.exists()) {
////			FileUtils.deleteDirectory(secfile);
////		}
//
//		// Create a folder in the temp folder that will act as the remote repository
//		File remoteDir = File.createTempFile("remote", "");
//		remoteDir.delete();
//		remoteDir.mkdirs();
//
//		// Create a bare repository
//		RepositoryCache.FileKey fileKey = RepositoryCache.FileKey.exact(remoteDir, FS.DETECTED);
//		Repository remoteRepo = fileKey.open(false);
//		remoteRepo.create(true);
//
//		// Clone the bare repository
//		cloneDir = File.createTempFile("clone", "");
//		cloneDir.delete();
//		cloneDir.mkdirs();
//		git = Git.cloneRepository().setURI(remoteRepo.getDirectory().getAbsolutePath()).setDirectory(cloneDir)
//				      .setBranch("master").call();
//		git.getRepository();
//
//		StoredConfig config = git.getRepository().getConfig();
//		config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, "master", "remote", "origin");
//		config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, "master", "merge", "refs/heads/master");
//		config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, "Branch", "remote", "origin");
//		config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, "Branch", "merge", "refs/heads/Branch");
//		config.save();
//		directory = remoteRepo.getDirectory().getAbsolutePath();
//
//		// Create a new file
//		File newFile = new File(cloneDir, "readMe.txt");
//		newFile.createNewFile();
//		FileUtils.writeStringToFile(newFile, "Test content file");
//		// Commit the new file
//		git.add().addFilepattern(newFile.getName()).call();
//		git.commit().setMessage("FirstFile").setAuthor("gildas", "gildas@example.com").call();
//
//		// Push the commit on the bare repository
//		refSpec = new RefSpec("master");
//		git.push().setRemote("origin").setRefSpecs(refSpec).call();
//
//		new GitClient(directory, projectKey);
//		git.checkout().setCreateBranch(true).setName("Branch").call();
//		Thread.sleep(2000);
//	}
//
//	@Ignore
//	@Before
//	public void setUpComponentGetter(){
//		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
//				new MockUserManager());
//	}


}
