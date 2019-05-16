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

	private static final String folderForDefaultBranchName = "develop";
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
		// init FS Manager
		FSmanager = new GitRepositoryFSManager(baseDir,
				projectName, repoUri, folderForDefaultBranchName);
	}

	@Test
	public void testGetDefaultBranchPath(){
		assertEquals(baseProjectUriDefaultDir,FSmanager.getDefaultBranchPath());
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
}