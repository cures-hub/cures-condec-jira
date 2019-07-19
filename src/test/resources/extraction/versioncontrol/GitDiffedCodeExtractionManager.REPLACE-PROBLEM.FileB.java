package de.uhd.ifi.se.decision.management.jira.extraction.impl;
/*
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.CoreConfig.AutoCRLF;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.google.common.collect.Lists;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryFSManager;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.model.git.impl.ChangedFileImpl;
import de.uhd.ifi.se.decision.management.jira.model.git.impl.DiffImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;*/

/**
 * @issue How to access commits related to a JIRA issue?
 * @decision Only use jGit.
 * @pro The jGit library is open source.
 * @alternative Both, the jgit library and the git integration for JIRA plugin
 * were used to access git repositories.
 * @con An application link and oAuth is needed to call REST API on Java side.
 *
 *
 * This implementation works well only with configuration for one remote
 * git server. Multiple instances of this class are "thread-safe" in the
 * limited way that the checked out branch files are stored in dedicated
 * branch folders and can be read, modifing files is not safe and not
 * supported.
 */
public class GitClientImpl implements GitClient {

}