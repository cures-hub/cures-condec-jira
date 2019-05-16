package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.nio.file.Files;
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
	 * @return absolute path to directory of the default branch
	 */
	public String getDefaultBranchPath() {
		return baseProjectUriDefaultPath;
	}

	/**
	 * Makes branch's folder available in the temporary pool.
	 * It can significantly contribute to improving the speed
	 * of check-outs of other branches as folder renaming
	 * is not costly compared to copying.
	 *
	 * @param branchShortName branch name
	 * @return null on failure, absolute path to new temporary directory
	 */
	public String releaseBranchDirectoryNameToTemp(String branchShortName) {
		File oldDir = new File(getBranchPath(branchShortName));
		if (!oldDir.isDirectory()) {
			return null;
		}
		else {
			Date date= new Date();
			long time = date.getTime();
			String tempDirString = baseProjectUriPath
					+File.separator
					+TEMP_DIR_PREFIX+String.valueOf(time);
			File tempDir = new File(tempDirString);
			try {
				oldDir.renameTo(tempDir);
			}
			catch (Exception e) {
				LOGGER.error("Could not rename "+oldDir
						+" to "+tempDirString+". "+e.getMessage());
				return null;
			}
			return tempDirString;
		}
	}

	/**
	 * Provides filesystem directory for targeted branch.
	 * Best case: branch already exists, costs no I/O operations.
	 * Good case: temporary folder exists and can be renamed to branch's
	 * 	target folder name.
	 * Bad case: branch folder is copied in I/O havey operation
	 * 	from default branch.
	 *
	 * @param branchShortName branch name
	 * @return null on failure, absolute path to branch's directory
	 */
	public String prepareBranchDirectory(String branchShortName) {
		if (!useFromExistingBranchFolder(branchShortName)
			&& !useFromTemporaryFolder(branchShortName)
			&& !useFromDefaultFolder(branchShortName)) {
			LOGGER.warn("Neither branch, nor temporary," +
					" nor default folder could be found under: "
					+ baseProjectUriPath);
			return null;
		}
		return getBranchPath(branchShortName);
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

	private String getBranchPath(String branchShortName) {
		return baseProjectUriPath+File.separator+getHash(branchShortName);
	}

	private boolean useFromDefaultFolder(String branchShortName) {
		File defaultDir = new File(baseProjectUriDefaultPath);
		if (!defaultDir.isDirectory()) {
			return false;
		}
		else {
			try {
				File newDir = new File(getBranchPath(branchShortName));
				FileUtils.copyDirectory(defaultDir,newDir);
			}
			catch (Exception e) {
				LOGGER.error("Could not copy "+defaultDir
						+" to "+getBranchPath(branchShortName)+".\n\t"+e.getMessage());
				return false;
			}
		}
		return true;
	}

	private boolean useFromExistingBranchFolder(String branchShortName) {
		File dir = new File(getBranchPath(branchShortName));
		return dir.isDirectory();
	}

	private boolean useFromTemporaryFolder(String branchShortName) {
		String[] tempDirs = findTemporaryDirectoryNames();
		if (tempDirs==null || tempDirs.length<1) {
			return false;
		}
		try {
			File dir = new File(baseProjectUriPath+File.separator
					+tempDirs[0]); // get the 1st of temp dirs
			File newDir = new File(getBranchPath(branchShortName));
			dir.renameTo(newDir);
		}
		catch (Exception e) {
			LOGGER.error("Could not rename "+tempDirs[0]
					+" to "+getBranchPath(branchShortName)+". "+e.getMessage());
			return false;
		}
		return true;
	}

	private String[] findTemporaryDirectoryNames() {
		File file = new File(baseProjectUriPath);
		String[] directories = file.list((current, name) ->
				(name.toString().startsWith(TEMP_DIR_PREFIX)
				&& new File(current, name).isDirectory()));
		return directories;
	}
}
