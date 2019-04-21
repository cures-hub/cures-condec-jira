package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import de.uhd.ifi.se.decision.management.jira.extraction.Diff;

import java.util.ArrayList;

public class DiffImpl implements Diff {
    private ArrayList<ChangedFileImpl> changedFileImpls;

    public DiffImpl(){
        this.changedFileImpls = new ArrayList<>();
    }

    public ArrayList<ChangedFileImpl> getChangedFileImpls() {
        return changedFileImpls;
    }

    public void addChangedFileImpl(ChangedFileImpl changedFileImpl){
        changedFileImpls.add(changedFileImpl);
    }

}
