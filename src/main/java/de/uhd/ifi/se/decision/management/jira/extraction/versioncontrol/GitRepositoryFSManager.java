package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class GitRepositoryFSManager {
	private static final String TEMP_DIR_PREFIX = "TEMP";
	private String basePath;
	private String baseProjectPath;
	private String baseProjectUriPath;
	private String baseProjectUriDefaultPath;

	private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryFSManager.class);

	public GitRepositoryFSManager(String home, String project, String repoUri, String defaultBranch) {
		basePath = home;
		baseProjectPath = basePath + File.separator + project;
		baseProjectUriPath = baseProjectPath + File.separator + getHash(repoUri);
		baseProjectUriDefaultPath = baseProjectUriPath + File.separator + defaultBranch;
	}

	/**
	 * Returns target directory path for the default branch of the repository.
	 *
	 * @return absolute path to directory of the default branch
	 */
	public String getDefaultBranchPath() {
		return baseProjectUriDefaultPath;
	}

	private String getHash(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte[] digest = md.digest();
			return DatatypeConverter.printHexBinary(digest).toUpperCase();
		}
		catch (NoSuchAlgorithmException e) {
			LOGGER.error("MD5 does not exist??");
			return "";
		}
	}
}
