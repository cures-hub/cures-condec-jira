package de.uhd.ifi.se.decision.management.jira.extraction.impl;

/**
 * @issue How to access commits related to a JIRA issue?
 * @decision Only use jGit.
 * @pro The jGit library is open source.
 * @alternative Both, the jgit library and the git integration for JIRA plugin
 *              were used to access git repositories.
 * @con An application link and oAuth is needed to call REST API on Java side.
 *
 *
 *      This implementation works well only with configuration for one remote
 *      git server. Multiple instances of this class are "thread-safe" in the
 *      limited way that the checked out branch files are stored in dedicated
 *      branch folders and can be read, modifing files is not safe and not
 *      supported.
 */
public class GitClient {

}