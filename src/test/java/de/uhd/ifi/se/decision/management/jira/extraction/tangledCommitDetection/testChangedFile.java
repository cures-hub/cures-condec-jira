package de.uhd.ifi.se.decision.management.jira.extraction.tangledCommitDetection;

import com.github.javaparser.ast.body.MethodDeclaration;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFileImpl;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class testChangedFile extends TestSetUpGit {

    private ChangedFileImpl changedFile;
    private Map<DiffEntry, EditList> diffsWithOneCommit;


    public Map<DiffEntry, EditList> GetDiff(GitClient gitClient, String jiraIssueKey) {
        return gitClient.getDiff(jiraIssueKey);
    }

    public void setChangedFile() {
        for (Map.Entry<DiffEntry, EditList> entry : diffsWithOneCommit.entrySet()) {
            File file = new File(gitClient.getDirectory().toString().replace(".git", "") + entry.getKey().getNewPath());
            changedFile = new ChangedFileImpl(entry.getValue(), file);
        }
    }

    @Before
    public void setUp() {
        super.setUp();
        diffsWithOneCommit = GetDiff(gitClient, "TEST-77");
    }

    @Test
    public void testCreatChangedFile() {
        setChangedFile();
        assertNotNull(changedFile);
    }

    @Test
    public void testGetFile() {
        setChangedFile();
        assertNotNull(changedFile.getFile());
    }

    @Test
    public void testCompilationUnit() {
        setChangedFile();
        assertNotNull(changedFile.getCompilationUnit());
    }

    @Test
    public void testGetSetPackageDistance() {
        setChangedFile();
        changedFile.setPackageDistance(10);
        assertEquals(10, changedFile.getPackageDistance());
    }

    @Test
    public void testAddDistances() {
        setChangedFile();
        changedFile.addLineDistance(10);
        changedFile.addMethodDistance(9);
        changedFile.addPathDistance(8);
    }

    @Test
    public void testGetSetMethodDeclarations() {
        setChangedFile();
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        changedFile.setMethodDeclarations(methodDeclaration);
        assertEquals(1, changedFile.getMethodDeclarations().size());
    }
    @Test
    public void testGetSetPercentage() {
        setChangedFile();
        assertEquals(0, changedFile.getPercentage(),0.00);
        changedFile.setPercentage(80);
        assertEquals(80, changedFile.getPercentage(),0.00);
    }

    @Test
    public void testGetEditList(){
        setChangedFile();
        assertNotNull(changedFile.getEditList());
    }

}
