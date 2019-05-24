package de.uhd.ifi.se.decision.management.jira.extraction.tangledcommitdetection;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;

import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFileImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DiffImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.TangledCommitDetectionImpl;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestTangledCommitDetection extends TestSetUpGit {

    private DiffImpl diffsWithMoreThanOneCommits;
    private DiffImpl diffsWithOneCommit;
    private TangledCommitDetectionImpl tangledCommitDetection;


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
        tangledCommitDetection = new TangledCommitDetectionImpl();
        diffsWithMoreThanOneCommits = mapToDiff(gitClient, "TEST-66");
        diffsWithOneCommit = mapToDiff(gitClient, "TEST-77");

    }
    @Ignore
    public void testCalculatePackageDistances() {
        tangledCommitDetection.calculatePackageDistances(diffsWithMoreThanOneCommits);
        assertEquals(2 , diffsWithMoreThanOneCommits.getChangedFileImpls().get(0).getPackageDistance());
        assertEquals(2 , diffsWithMoreThanOneCommits.getChangedFileImpls().get(1).getPackageDistance());
        assertEquals(4 , diffsWithMoreThanOneCommits.getChangedFileImpls().get(2).getPackageDistance());
    }

    @Test
    public void testCalculatePackageDistance() {
        tangledCommitDetection.calculatePackageDistances(diffsWithOneCommit);
        assertEquals(0 , diffsWithOneCommit.getChangedFileImpls().get(0).getPackageDistance());
    }

    @Test
    public void testStandardizationWithMoreThanOneCommits() {
        tangledCommitDetection.calculatePackageDistances(diffsWithMoreThanOneCommits);
        //sort after standardization
        tangledCommitDetection.standardization(diffsWithMoreThanOneCommits);
        assertEquals(100.0 , diffsWithMoreThanOneCommits.getChangedFileImpls().get(0).getPercentage(),0.0000);
        assertEquals(50.0 , diffsWithMoreThanOneCommits.getChangedFileImpls().get(1).getPercentage(),0.0000);
        assertEquals(50.0 , diffsWithMoreThanOneCommits.getChangedFileImpls().get(2).getPercentage(), 0.0000);

    }

    @Test
    public void testStandardizationWithOneCommit() {
        tangledCommitDetection.calculatePackageDistances(diffsWithOneCommit);
        tangledCommitDetection.standardization(diffsWithOneCommit);
        assertEquals(0 , diffsWithOneCommit.getChangedFileImpls().get(0).getPercentage(),0.0000);

    }

    @Test
    public void testParsePackage() {
        assertEquals(9 , tangledCommitDetection.parsePackage(diffsWithOneCommit.getChangedFileImpls().get(0).getCompilationUnit().getPackageDeclaration()).size());
    }

    @Test
    public void testIsAllChangesInOnePackageWithMoreThanOneCommits() {
        assertFalse(tangledCommitDetection.isAllChangesInOnePackage(diffsWithMoreThanOneCommits));
    }

    @Test
    public void testIsAllChangesInOnePackageWithOneCommit() {
        assertTrue(tangledCommitDetection.isAllChangesInOnePackage(diffsWithOneCommit));
    }

    @Test
    public void testIsAllChangesInMethodsWithOneCommit() {
        assertTrue(tangledCommitDetection.isAllChangesInOnePackage(diffsWithOneCommit));
    }

    @Test
    public void testIsAllChangesInMethodsWithMoreThanOneCommits() {
        assertFalse(tangledCommitDetection.isAllChangesInOnePackage(diffsWithMoreThanOneCommits));
    }



}
