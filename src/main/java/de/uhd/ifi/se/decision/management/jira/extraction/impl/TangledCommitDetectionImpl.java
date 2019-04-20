package de.uhd.ifi.se.decision.management.jira.extraction.impl;


import com.github.javaparser.ast.PackageDeclaration;
import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;
import org.eclipse.jgit.diff.Edit;

import java.util.*;


public class TangledCommitDetectionImpl implements TangledCommitDetection {

    @Override
    public void standardization(Diff diff) {
        Collections.sort(diff.getChangedFiles());
        float max = diff.getChangedFiles().get(diff.getChangedFiles().size()-1).getPackageDistance();
        for(ChangedFile changedFile: diff.getChangedFiles()){
            changedFile.setPercentage((changedFile.getPackageDistance()/max)*100);
        }
    }

    @Override
    public void calculatePredication(Diff diff) {
        if(diff.getChangedFiles().size() != 1){
            if(this.isAllChangesInOnePackage(diff)){
                this.calculatePathDistances(diff);
            }else{
                this.calculatePackageDistancesNew(diff);
            }
        }else{
            if(this.isAllChangesInMethods(diff) || (diff.getChangedFiles().get(0).getEditList().size() == 1)){
                this.calculateMethodDistances(diff);
            }else{
                this.calculateLineDistances(diff);
            }
        }
        this.standardization(diff);
    }

    @Override
    public Boolean isAllChangesInOnePackage(Diff diffs) {
            for(int i = 0; i < diffs.getChangedFiles().size(); i++){
                for( int j = i+1; j< diffs.getChangedFiles().size(); j++){
                    if(!(diffs.getChangedFiles().get(i).getCompilationUnit().getPackageDeclaration().isPresent()
                            && diffs.getChangedFiles().get(j).getCompilationUnit().getPackageDeclaration().isPresent()
                            && diffs.getChangedFiles().get(i).getCompilationUnit().getPackageDeclaration().toString()
                                    .equalsIgnoreCase(diffs.getChangedFiles().get(j).getCompilationUnit().getPackageDeclaration().toString()))){
                        return false;
                    }
                }
            }
                return true;
    }

    @Override
    public void calculatePackageDistancesNew(Diff diffs) {
        if (diffs.getChangedFiles().size() > 1) {
            for (int i = 0; i < diffs.getChangedFiles().size(); i++) {
                Vector<String> leftPackageDeclaration = this.parsePackage(diffs.getChangedFiles().get(i).getCompilationUnit().getPackageDeclaration());
                for (int j = 0; j < diffs.getChangedFiles().size(); j++) {
                        Vector<String> rightPackageDeclaration = this.parsePackage(diffs.getChangedFiles().get(j).getCompilationUnit().getPackageDeclaration());
                    if(i!=j) {
                        if (leftPackageDeclaration.size() >= rightPackageDeclaration.size()) {
                            for (int k = 0; k < rightPackageDeclaration.size(); k++) {
                                if (!leftPackageDeclaration.elementAt(k).equals(rightPackageDeclaration.elementAt(k))) {
                                    diffs.getChangedFiles().get(i).setPackageDistance(diffs.getChangedFiles().get(i).getPackageDistance() + (leftPackageDeclaration.size() - k));
                                    break;
                                }
                            }
                        } else {
                            for (int k = 0; k < leftPackageDeclaration.size(); k++) {
                                if (!leftPackageDeclaration.elementAt(k).equals(rightPackageDeclaration.elementAt(k))) {
                                    diffs.getChangedFiles().get(i).setPackageDistance(diffs.getChangedFiles().get(i).getPackageDistance() + (rightPackageDeclaration.size() - k));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Boolean isAllChangesInMethods(Diff diff) {
        int numberOfChangesHasMethod = 0;
        int totalNumberOfChanges = 0;
        for(int i = 0; i < diff.getChangedFiles().size(); i++){
            for(int j = 0; j < diff.getChangedFiles().get(i).getMethodDeclarations().size(); j++){
                if(diff.getChangedFiles().get(i).getEditList().size()>2){
                    for(Edit edit: diff.getChangedFiles().get(i).getEditList()){
                        totalNumberOfChanges++;
                        if(edit.getBeginB() >= diff.getChangedFiles().get(i).getMethodDeclarations().elementAt(j).getRange().get().begin.line
                                && edit.getEndB() <= diff.getChangedFiles().get(i).getMethodDeclarations().elementAt(j).getRange().get().end.line){
                            numberOfChangesHasMethod++;
                        }
                    }
                }else{
                    return false;
                }
            }
        }
        if(numberOfChangesHasMethod!=totalNumberOfChanges){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public Vector<String> parsePackage(Optional<PackageDeclaration> op) {
        return new Vector<>(Arrays.asList(op.get().toString().split("\\.")));
    }

    @Override
    public void calculateLineDistances(Diff diffs) {
        for (ChangedFile changedFile : diffs.getChangedFiles()) {
            if (changedFile.getEditList().size() > 1) {
                for (int i = 0; i < changedFile.getEditList().size(); i++) {
                    for (int j = i + 1; j < changedFile.getEditList().size(); j++) {
                        int lineDistance = changedFile.getEditList().get(j).getBeginB() - changedFile.getEditList().get(i).getEndB();
                        double distance = ((double) (lineDistance) / changedFile.getCompilationUnit().getRange().get().end.line);
                        changedFile.addLineDistance(distance);
                    }
                }
            } else {
                changedFile.addLineDistance(0.0);
            }
        }
    }

    @Override
    public void calculateMethodDistances(Diff diff) {
        for(ChangedFile changedFile: diff.getChangedFiles()){
            if(changedFile.getMethodDeclarations().size() > 1){
                for(int i= 0; i < changedFile.getMethodDeclarations().size(); i++){
                    for(int j = 0; j < changedFile.getMethodDeclarations().size(); j++){
                        int nextBegin = changedFile.getMethodDeclarations().elementAt(j).getRange().get().begin.line;
                        int prevEnd= changedFile.getMethodDeclarations().elementAt(i).getRange().get().end.line;
                        changedFile.addMethodDistance(nextBegin - prevEnd);
                    }
                }
            }else {
                changedFile.addLineDistance(0);
            }
        }
    }

    @Override
    public void calculatePathDistances(Diff diffs) {
        if (diffs.getChangedFiles().size() > 1) {
            for (int i = 0; i < diffs.getChangedFiles().size(); i++) {
                char[] leftPath = diffs.getChangedFiles().get(i).getDiffEntry().getNewPath().toCharArray();
                for (int j = i + 1; j < diffs.getChangedFiles().size(); j++) {
                    char[] rightPath = diffs.getChangedFiles().get(j).getDiffEntry().getNewPath().toCharArray();
                    if (leftPath.length >= rightPath.length) {
                        for (int k = 0; k < rightPath.length; k++) {
                            if (leftPath[k] != rightPath[k]) {
                                diffs.getChangedFiles().get(i).addPathDistance((double) leftPath.length - (k + 1));
                                diffs.getChangedFiles().get(j).addPathDistance((double) leftPath.length - (k + 1));
                                break;
                            }
                        }
                    } else {
                        for (int k = 0; k < leftPath.length; k++) {
                            if (leftPath[k] != rightPath[k]) {
                                diffs.getChangedFiles().get(i).addPathDistance((double) rightPath.length - (k + 1));
                                diffs.getChangedFiles().get(j).addPathDistance((double) rightPath.length - (k + 1));
                                break;
                            }
                        }
                    }

                }
            }
        } else {
            diffs.getChangedFiles().get(0).addPathDistance(0.0);
        }
    }

}
