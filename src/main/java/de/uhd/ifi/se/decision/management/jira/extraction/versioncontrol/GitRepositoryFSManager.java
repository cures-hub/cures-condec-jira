package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class GitRepositoryFSManager {
	private static final String TEMP_DIR_PREFIX = "TEMP";
	private static final long BRANCH_OUTDATED_AFTER = 60 * 60 * 1000; //ex. 1 day = 24 hours * 60 minutes * 60 seconds * 1000 miliseconds
	private String baseProjectUriPath;
	private String baseProjectUriDefaultPath;

	private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryFSManager.class);

	public GitRepositoryFSManager(String home, String project, String repoUri, String defaultBranchName) {
		String baseProjectPath = home + File.separator + project;
		baseProjectUriPath = baseProjectPath + File.separator + getShortHash(repoUri);
		baseProjectUriDefaultPath = baseProjectUriPath + File.separator + defaultBranchName;
		// clean up if possible after previous requests.
		maintainNotUsedBranchPaths();
	}

	/**
	 * Returns target directory path for the default branch of the repository.
	 *
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
	 * This mehod stays public as the developer might intend
	 * to release the folder and not wait for maintenance
	 * strategy to trigger it.
	 *
	 * @param branchShortName branch name
	 * @return null on failure, absolute path to new temporary directory
	 */
	public String releaseBranchDirectoryNameToTemp(String branchShortName) {
		File oldDir = new File(getCheckoutPath(branchShortName));
		if (!oldDir.isDirectory()) {
			return null;
		} else {
			Date date = new Date();
			long time = date.getTime();
			String tempDirString = baseProjectUriPath
					+ File.separator
					+ TEMP_DIR_PREFIX + time;
			File tempDir = new File(tempDirString);
			boolean renameResult = false;
			try {
				renameResult = oldDir.renameTo(tempDir);
			} catch (Exception e) {
				LOGGER.error("Could not rename " + oldDir
						+ " to " + tempDirString + ". " + e.getMessage());
				return null;
			}
			if (!renameResult) {
				LOGGER.error("Could not rename " + oldDir
						+ " to " + tempDirString + ". The reason is not known.");
				return null;
			}
			removeBranchPathMarker(branchShortName);
			return tempDirString;
		}
	}

	/**
	 * Provides filesystem directory for targeted branch.
	 * Best case: branch already exists, costs no I/O operations.
	 * Good case: temporary folder exists and can be renamed to branch's
	 * target folder name.
	 * Bad case: branch folder is copied in I/O heavy operation
	 * from default branch.
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
		rememberBranchPathRequest(branchShortName);
		return getCheckoutPath(branchShortName);
	}

	/**
	 * @issue:file system does not allow all characters for folder and file name,
	 * therefore md5 can be used to get unique strings for inputs like uris etc.
	 * But md5 hashes can produce too long paths and corrupt the filesystem,
	 * especially for java projects. How can this be overcome?
	 *
	 * @alternative use full length of the hash! the project structure should
	 * never be that big.
	 * @con this would cause unnecessary refactoring activities for the project
	 * in git repository.
	 *
	 * @decision use the first 5 characters from the generated hash!
	 * @pro it is common practice to shorten hashes.
	 * @con entropy might suffer too much from using only 5 chars.
	 */
	private String getShortHash(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte[] digest = md.digest();
			return DatatypeConverter.printHexBinary(digest).toUpperCase().substring(0, 5);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("MD5 does not exist??");
			return "";
		}
	}


	/*
	 * Makes sure not too much disk space is wasted, if there
	 * is no need for many folders.
	 *
	 * Still does not prevent disk space waste when many branches
	 * need to be accessed in parallel.
	 */
	private void maintainNotUsedBranchPaths() {

		/* leave branches where they are until issues with repo checkouts are not fixed */
		boolean DEV_BUG_SKIP = true;
		if (DEV_BUG_SKIP) {
			return;
		}

		String[] notUsedBranchPaths = findOutdatedBranchPaths();
		if (notUsedBranchPaths != null)
			for (String branch : notUsedBranchPaths) {
				releaseBranchDirectoryNameToTemp(branch);
				LOGGER.info("Returned " + branch + " to temporary directory pool.");
			}
	}

	/*
	 * Writes files for each branch folder request, the creation date
	 * of these files can be later used for branch folder clean-ups.
	 */
	private void rememberBranchPathRequest(String branchShortName) {
		// ignore the last marker
		removeBranchPathMarker(branchShortName);
		// add new marker
		File file = new File(baseProjectUriPath, branchShortName);
		file.setWritable(true);
		try {
			// assumes branch names are valid file names
			FileUtils.writeStringToFile(file, getShortHash(branchShortName), StandardCharsets.UTF_8);
		} catch (IOException ex) {
			LOGGER.info(ex.getMessage());
		}
	}

	/* If the branch file marker does not exist, the maintenance
	 * shall not try to recycle the branch folder
	 */
	private void removeBranchPathMarker(String branchShortName) {
		File file = new File(baseProjectUriPath, branchShortName);
		file.delete();
	}

	private String getCheckoutPath(String checkoutPoint) {
		return baseProjectUriPath + File.separator + getShortHash(checkoutPoint);
	}

	private boolean useFromDefaultFolder(String branchShortName) {
		File defaultDir = new File(baseProjectUriDefaultPath);
		if (!defaultDir.isDirectory()) {
			return false;
		} else {
			try {
				File newDir = new File(getCheckoutPath(branchShortName));
				FileUtils.copyDirectory(defaultDir, newDir);
			} catch (Exception e) {
				LOGGER.error("Could not copy " + defaultDir
						+ " to " + getCheckoutPath(branchShortName) + ".\n\t" + e.getMessage());
				return false;
			}
		}
		return true;
	}

	private boolean useFromExistingBranchFolder(String branchShortName) {
		File dir = new File(getCheckoutPath(branchShortName));
		return dir.isDirectory();
	}

	private boolean useFromTemporaryFolder(String branchShortName) {
		String[] tempDirs = findTemporaryDirectoryNames();
		if (tempDirs == null || tempDirs.length < 1) {
			return false;
		}
		try {
			File dir = new File(baseProjectUriPath, tempDirs[0]); // get the 1st of temp dirs, but is 1st the best?
			File newDir = new File(getCheckoutPath(branchShortName));
			dir.renameTo(newDir);
		} catch (Exception e) {
			LOGGER.error("Could not rename " + tempDirs[0]
					+ " to " + getCheckoutPath(branchShortName) + ". " + e.getMessage());
			return false;
		}
		return true;
	}

	private String[] findTemporaryDirectoryNames() {
		File file = new File(baseProjectUriPath);
		String[] directories = file.list((current, name) ->
				(name.startsWith(TEMP_DIR_PREFIX)
						&& new File(current, name).isDirectory()));
		return directories;
	}

	/* Searches for files inside baseProjectUriPath
	 * and looks at their creation dates
	 */
	private String[] findOutdatedBranchPaths() {
		return findBranchPathFiles(true);
	}

	private String[] findBranchPathFiles(boolean getOutdated) {
		File file = new File(baseProjectUriPath);
		Date date = new Date();
		String[] branchFilteredTouchFiles = file.list((current, name) ->
		{
			long fileLifespan = date.getTime() - current.lastModified();
			boolean lifeSpanCondition = fileLifespan > BRANCH_OUTDATED_AFTER;
			if (!getOutdated) {
				lifeSpanCondition = !lifeSpanCondition;
			}
			boolean isFile = new File(current, name).isFile();
			return isFile && lifeSpanCondition;
		});
		return branchFilteredTouchFiles;
	}

	public boolean isBranchDirectoryInUse(String branchShortName) {
		String[] inUseList = findBranchPathFiles(false);
		if (inUseList != null) {
			for (String touchFileName : inUseList) {
				if (touchFileName.equals(branchShortName)) {
					return true;
				}
			}
		}
		return false;
	}
}
