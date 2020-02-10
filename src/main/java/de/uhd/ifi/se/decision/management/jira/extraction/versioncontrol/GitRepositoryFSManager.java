package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitRepositoryFSManager {
    private static final String TEMP_DIR_PREFIX = "TEMP";
    private static final long BRANCH_OUTDATED_AFTER = 60 * 60 * 1000; // ex. 1 day = 24 hours * 60 minutes * 60 seconds
								      // * 1000 miliseconds
    private Map<String, String> baseProjectUriPaths;
    private Map<String, String> baseProjectUriDefaultPaths;

    private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryFSManager.class);

    public GitRepositoryFSManager(String home, String project, List<String> repoUris,
	    Map<String, String> defaultBranchNames) {
	baseProjectUriPaths = new HashMap<String, String>();
	baseProjectUriDefaultPaths = new HashMap<String, String>();
	String baseProjectPath = home + File.separator + project;
	for (int i = 0; i < repoUris.size(); i++) {
	    baseProjectUriPaths.put(repoUris.get(i), baseProjectPath + File.separator + getShortHash(repoUris.get(i)));
	    if (repoUris.size() == defaultBranchNames.keySet().size()) {
		baseProjectUriDefaultPaths.put(repoUris.get(i), baseProjectUriPaths.get(repoUris.get(i))
			+ File.separator + defaultBranchNames.get(repoUris.get(i)));
	    } else {
		baseProjectUriDefaultPaths.put(repoUris.get(i),
			baseProjectUriPaths.get(repoUris.get(i)) + File.separator + "develop");
	    }
	}
	// clean up if possible after previous requests.
	maintainNotUsedBranchPaths();
    }

    /**
     * Returns target directory paths for the default branch of the repositories.
     *
     * @return absolute path to directories of the default branch
     */
    public Map<String, String> getDefaultBranchPaths() {
	return baseProjectUriDefaultPaths;
    }

    /**
     * Makes branch's folder available in the temporary pool. It can significantly
     * contribute to improving the speed of check-outs of other branches as folder
     * renaming is not costly compared to copying.
     *
     * This method stays public as the developer might intend to release the folder
     * and not wait for maintenance strategy to trigger it.
     *
     * @param branchShortName branch name, Repository Uri for branch
     * @return null on failure, absolute path to new temporary directory
     */
    public String releaseBranchDirectoryNameToTemp(String branchShortName, String repoUri) {
	if (baseProjectUriPaths.containsKey(repoUri)) {
	    String checkoutPath = getCheckoutPath(branchShortName, repoUri);
	    if (!checkoutPath.equals("")) {
		File oldDir = new File(getCheckoutPath(branchShortName, repoUri));
		if (!oldDir.isDirectory()) {
		    return null;
		} else {
		    Date date = new Date();
		    long time = date.getTime();
		    String tempDirString = baseProjectUriPaths.get(repoUri) + File.separator + TEMP_DIR_PREFIX + time;
		    File tempDir = new File(tempDirString);
		    boolean renameResult = false;
		    try {
			renameResult = oldDir.renameTo(tempDir);
		    } catch (Exception e) {
			LOGGER.error("Could not rename " + oldDir + " to " + tempDirString + ". " + e.getMessage());
			return null;
		    }
		    if (!renameResult) {
			LOGGER.error(
				"Could not rename " + oldDir + " to " + tempDirString + ". The reason is not known.");
			return null;
		    }
		    removeBranchPathMarker(branchShortName, repoUri);
		    return tempDirString;
		}
	    }
	}
	return null;
    }

    /**
     * Provides filesystem directory for targeted branch. Best case: branch already
     * exists, costs no I/O operations. Good case: temporary folder exists and can
     * be renamed to branch's target folder name. Bad case: branch folder is copied
     * in I/O heavy operation from default branch.
     *
     * @param branchShortName branch name, Repository Uri of branch
     * @return null on failure, absolute path to branch's directory
     */
    public String prepareBranchDirectory(String branchShortName, String repoUri) {
	if (baseProjectUriPaths.containsKey(repoUri)) {
	    if (!useFromExistingBranchFolder(branchShortName, repoUri)
		    && !useFromTemporaryFolder(branchShortName, repoUri)
		    && !useFromDefaultFolder(branchShortName, repoUri)) {
		LOGGER.warn("Neither branch, nor temporary," + " nor default folder could be found under: "
			+ baseProjectUriPaths.get(repoUri));
		return null;
	    }
	    rememberBranchPathRequest(branchShortName, repoUri);
	    return getCheckoutPath(branchShortName, repoUri);
	}
	return "";
    }

    /**
     * @issue:file system does not allow all characters for folder and file name,
     *             therefore md5 can be used to get unique strings for inputs like
     *             uris etc. But md5 hashes can produce too long paths and corrupt
     *             the filesystem, especially for java projects. How can this be
     *             overcome?
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
     * Makes sure not too much disk space is wasted, if there is no need for many
     * folders.
     *
     * Still does not prevent disk space waste when many branches need to be
     * accessed in parallel.
     */
    private void maintainNotUsedBranchPaths() {

	/*
	 * leave branches where they are until issues with repo checkouts are not fixed
	 */
	boolean DEV_BUG_SKIP = true;
	if (DEV_BUG_SKIP) {
	    return;
	}

	Map<String, List<String>> notUsedBranchPaths = findOutdatedBranchPaths();
	Set<String> repoUris = notUsedBranchPaths.keySet();
	for (String repoUri : repoUris) {
	    if (notUsedBranchPaths.get(repoUri) != null) {
		for (String branch : notUsedBranchPaths.get(repoUri)) {
		    releaseBranchDirectoryNameToTemp(branch, repoUri);
		    LOGGER.info("Returned " + branch + " to temporary directory pool.");
		}
	    }
	}

    }

    /*
     * Writes files for each branch folder request, the creation date of these files
     * can be later used for branch folder clean-ups.
     */
    private void rememberBranchPathRequest(String branchShortName, String repoUri) {
	if (baseProjectUriPaths.containsKey(repoUri)) {
	    // ignore the last marker
	    removeBranchPathMarker(branchShortName, repoUri);
	    // add new marker
	    File file = new File(baseProjectUriPaths.get(repoUri), branchShortName);
	    file.setWritable(true);
	    try {
		// assumes branch names are valid file names
		FileUtils.writeStringToFile(file, getShortHash(branchShortName), StandardCharsets.UTF_8);
	    } catch (IOException ex) {
		LOGGER.info(ex.getMessage());
	    }
	}

    }

    /*
     * If the branch file marker does not exist, the maintenance shall not try to
     * recycle the branch folder
     */
    private void removeBranchPathMarker(String branchShortName, String repoUri) {
	if (baseProjectUriPaths.containsKey(repoUri)) {
	    File file = new File(baseProjectUriPaths.get(repoUri), branchShortName);
	    file.delete();
	}
    }

    private String getCheckoutPath(String checkoutPoint, String repoUri) {
	if (baseProjectUriPaths.containsKey(repoUri)) {
	    return baseProjectUriPaths.get(repoUri) + File.separator + getShortHash(checkoutPoint);
	}
	return "";
    }

    private boolean useFromDefaultFolder(String branchShortName, String repoUri) {
	if (baseProjectUriDefaultPaths.containsKey(repoUri)) {
	    File defaultDir = new File(baseProjectUriDefaultPaths.get(repoUri));
	    if (!defaultDir.isDirectory()) {
		return false;
	    } else {
		try {
		    File newDir = new File(getCheckoutPath(branchShortName, repoUri));
		    FileUtils.copyDirectory(defaultDir, newDir);
		} catch (Exception e) {
		    LOGGER.error("Could not copy " + defaultDir + " to " + getCheckoutPath(branchShortName, repoUri)
			    + ".\n\t" + e.getMessage());
		    return false;
		}
	    }
	    return true;
	}
	return false;
    }

    private boolean useFromExistingBranchFolder(String branchShortName, String repoUri) {
	File dir = new File(getCheckoutPath(branchShortName, repoUri));
	return dir.isDirectory();
    }

    private boolean useFromTemporaryFolder(String branchShortName, String repoUri) {
	if (baseProjectUriPaths.containsKey(repoUri)) {
	    List<String> tempDirs = findTemporaryDirectoryNames();
	    if (tempDirs == null || tempDirs.size() < 1) {
		return false;
	    }
	    try {
		File dir = new File(baseProjectUriPaths.get(repoUri), tempDirs.get(0)); // get the 1st of temp dirs, but
											// is
		// 1st the best?
		File newDir = new File(getCheckoutPath(branchShortName, repoUri));
		dir.renameTo(newDir);
	    } catch (Exception e) {
		LOGGER.error("Could not rename " + tempDirs.get(0) + " to " + getCheckoutPath(branchShortName, repoUri)
			+ ". " + e.getMessage());
		return false;
	    }
	    return true;
	}
	return false;
    }

    private List<String> findTemporaryDirectoryNames() {
	Set<String> repoUris = baseProjectUriPaths.keySet();
	List<String> directories = new ArrayList<String>();
	for (String repoUri : repoUris) {
	    File file = new File(baseProjectUriPaths.get(repoUri));
	    String[] repodirectories = file.list(
		    (current, name) -> (name.startsWith(TEMP_DIR_PREFIX) && new File(current, name).isDirectory()));
	    if (repodirectories != null) {
		directories.addAll(Arrays.asList(repodirectories));
	    }
	}
	return directories;
    }

    /*
     * Searches for files inside baseProjectUriPaths and looks at their creation
     * dates
     */
    private Map<String, List<String>> findOutdatedBranchPaths() {
	return findBranchPathFiles(true);
    }

    private Map<String, List<String>> findBranchPathFiles(boolean getOutdated) {
	Set<String> repoUris = baseProjectUriPaths.keySet();
	Map<String, List<String>> branchMap = new HashMap<String, List<String>>();
	for (String key : repoUris) {
	    List<String> branchFilteredTouchFilesList = new ArrayList<String>();
	    String baseProjectUriPath = baseProjectUriPaths.get(key);
	    File file = new File(baseProjectUriPath);
	    Date date = new Date();
	    String[] branchFilteredTouchFiles = file.list((current, name) -> {
		long fileLifespan = date.getTime() - current.lastModified();
		boolean lifeSpanCondition = fileLifespan > BRANCH_OUTDATED_AFTER;
		if (!getOutdated) {
		    lifeSpanCondition = !lifeSpanCondition;
		}
		boolean isFile = new File(current, name).isFile();
		return isFile && lifeSpanCondition;
	    });
	    branchFilteredTouchFilesList.addAll(Arrays.asList(branchFilteredTouchFiles));
	    branchMap.put(key, branchFilteredTouchFilesList);
	}
	return branchMap;

    }

    public boolean isBranchDirectoryInUse(String branchShortName) {
	Map<String, List<String>> inUseList = findBranchPathFiles(false);
	Set<String> repoUris = inUseList.keySet();
	for (String repoUri : repoUris) {
	    if (inUseList.get(repoUri) != null) {
		for (String touchFileName : inUseList.get(repoUri)) {
		    if (touchFileName.equals(branchShortName)) {
			return true;
		    }
		}
	    }
	}
	return false;
    }
}
