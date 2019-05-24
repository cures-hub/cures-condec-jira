package de.uhd.ifi.se.decision.management.jira.extraction.tangledcommitdetection;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFileImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DiffImpl;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestDiff extends TestSetUpGit {
    private DiffImpl diffsWithMoreThanOneCommits;
    private DiffImpl diffsWithOneCommit;


    public DiffImpl mapToDiff(GitClient gitClient, String jiraIssueKey) {
        DiffImpl mappedDiff = new DiffImpl();
        Map<DiffEntry, EditList> diff1 = gitClient.getDiff(jiraIssueKey);
        for (Map.Entry<DiffEntry, EditList> entry : diff1.entrySet()) {
            File file = new File(gitClient.getDirectory().toString().replace(".git", "") + entry.getKey().getNewPath());
            mappedDiff.addChangedFileImpl(new ChangedFileImpl(entry.getValue(), file));
        }
        return mappedDiff;
    }

    @Before
    public void setUp() {
        super.setUp();
        diffsWithMoreThanOneCommits = mapToDiff(gitClient, "TEST-66");
        diffsWithOneCommit = mapToDiff(gitClient, "TEST-77");

    }

    @Test
    public void createDiff() {
        DiffImpl diff = new DiffImpl();
        assertEquals(0 , diff.getChangedFileImpls().size());
    }

    @Test
    public void testGetChangedFileImplsWithMoreThanOneCommits() {
        assertEquals(3 , diffsWithMoreThanOneCommits.getChangedFileImpls().size());
    }

    @Test
    public void testGetChangedFileImplsWithOneCommit() {
        assertEquals(1 , diffsWithOneCommit.getChangedFileImpls().size());
    }

    @Test
    public void testAddChangedFileImpl() {
        diffsWithOneCommit.addChangedFileImpl(null);
        assertEquals(2 , diffsWithOneCommit.getChangedFileImpls().size());
    }


}
