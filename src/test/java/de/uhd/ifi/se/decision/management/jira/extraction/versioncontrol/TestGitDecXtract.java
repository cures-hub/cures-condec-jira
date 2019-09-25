package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import org.eclipse.jgit.lib.Ref;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

public class TestGitDecXtract extends TestSetUpGit {
    private GitDecXtract gitDecX;

    @Test
    public void nullOrEmptyFeatureBranchCommits() {
        // git repository is setup already
        gitDecX = new GitDecXtract("TEST", getExampleUri());
        int numberExpectedElements = 0;
        List<DecisionKnowledgeElement> gotElements = gitDecX.getElements((String) null);
        Assert.assertEquals(numberExpectedElements, gotElements.size());

        gotElements = gitDecX.getElements("");
        Assert.assertEquals(numberExpectedElements, gotElements.size());

        gotElements = gitDecX.getElements("doesNotExistBranch");
        Assert.assertEquals(numberExpectedElements, gotElements.size());
    }

    @Test
    public void fromFeatureBranchCommits() {
        // git repository is setup already
        gitDecX = new GitDecXtract("TEST", getExampleUri());
        // 5 code rationale exists in main branch, will be changed in feature branch
        // feature branch: 5 in messages + 10 in final files + 3 outdated
        int numberExpectedElements = 5 + 10 + 3;

        // by branch name
        List<DecisionKnowledgeElement> gotElements = gitDecX.getElements("featureBranch");
        Assert.assertEquals(numberExpectedElements, gotElements.size());

        // by Ref, find Ref first
        List<Ref> featureBranches = gitClient.getRemoteBranches();
        Ref featureBranch = null;
        Iterator<Ref> it = featureBranches.iterator();
        while (it.hasNext()) {
            Ref value = it.next();
            if (value.getName().endsWith("featureBranch")) {
                featureBranch = value;
                return;
            }
        }

        gotElements = gitDecX.getElements(featureBranch);
        Assert.assertEquals(numberExpectedElements, gotElements.size());
    }

    @Test
    public void fromFeatureBranchCommitsNullInput() {
        gitDecX = new GitDecXtract("TEST", getExampleUri());

        List<DecisionKnowledgeElement> gotElements = gitDecX.getElements((String) null);
        Assert.assertNotNull(gotElements);
        Assert.assertEquals(0, gotElements.size());

        gotElements = gitDecX.getElements((Ref) null);
        Assert.assertNotNull(gotElements);
        Assert.assertEquals(0, gotElements.size());

    }
}
