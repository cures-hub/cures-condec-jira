package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File system manager for git repositories.
 * 
 * Each git repository is stored in
 * JiraHome/data/condec-plugin/git/<project-key>/<MD5 hash of URI>.
 */
public class GitRepositoryFSManager {
	private static final String TEMP_DIR_PREFIX = "TEMP";
	private static final long BRANCH_OUTDATED_AFTER = 60 * 60 * 1000; // ex. 1 day = 24 hours * 60 minutes * 60 seconds
	// * 1000 miliseconds
	private String projectPath;
	private String projectUriPath;

	private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryFSManager.class);

	public GitRepositoryFSManager(String home, String projectKey, String repoUri, String defaultBranchName) {
		projectPath = home + File.separator + projectKey;
		projectUriPath = projectPath + File.separator + getShortHash(repoUri);
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
	private String getShortHash(String text) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(text.getBytes());
			byte[] digest = messageDigest.digest();
			return DatatypeConverter.printHexBinary(digest).toUpperCase().substring(0, 5);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("MD5 does not exist??");
			return "";
		}
	}

	/**
	 * @return absolute path to the directory of the default branch of a git
	 *         repository.
	 */
	public String getPath() {
		return projectUriPath;
	}
}
