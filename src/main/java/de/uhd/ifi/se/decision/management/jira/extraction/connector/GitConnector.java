package de.uhd.ifi.se.decision.management.jira.extraction.connector;

import static java.util.Collections.singleton;

import java.io.File;
import java.io.IOException;

import de.uhd.ifi.se.decision.management.jira.config.GitConfig;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * This class handles the Git operations.
 */
public class GitConnector {
    /**
     * URI the Repository should be cloned from.
     */
    private String uri;

    /**
     * The Git Object for the cloned Repository, to perform the operations on.
     */
    private Git git;

    /**
     * The directory the repository should be cloned to.
     */
    private File directory;

    /**
     * Store the login information to get access to the remote.
     * Is needed for cloning, pulling and pushing
     */
    private UsernamePasswordCredentialsProvider userNamePassword;

    /**
     * Initializes the uri and the repository with the given variables for the git.
     * @param uri to clone the repository from.
     * @param directory to clone the git repository to.
     */
    public GitConnector(String uri, String directory) {
        this.uri = uri;
        this.directory = new File(directory);

        userNamePassword = new UsernamePasswordCredentialsProvider("jgit", "VCSs3cr3T");
    }

    /**
     * Initializes the uri with the given one and sets the directory to an default value
     * @param uri to clone the repository from.
     */
    /*
    public GitConnector(String uri) {
        this.uri = uri;
        this.directory = new File("/var/tmp/fd/FeatureRefactoringPluginRepository");

        userNamePassword = new UsernamePasswordCredentialsProvider("jgit", "VCSs3cr3T");
    }*/

    /**
     * Initializes the uri with the set one of the GitConfig and sets the directory to an default value
     * @param projectKey to get the repository from.
     */
    public GitConnector(String projectKey){
        GitConfig config = new GitConfig(projectKey);
        this.uri = config.getPath();
        this.directory = new File("/var/tmp/fd/FeatureRefactoringPluginRepository");

        userNamePassword = new UsernamePasswordCredentialsProvider("jgit", "VCSs3cr3T");
    }

    /**
     * Clones the Repo to a specific directory.
     */
    public void cloneRepo() throws GitAPIException {
        try {
            if(existingRepository()) {
                git.pull().call();
            } else {
                try {
                    CloneCommand cloneCommand = Git.cloneRepository();
                    cloneCommand.setCredentialsProvider(userNamePassword);
                    cloneCommand.setBare(false);
                    cloneCommand.setCloneAllBranches(false);
                    cloneCommand.setBranchesToClone(singleton("refs/heads/master"));
                    cloneCommand.setBranch("refs/heads/master");
                    cloneCommand.setDirectory(directory).setURI(uri);
                    this.git = cloneCommand.call();
                } catch (GitAPIException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pulls the changes from origin master to the local copy of the repository.
     * Returns true if there was no merge, returns false if there was a merge.
     * @return true if no merges occurred while pulling, false if there was a merge.
     * @throws GitAPIException
     */
    public boolean pull() throws GitAPIException {
        try {
            //test whether the repo exists already. Connect to it if it exists
            if (existingRepository()) {
                try {
                    //pull
                    PullResult result = git.pull().setCredentialsProvider(userNamePassword).call();
                    MergeResult mergeResult = result.getMergeResult();
                    //return if a merge happened, that could not be resolved.
                    return mergeResult.getMergeStatus().isSuccessful();
                } catch(GitAPIException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        //if no repo exist there could not be pulled, so there were no merges
        return true;
    }

    /**
     * commits the changes to the master branch of the selected repository.
     * for this task all unstaged changes are added to the index.
     * @param message to be shown in the commit e.g. changed Features: X, Y
     * @throws IOException
     * @throws GitAPIException
     */
    public void commit(String message) throws IOException, GitAPIException {
        /*
         * provide an hint that the commit and it's changes were auto generated
         */
        final String AUTO = "[AUTO]";
        final String AUTO_HINT = "This commit messages and it's changes were automatically created using the feature " +
                "refactoring jira plugin.";

        if(existingRepository()) {
            //add all unstaged files and changes to the index.
            addAll();

            //commit all changes.
            CommitCommand commitCommand = git.commit();
            commitCommand.setAll(true);
            commitCommand.setMessage(AUTO + message + "\n" + AUTO_HINT);
            try {
                commitCommand.call();
            } catch (GitAPIException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * commits the changes to the master branch of the selected repository.
     * for this task all unstaged changes are added to the index.
     * And finally pushes the commit to the master branch.
     * @param message to be shown in the commit e.g. changed Features: X, Y
     * @throws IOException
     * @throws GitAPIException
     */
    public void commitAndPush(String message) throws IOException, GitAPIException {
        commit(message);

        PushCommand pushCommand = git.push();
        pushCommand.setRemote(uri);
        pushCommand.setPushAll();
        pushCommand.setCredentialsProvider(userNamePassword);
        pushCommand.call();

        //RefSpec spec = new RefSpec("refs/heads/master");
        //this.git.push().setRefSpecs(spec).setForce(true).setRemote("origin").setCredentialsProvider(new UsernamePasswordCredentialsProvider("jkeller", "968uNn")).call();
    }

    /**
     * add all staged and unstaged files to the repository index
     * @throws IOException
     * @throws GitAPIException
     */
    private void addAll() throws IOException, GitAPIException {
        if(existingRepository()) {
            try {
                git.add().addFilepattern(".").call();
            } catch (GitAPIException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * checks whether the git instance is null or no repository
     * @return true if the repository could successfully be get or false if not (something went wrong)
     * @throws IOException
     */
    private boolean existingRepository() throws IOException {
        if(directory.exists()) {
            if(directory.list().length > 0) {
                if(git != null) {
                    closeRepo();
                }
                try {
                    this.git = Git.open(directory);
                } catch (IOException io) {
                    io.printStackTrace();
                    throw io;
                }
            }
        }
        return checkExistingRepository();
    }

    /**
     * check whether the current directory is an git repository
     * @return true if the directory is an repository false otherwise
     * @throws IOException
     */
    private boolean checkExistingRepository() throws IOException {
//        if(git != null) {
//            try {
//                if (git.getRepository().getRef("HEAD") != null) {
//                    return true;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw e;
//            }
//        }
        return false;
    }

    /**
     * Closes the Repo.
     */
    public void closeRepo() {
        if(git != null) {
            this.git.getRepository().close();
            this.git.close();
        }
    }

    /**
     * Closes the Repo and deletes its local files.
     */
    public void closeAndDeleteRepo() {
        if(git != null) {
            this.git.getRepository().close();
            this.git.close();
        }

        try {
            if(directory.exists()){
                FileUtils.deleteDirectory(directory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return The file (directory) where the Repository was cloned to.
     */
    public File getRepositoryDirectoryAsFile() {
        return this.directory;
    }

    public String getRepositoryDirectoryAsString() {
        return this.directory.getAbsolutePath();
    }

    public void setDirectory(String directory) {
        this.directory = new File(directory);
    }
}
