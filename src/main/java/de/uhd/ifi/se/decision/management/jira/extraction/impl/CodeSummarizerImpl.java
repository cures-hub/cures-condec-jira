package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.extraction.*;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.body.MethodDeclaration;

public class CodeSummarizerImpl implements CodeSummarizer {

    private GitClient gitClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeSummarizerImpl.class);
    private int minProbability;
    private String projectKey;
    private String issueKey;
    public CodeSummarizerImpl(String projectKey) {
        this.projectKey = projectKey;
        this.gitClient = new GitClientImpl(projectKey);
    }

	@Override
	public String createSummary(Issue jiraIssue, int probability) {
        this.minProbability = probability;
        this.issueKey = jiraIssue.getKey();
		if (jiraIssue == null) {
			return "";
		}
		Map<DiffEntry, EditList> diff = gitClient.getDiff(jiraIssue);
		return createSummary(diff);
	}

    @Override
    public String createSummary(RevCommit commit) {
        if (commit == null) {
            return "";
        }
        Map<DiffEntry, EditList> diff = gitClient.getDiff(commit);
        return createSummary(diff);
    }

    @Override
    public String createSummary(Map<DiffEntry, EditList> diff) {
        if (diff == null || diff.size() == 0) {
            return "";
        }
        DiffImpl allDiffs = new DiffImpl();
        for (Map.Entry<DiffEntry, EditList> entry : diff.entrySet()) {
            File file = new File(gitClient.getDirectory().toString().replace(".git", "") + entry.getKey().getNewPath());
            allDiffs.addChangedFileImpl(new ChangedFileImpl(entry.getValue(), file));
        }
        try {
            TangledCommitDetection.getMethods(allDiffs);
            TangledCommitDetectionImpl tangledCommitDetection = new TangledCommitDetectionImpl();
            tangledCommitDetection.calculatePredication(allDiffs);
        }catch (Exception e){
            LOGGER.error("calculation fails");
            return "";
        }
        //make easy for mapper
        Vector<SimplifiedChangedFile> simplifiedChangedFiles = new Vector<>();
        for (ChangedFileImpl changedFile: allDiffs.getChangedFileImpls()) {
            simplifiedChangedFiles.add(ChangedFile.getSimplified(changedFile));
        }
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(simplifiedChangedFiles);
            System.out.println(jsonString);
        }catch (IOException e){
            e.printStackTrace();
        }
        try{
            Diff.sendPost(this.projectKey, this.issueKey, jsonString);
        }catch (Exception e){
            e.printStackTrace();
        }

        return generateSummary(allDiffs);
    }

    private String generateSummary(DiffImpl diffImpl){
        String rows ="";
        for(ChangedFileImpl changedFileImpl : diffImpl.getChangedFileImpls()){
            if(changedFileImpl.getPercentage() >= this.minProbability){
                rows += this.addRow(this.addTableItem(FilenameUtils.removeExtension(changedFileImpl.getFile().getName()),
                        this.summarizeMethods(changedFileImpl),Float.toString(changedFileImpl.getPercentage())));
            }
        }
        return this.generateTable(rows);
    }

    private String summarizeMethods(ChangedFileImpl changedFileImpl){
        String summarizedMethods ="";
        for (MethodDeclaration methodDeclaration : changedFileImpl.getMethodDeclarations()) {
            summarizedMethods += methodDeclaration.getNameAsString() + "<br/>";
        }
        return summarizedMethods;
    }

    private String generateTable(String rows){
        return "<table style=\"width:100%; border: 1px solid black; border-collapse: collapse;\">" +
                "<tr>\n" +
                "    <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Classname</th>\n" +
                "    <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Changed methods</th> \n" +
                "    <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Probability</th>\n" +
                "</tr>\n" +
                rows +
                "</table>";
    }

    private String addRow(String tableItem){
        return "<tr>\n" + tableItem + "</tr>\n";
    }

    private String addTableItem( String item1, String item2, String item3){
        return "<td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">" + item1 + "</td>\n"+
                "<td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">" + item2 + "</td>\n"
                +"<td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">" + item3 + "% </td>\n";
    }

}