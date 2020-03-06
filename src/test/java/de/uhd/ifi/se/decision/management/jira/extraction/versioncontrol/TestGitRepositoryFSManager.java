package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.junit.Before;
import org.junit.Test;

public class TestGitRepositoryFSManager {

    private List<String> baseProjectUriDir;
    private List<String> baseProjectUriDefaultDir;
    private List<String> baseProjectUriTempDir;
    private List<String> expectedBaseProjectUriBranchDir;
    private List<String> repoUris;

    private static final String branchName = "myTempBranch";
    private static final String distinctFileInBranchFolder = "branch.txt";
    private static final String distinctFileInDefaultFolder = "develop.txt";
    private static final String distinctFileInTempFolder = "temp.txt";

    private static final String folderForTempBranchDirName = "TEMP123456";
    private static final String projectName = "TEST";

    private static File baseProjectDir;
    private static GitRepositoryFSManager FSmanager;

    @Before
    public void setUp() {
	File directory = getExampleDirectory();
	Map<String, String> folderForDefaultBranchNames;
	// add base dir
	String baseDir = directory.getAbsolutePath();
	baseProjectDir = new File(baseDir + File.separator + projectName);
	baseProjectDir.mkdirs();
	repoUris = new ArrayList<String>();
	repoUris.add("https://jira-se.ifi.uni-heidelberg.de/test.git");
	repoUris.add("https://jira-se.ifi.uni-heidelberg.de/test2.git");
	folderForDefaultBranchNames = new HashMap<String, String>();
	folderForDefaultBranchNames.put(repoUris.get(0), "develop");
	folderForDefaultBranchNames.put(repoUris.get(1), "develop");
	baseProjectUriDir = new ArrayList<String>();
	baseProjectUriDefaultDir = new ArrayList<String>();
	baseProjectUriTempDir = new ArrayList<String>();
	expectedBaseProjectUriBranchDir = new ArrayList<String>();
	// prepare paths for subdirectories
	baseProjectUriDir.add(baseDir + File.separator + projectName + File.separator + getHash(repoUris.get(0)));
	baseProjectUriDefaultDir
		.add(baseProjectUriDir.get(0) + File.separator + folderForDefaultBranchNames.get(repoUris.get(0)));
	baseProjectUriTempDir.add(baseProjectUriDir.get(0) + File.separator + folderForTempBranchDirName);
	expectedBaseProjectUriBranchDir.add(baseProjectUriDir.get(0) + File.separator + getHash(branchName));
	// init FS Manager
	FSmanager = new GitRepositoryFSManager(baseDir, projectName, repoUris, folderForDefaultBranchNames);
    }

    @Test
    public void testIsBranchDirectoryInUse() {
	addBranchMarker(branchName);
	assertTrue(FSmanager.isBranchDirectoryInUse(branchName));
    }

    @Test
    public void testReleaseBranchDirectoryNameToTemp() {
	// setup
	addDistinctFileInDefaultDir();

	// branch and temp folders do not exist
	assertFalse(new File(expectedBaseProjectUriBranchDir.get(0)).isDirectory());
	assertEquals(0, findTemporaryDirectoryNames().length);

	// create branch folder from default
	FSmanager.prepareBranchDirectory(branchName, repoUris.get(0));
	assertTrue(new File(expectedBaseProjectUriBranchDir.get(0)).isDirectory());

	// test releasing branch folder to temporary folders pool
	FSmanager.releaseBranchDirectoryNameToTemp(branchName, repoUris.get(0));
	assertFalse(new File(expectedBaseProjectUriBranchDir.get(0)).isDirectory());
	assertEquals(1, findTemporaryDirectoryNames().length);

	String expectedFileName = baseProjectUriDir.get(0) + File.separator + findTemporaryDirectoryNames()[0]
		+ File.separator + distinctFileInDefaultFolder;

	assertTrue(new File(expectedFileName).isFile());
    }

    @Test
    public void testGetDefaultBranchPath() {
	assertEquals(baseProjectUriDefaultDir.get(0), FSmanager.getDefaultBranchPaths().get(repoUris.get(0)));
    }

    @Test
    public void testPrepareBranchDirectoryFailure() {
	// no directories for neither default, temporary nor branch exist
	assertNull(FSmanager.prepareBranchDirectory(branchName, repoUris.get(0)));
    }

