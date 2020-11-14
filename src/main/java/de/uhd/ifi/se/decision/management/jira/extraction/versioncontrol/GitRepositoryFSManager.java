package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;

/**
 * File system manager for git repositories.
 * 
 * Each git repository is stored in
 * JiraHome/data/condec-plugin/git/<project-key>/<MD5 hash of URI>.
 */
public class GitRepositoryFSManager {

	/**
	 * @issue What is the best place to clone the git repo to?
	 * @decision Clone git repo to JiraHome/data/condec-plugin/git!
	 * @pro The Git integration for Jira plug-in clones its repos to a similar
	 *      folder: JiraHome/data/git-plugin.
	 */
	public static String GIT_DIRECTORY = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory()
			.getAbsolutePath() + File.separator + "condec-plugin" + File.separator + "git" + File.separator;

	private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryFSManager.class);

	private String repositoryPath;

	public GitRepositoryFSManager(String projectKey, String repoUri) {
		String projectPath = GIT_DIRECTORY + File.separator + projectKey;
		repositoryPath = projectPath + File.separator + getShortHash(repoUri);
		new File(repositoryPath).mkdirs();
	}

	/**
	 * @issue File system does not allow all characters for folder and file name,
	 *        therefore md5 can be used to get unique strings for inputs like uris
	 *        etc. But md5 hashes can produce too long paths and corrupt the
	 *        filesystem, especially for java projects. How can this be overcome?
	 *
	 * @alternative use full length of the hash! the project structure should never
	 *              be that big.
	 * @con this would cause unnecessary refactoring activities for the project in
	 *      git repository.
	 *
	 * @decision use the first 5 characters from the generated hash!
	 * @pro it is common practice to shorten hashes.
	 * @con entropy might suffer too much from using only 5 chars.
	 */
	public static String getShortHash(String text) {
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
	 * @return absolute path to the directory of the git repository (parent
	 *         directory of .git directory).
	 */
	public String getPathToRepositoryInFileSystem() {
		return repositoryPath;
	}
}
