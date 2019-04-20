package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.util.ArrayList;

public class Diff{
    private ArrayList<ChangedFile> changedFiles;

    public Diff(){
        this.changedFiles = new ArrayList<>();
    }

    public ArrayList<ChangedFile> getChangedFiles() {
        return changedFiles;
    }

    public void addChangedFiles(ChangedFile changedFile){
        changedFiles.add(changedFile);
    }

}
