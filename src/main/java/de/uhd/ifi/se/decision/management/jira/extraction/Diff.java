package de.uhd.ifi.se.decision.management.jira.extraction;

import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFileImpl;

import java.util.ArrayList;

public interface Diff {
    ArrayList<ChangedFileImpl> getChangedFileImpls();
    void addChangedFileImpl(ChangedFileImpl changedFileImpl);
}