    @Test
    public void testPrepareBranchDirectoryFromBranch() {
	// setup
	addDistinctFileInBranchDir();
	String expectedContentPath = expectedBaseProjectUriBranchDir.get(0) + File.separator
		+ distinctFileInBranchFolder;

	assertEquals(0, findTemporaryDirectoryNames().length);
	// test
	testBranchFolderPreparation(branchName, expectedBaseProjectUriBranchDir, expectedContentPath);
	// preparation from branch directory itself should not affect temporary folders
	assertEquals(0, findTemporaryDirectoryNames().length);
    }

    @Test
    public void testPrepareBranchDirectoryFromBranchPrecedenceOverDefaultAndTemp() {
	// setup
	addDistinctFileInBranchDir();
	addDistinctFileInDefaultDir();
	addDistinctFileInTempDir();
	String expectedContentPath = expectedBaseProjectUriBranchDir.get(0) + File.separator
		+ distinctFileInBranchFolder;

	assertEquals(1, findTemporaryDirectoryNames().length);

	// test
	testBranchFolderPreparation(branchName, expectedBaseProjectUriBranchDir, expectedContentPath);
	assertEquals(1, findTemporaryDirectoryNames().length);
    }

    @Test
    public void testPrepareBranchDirectoryFromTemp() {
	// setup
	addDistinctFileInTempDir();
	String expectedContentPath = expectedBaseProjectUriBranchDir.get(0) + File.separator + distinctFileInTempFolder;

	assertEquals(1, findTemporaryDirectoryNames().length);

	// test
	testBranchFolderPreparation(branchName, expectedBaseProjectUriBranchDir, expectedContentPath);
	// preparation should take the folder from temporary folders pool
	assertEquals(0, findTemporaryDirectoryNames().length);

	addDistinctFileInTempDir();
	assertEquals(1, findTemporaryDirectoryNames().length);
    }

    @Test
    public void testPrepareBranchDirectoryFromTempPrecedenceOverDefault() {
	// setup
	addDistinctFileInDefaultDir();
	addDistinctFileInTempDir();
	String expectedContentPath = expectedBaseProjectUriBranchDir.get(0) + File.separator + distinctFileInTempFolder;

	assertEquals(1, findTemporaryDirectoryNames().length);

	// test
	testBranchFolderPreparation(branchName, expectedBaseProjectUriBranchDir, expectedContentPath);
	assertEquals(0, findTemporaryDirectoryNames().length);
    }

    @Test
    public void testPrepareBranchDirectoryFromDefault() {
	// setup
	addDistinctFileInDefaultDir();
	String expectedContentPath = expectedBaseProjectUriBranchDir.get(0) + File.separator
		+ distinctFileInDefaultFolder;

	assertEquals(0, findTemporaryDirectoryNames().length);
	// test
	testBranchFolderPreparation(branchName, expectedBaseProjectUriBranchDir, expectedContentPath);
	// preparation from default folder should not affect temporary folders
	assertEquals(0, findTemporaryDirectoryNames().length);
    }

    private void testBranchFolderPreparation(String branchName, List<String> expectedDir, String expectedContentPath) {
	String actualDir = FSmanager.prepareBranchDirectory(branchName, repoUris.get(0));
	assertNotNull(actualDir);
	assertEquals(expectedDir.get(0), actualDir);
	assertTrue(new File(expectedDir.get(0)).isDirectory());
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
	    return DatatypeConverter.printHexBinary(digest).toUpperCase().substring(0, 5);
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /* helper for fetching temporary folders */
    private String[] findTemporaryDirectoryNames() {
	File file = new File(baseProjectUriDir.get(0));
	String[] directories = file
		.list((current, name) -> (name.toString().startsWith("TEMP") && new File(current, name).isDirectory()));
	return directories;
    }

    /* helpers for adding files */

    private void addBranchMarker(String branchName) {
	File dir = new File(baseProjectUriDir.get(0));
	File touchFile = new File(dir, branchName);
	try {
	    dir.mkdirs();
	    touchFile.createNewFile();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

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
	if (target.endsWith("branch")) {
	    dir = new File(expectedBaseProjectUriBranchDir.get(0));
	    // add distinct file name to default branch directory
	    file = new File(dir, distinctFileInBranchFolder);
	} else if (target.endsWith("default")) {
	    dir = new File(baseProjectUriDefaultDir.get(0));
	    // add distinct file name to default branch directory
	    file = new File(dir, distinctFileInDefaultFolder);
	} else if (target.endsWith("temp")) {
	    dir = new File(baseProjectUriTempDir.get(0));
	    // add distinct file name to temporary branch directory
	    file = new File(dir, distinctFileInTempFolder);
	} else {
	    return false;
	}
	try {
	    dir.mkdirs();
	    file.createNewFile();
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }
}