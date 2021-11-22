package de.uhd.ifi.se.decision.management.jira.git;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;

/**
 * File system manager for git repositories.
 * 
 * @issue Where to store repos associated to a Jira project?
 * @decision Each git repository is stored in
 *           JiraHome/data/condec-plugin/git/<project-key>/<MD5 hash of URI>!
 */
public class GitRepositoryFileSystemManager {

	/**
	 * @issue What is the best place to clone the git repos to?
	 * @decision Clone git repos to JiraHome/data/condec-plugin/git!
	 * @pro The Git integration for Jira plug-in clones its repos to a similar
	 *      folder: JiraHome/data/git-plugin.
	 */
	public static String GIT_DIRECTORY = ComponentGetter.PLUGIN_HOME + "git" + File.separator;

	private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryFileSystemManager.class);

	private File pathToWorkingDirectory;

	/**
	 * @issue How to distinguish different git repositories for a Jira project in
	 *        file system?
	 * @decision Use the MD5 hash of the repo URI as the folder name for a git
	 *           repository!
	 * @alternative Use the repo URI as the folder name!
	 * @con The repo URI might contain charaters not allowed in file system.
	 * @alternative Use numbers as folder names: 0, 1, 2, ...!
	 * @con This requires to store a mapping between number and repo URI.
	 * 
	 * @param projectKey
	 *            of a Jira project that the git repository is associated with.
	 * @param repoUri
	 *            remote Uniform Resource Identifier (URI) of the git repository as
	 *            a String.
	 */
	public GitRepositoryFileSystemManager(String projectKey, String repoUri) {
		String projectPath = GIT_DIRECTORY + File.separator + projectKey;
		pathToWorkingDirectory = new File(projectPath + File.separator + getShortHash(repoUri));
		pathToWorkingDirectory.mkdirs();
	}

	/**
	 * @issue File system does not allow all characters for folder and file name,
	 *        therefore md5 can be used to get unique strings for inputs like uris
	 *        etc. But md5 hashes can produce too long paths and corrupt the
	 *        filesystem, especially for Java projects. How can this be overcome?
	 * @alternative Use full length of the hash!
	 * @decision Use the first 5 characters from the generated hash!
	 * @pro It is common practice to shorten hashes.
	 * @con Entropy might suffer too much from using only 5 chars.
	 */
	private static String getShortHash(String text) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(text.getBytes());
			byte[] digest = messageDigest.digest();
			return DatatypeConverter.printHexBinary(digest).toUpperCase().substring(0, 5);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("MD5 does not exist?");
			return "";
		}
	}

	/**
	 * @return absolute path to the working directory of the repository (parent
	 *         directory of .git directory).
	 */
	public File getPathToWorkingDirectory() {
		return pathToWorkingDirectory;
	}

	/**
	 * Deletes the working directory of the repository including all of its files
	 * and sub-directories.
	 * 
	 * @return true if deletion was successful.
	 */
	public boolean deleteWorkingDirectory() {
		if (!pathToWorkingDirectory.exists()) {
			return false;
		}
		return GitRepositoryFileSystemManager.deleteDirectory(pathToWorkingDirectory);
	}

	public static boolean deleteDirectory(File directory) {
		if (directory.listFiles() == null) {
			return false;
		}
		boolean isDeleted = true;
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				deleteDirectory(file);
			} else {
				isDeleted = isDeleted && file.delete();
			}
		}
		return isDeleted && directory.delete();
	}

	/**
	 * Deletes all repositories for a Jira project including all of their files and
	 * sub-directories.
	 * 
	 * @param projectKey
	 *            Jira project
	 * @return true if deletion was successful.
	 */
	public static boolean deleteProjectDirectory(String projectKey) {
		return deleteDirectory(new File(GIT_DIRECTORY + File.separator + projectKey));
	}
}
