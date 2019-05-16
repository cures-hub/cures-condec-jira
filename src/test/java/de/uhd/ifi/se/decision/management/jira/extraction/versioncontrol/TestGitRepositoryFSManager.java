package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class TestGitRepositoryFSManager {


	private String baseDir;
	private String baseProjectUriDir;
	private String baseProjectUriDefaultDir;
	private String baseProjectUriTempDir;
	private String expectedBaseProjectUriBranchDir;

	private static final String branchName = "myTempBranch";
	private static final String distinctFileInBranchFolder = "branch.txt";
	private static final String distinctFileInDefaultFolder = "develop.txt";
	private static final String distinctFileInTempFolder = "temp.txt";
	private static final String folderForDefaultBranchName = "develop";
	private static final String folderForTempBranchDirName = "TEMP123456";
	private static final String projectName = "TEST";
	private static final String repoUri = "https://jira-se.ifi.uni-heidelberg.de/test.git";

	private static File baseProjectDir;
	private static GitRepositoryFSManager FSmanager;

	@Before
	public void setup() {
		File directory = getExampleDirectory();

		// add base dir
		baseDir = directory.getAbsolutePath();
		baseProjectDir = new File(baseDir+File.separator+projectName);
		baseProjectDir.mkdirs();

		// prepare paths for subdirectories
		baseProjectUriDir = baseDir+File.separator+projectName
				+File.separator+getHash(repoUri);
		baseProjectUriDefaultDir = baseProjectUriDir
				+File.separator+ folderForDefaultBranchName;
		baseProjectUriTempDir = baseProjectUriDir
				+File.separator+ folderForTempBranchDirName;
		expectedBaseProjectUriBranchDir = baseProjectUriDir
				+File.separator
				+getHash(branchName);

		// init FS Manager
		FSmanager = new GitRepositoryFSManager(baseDir,
				projectName, repoUri, folderForDefaultBranchName);
	}

	@Test
	public void testReleaseBranchDirectoryNameToTemp() {
		// setup
		addDistinctFileInDefaultDir();

		// branch and temp folders do not exist
		assertFalse(new File(expectedBaseProjectUriBranchDir).isDirectory());
		assertEquals(0,findTemporaryDirectoryNames().length);

		// create branch folder from default
		FSmanager.prepareBranchDirectory(branchName);
		assertTrue(new File(expectedBaseProjectUriBranchDir).isDirectory());

		// test releasing branch folder to temporary folders pool
		FSmanager.releaseBranchDirectoryNameToTemp(branchName);
		assertFalse(new File(expectedBaseProjectUriBranchDir).isDirectory());
		assertEquals(1,findTemporaryDirectoryNames().length);

		String expectedFileName = baseProjectUriDir+File.separator
				+ findTemporaryDirectoryNames()[0]
				+ File.separator
				+ distinctFileInDefaultFolder;

		assertTrue(new File(expectedFileName).isFile());
	}

	@Test
	public void testGetDefaultBranchPath(){
		assertEquals(baseProjectUriDefaultDir,FSmanager.getDefaultBranchPath());
	}

	@Test
	public void testPrepareBranchDirectoryFailure() {
		// no directories for neither default, temporary nor branch exist
		assertNull(FSmanager.prepareBranchDirectory(branchName));
	}

	@Test
	public void testPrepareBranchDirectoryFromBranch() {
		// setup
		addDistinctFileInBranchDir();
		String expectedContentPath = expectedBaseProjectUriBranchDir
				+ File.separator
				+ distinctFileInBranchFolder;

		assertEquals(0,findTemporaryDirectoryNames().length);
		// test
		testBranchFolderPreparation(branchName, expectedBaseProjectUriBranchDir, expectedContentPath);
		// preparation from branch directory itself should not affect temporary folders
		assertEquals(0,findTemporaryDirectoryNames().length);
	}

	@Test
	public void testPrepareBranchDirectoryFromBranchPrecedenceOverDefaultAndTemp() {
		// setup
		addDistinctFileInBranchDir();
		addDistinctFileInDefaultDir();
		addDistinctFileInTempDir();
		String expectedContentPath = expectedBaseProjectUriBranchDir
				+ File.separator
				+ distinctFileInBranchFolder;

		assertEquals(1,findTemporaryDirectoryNames().length);

		// test
		testBranchFolderPreparation(branchName, expectedBaseProjectUriBranchDir, expectedContentPath);
		assertEquals(1,findTemporaryDirectoryNames().length);
	}

	@Test
	public void testPrepareBranchDirectoryFromTemp() {
		// setup
		addDistinctFileInTempDir();
		String expectedContentPath = expectedBaseProjectUriBranchDir
				+ File.separator
				+ distinctFileInTempFolder;

		assertEquals(1,findTemporaryDirectoryNames().length);

		// test
		testBranchFolderPreparation(branchName, expectedBaseProjectUriBranchDir, expectedContentPath);
		// preparation should take the folder from temporary folders pool
		assertEquals(0,findTemporaryDirectoryNames().length);

		addDistinctFileInTempDir();
		assertEquals(1,findTemporaryDirectoryNames().length);
	}

	@Test
	public void testPrepareBranchDirectoryFromTempPrecedenceOverDefault() {
		// setup
		addDistinctFileInDefaultDir();
		addDistinctFileInTempDir();
		String expectedContentPath = expectedBaseProjectUriBranchDir
				+ File.separator
				+ distinctFileInTempFolder;

		assertEquals(1,findTemporaryDirectoryNames().length);

		// test
		testBranchFolderPreparation(branchName, expectedBaseProjectUriBranchDir, expectedContentPath);
		assertEquals(0,findTemporaryDirectoryNames().length);
	}
	@Test
	public void testPrepareBranchDirectoryFromDefault() {
		// setup
		addDistinctFileInDefaultDir();
		String expectedContentPath = expectedBaseProjectUriBranchDir
				+ File.separator
				+ distinctFileInDefaultFolder;

		assertEquals(0,findTemporaryDirectoryNames().length);
		// test
		testBranchFolderPreparation(branchName, expectedBaseProjectUriBranchDir, expectedContentPath);
		// preparation from default folder should not affect temporary folders
		assertEquals(0,findTemporaryDirectoryNames().length);
	}

	private void testBranchFolderPreparation(String branchName, String expectedDir, String expectedContentPath) {
		String actualDir = FSmanager.prepareBranchDirectory(branchName);
		assertNotNull(actualDir);
		assertEquals(expectedDir,actualDir);
		assertTrue(new File(expectedDir).isDirectory());
		assertTrue(new File(expectedContentPath).isFile());
	}

	private static File getExampleDirectory() {
		File directory = null;
		try {
			directory = File.createTempFile("clone", "");
			directory.delete();
			directory.mkdirs();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return directory;
	}

	private static String getHash(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte[] digest = md.digest();
			return DatatypeConverter.printHexBinary(digest).toUpperCase();
		}
		catch (NoSuchAlgorithmException e) {;
			return "";
		}
	}

	/* helper for fetching temporary folders */
	private String[] findTemporaryDirectoryNames() {
		File file = new File(baseProjectUriDir);
		String[] directories = file.list((current, name) ->
				(name.toString().startsWith("TEMP")
						&& new File(current, name).isDirectory()));
		return directories;
	}

	/* helpers for adding files */

	private void addDistinctFileInDefaultDir() {
		addDistinctFile("default");
	}

	private void addDistinctFileInTempDir() {
		addDistinctFile("temp");
	}

	private void addDistinctFileInBranchDir() {
		addDistinctFile("branch");
	}

	private boolean addDistinctFile(String target) {
		File file;
		File dir;
		if (target.endsWith("branch")){
			dir = new File (expectedBaseProjectUriBranchDir);
			// add distinct file name to default branch directory
			file = new File(dir, distinctFileInBranchFolder);
		}
		else if (target.endsWith("default")){
			dir = new File (baseProjectUriDefaultDir);
			// add distinct file name to default branch directory
			file = new File(dir, distinctFileInDefaultFolder);
		}
		else if (target.endsWith("temp")) {
			dir = new File (baseProjectUriTempDir);
			// add distinct file name to temporary branch directory
			file = new File(dir, distinctFileInTempFolder);
		}
		else {
			return false;
		}
		try {
			dir.mkdirs();
			file.createNewFile();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}