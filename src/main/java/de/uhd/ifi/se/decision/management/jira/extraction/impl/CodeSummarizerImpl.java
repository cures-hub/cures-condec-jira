package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.io.File;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.body.MethodDeclaration;

import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;

public class CodeSummarizerImpl implements CodeSummarizer {

    private GitClient gitClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeSummarizerImpl.class);
    private int minProbability;
    public CodeSummarizerImpl(String projectKey) {
        this.gitClient = new GitClientImpl(projectKey);
    }

    @Override
    public String createSummary(String jiraIssueKey, int probability) {
        minProbability = probability;
        if (jiraIssueKey == null || jiraIssueKey.equalsIgnoreCase("")) {
            return "";
        }
        Map<DiffEntry, EditList> diff = gitClient.getDiff(jiraIssueKey);
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
        return generateSummary(allDiffs);
    }

    private String generateSummary(DiffImpl diffImpl){
        String rows ="";
        for(ChangedFileImpl changedFileImpl : diffImpl.getChangedFileImpls()){
            if(changedFileImpl.getPercentage() >= minProbability){
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