package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.util.Vector;

public class Diff {
    private Vector<ChangedFile> changedFiles;

    public Diff(){
        this.changedFiles = new Vector<>();
    }

    public void setChangedFiles(Vector<ChangedFile> changedFiles) {
        this.changedFiles = changedFiles;
    }

    public Vector<ChangedFile> getChangedFiles() {
        return changedFiles;
    }

    public void addChangedFiles(ChangedFile changedFile){
        changedFiles.add(changedFile);
    }
}
