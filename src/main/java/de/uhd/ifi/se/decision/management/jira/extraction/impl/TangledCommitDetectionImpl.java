package de.uhd.ifi.se.decision.management.jira.extraction.impl;


import com.github.javaparser.ast.PackageDeclaration;
import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;

import org.eclipse.jgit.diff.Edit;

import java.util.*;


public class TangledCommitDetectionImpl implements TangledCommitDetection {

    @Override
    public void standardization(DiffImpl diffImpl) {
        if(diffImpl.getChangedFileImpls().size() > 1){
            Collections.sort(diffImpl.getChangedFileImpls());
            float max = diffImpl.getChangedFileImpls().get(0).getPackageDistance();
            for(ChangedFileImpl changedFileImpl : diffImpl.getChangedFileImpls()){
                changedFileImpl.setPercentage((changedFileImpl.getPackageDistance()/max)*100);
            }
        }else {
            diffImpl.getChangedFileImpls().get(0).setPercentage(0);
        }

    }

    @Override
    public void calculatePredication(DiffImpl diffImpl) {
        if(diffImpl.getChangedFileImpls().size() != 1){
            if(this.isAllChangesInOnePackage(diffImpl)){
                this.calculatePathDistances(diffImpl);
            }else{
                this.calculatePackageDistances(diffImpl);
            }
        }else{
            if(this.isAllChangesInMethods(diffImpl) || (diffImpl.getChangedFileImpls().get(0).getEditList().size() == 1)){
                this.calculateMethodDistances(diffImpl);
            }else{
                this.calculateLineDistances(diffImpl);
            }
        }
        this.standardization(diffImpl);
    }

    @Override
    public Boolean isAllChangesInOnePackage(DiffImpl diffs) {
        if(diffs.getChangedFileImpls().size()>1){
            for(int i = 0; i < diffs.getChangedFileImpls().size(); i++){
                for(int j = i+1; j< diffs.getChangedFileImpls().size(); j++){
                    if(!(diffs.getChangedFileImpls().get(i).getCompilationUnit().getPackageDeclaration().isPresent()
                            && diffs.getChangedFileImpls().get(j).getCompilationUnit().getPackageDeclaration().isPresent()
                            && diffs.getChangedFileImpls().get(i).getCompilationUnit().getPackageDeclaration().toString()
                            .equalsIgnoreCase(diffs.getChangedFileImpls().get(j).getCompilationUnit().getPackageDeclaration().toString()))){
                        return false;
                    }
                }
            }
            return true;
        }else{
            return true;
        }

    }

    @Override
    public void calculatePackageDistances(DiffImpl diffs) {
        Integer [][] maxtrix = new Integer[diffs.getChangedFileImpls().size()][diffs.getChangedFileImpls().size()];
        if (diffs.getChangedFileImpls().size() > 1) {
            for (int i = 0; i < diffs.getChangedFileImpls().size(); i++) {
                Vector<String> leftPackageDeclaration = this.parsePackage(diffs.getChangedFileImpls().get(i).getCompilationUnit().getPackageDeclaration());
                for (int j = 0; j < diffs.getChangedFileImpls().size(); j++) {
                        Vector<String> rightPackageDeclaration = this.parsePackage(diffs.getChangedFileImpls().get(j).getCompilationUnit().getPackageDeclaration());
                    if(i!=j) {
                        if (leftPackageDeclaration.size() >= rightPackageDeclaration.size()) {
                            for (int k = 0; k < rightPackageDeclaration.size(); k++) {
                                if (!leftPackageDeclaration.elementAt(k).equals(rightPackageDeclaration.elementAt(k))) {
                                    diffs.getChangedFileImpls().get(i).setPackageDistance(diffs.getChangedFileImpls().get(i).getPackageDistance() + (leftPackageDeclaration.size() - k));
                                    maxtrix[i][j] = leftPackageDeclaration.size() - k;

                                    break;
                                }
                            }
                        } else {
                            for (int k = 0; k < leftPackageDeclaration.size(); k++) {
                                if (!leftPackageDeclaration.elementAt(k).equals(rightPackageDeclaration.elementAt(k))) {
                                    diffs.getChangedFileImpls().get(i).setPackageDistance(diffs.getChangedFileImpls().get(i).getPackageDistance() + (rightPackageDeclaration.size() - k));
                                    maxtrix[i][j] = rightPackageDeclaration.size() - k;
                                    break;
                                }
                            }
                        }
                    }else{
                        maxtrix[i][j] = 0;

                    }
                }
            }

        }
        else{
            diffs.getChangedFileImpls().get(0).setPackageDistance(0);
        }
    }

    @Override
    public Boolean isAllChangesInMethods(DiffImpl diffImpl) {
        int numberOfChangesHasMethod = 0;
        int totalNumberOfChanges = 0;
        for(int i = 0; i < diffImpl.getChangedFileImpls().size(); i++){
            for(int j = 0; j < diffImpl.getChangedFileImpls().get(i).getMethodDeclarations().size(); j++){
                if(diffImpl.getChangedFileImpls().get(i).getEditList().size()>2){
                    for(Edit edit: diffImpl.getChangedFileImpls().get(i).getEditList()){
                        totalNumberOfChanges++;
                        if(edit.getBeginB() >= diffImpl.getChangedFileImpls().get(i).getMethodDeclarations().elementAt(j).getRange().get().begin.line
                                && edit.getEndB() <= diffImpl.getChangedFileImpls().get(i).getMethodDeclarations().elementAt(j).getRange().get().end.line){
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
    public void calculateLineDistances(DiffImpl diffs) {
        for (ChangedFileImpl changedFileImpl : diffs.getChangedFileImpls()) {
            if (changedFileImpl.getEditList().size() > 1) {
                for (int i = 0; i < changedFileImpl.getEditList().size(); i++) {
                    for (int j = i + 1; j < changedFileImpl.getEditList().size(); j++) {
                        int lineDistance = changedFileImpl.getEditList().get(j).getBeginB() - changedFileImpl.getEditList().get(i).getEndB();
                        double distance = ((double) (lineDistance) / changedFileImpl.getCompilationUnit().getRange().get().end.line);
                        changedFileImpl.addLineDistance(distance);
                    }
                }
            } else {
                changedFileImpl.addLineDistance(0.0);
            }
        }
    }

    @Override
    public void calculateMethodDistances(DiffImpl diffImpl) {
        for(ChangedFileImpl changedFileImpl : diffImpl.getChangedFileImpls()){
            if(changedFileImpl.getMethodDeclarations().size() > 1){
                for(int i = 0; i < changedFileImpl.getMethodDeclarations().size(); i++){
                    for(int j = 0; j < changedFileImpl.getMethodDeclarations().size(); j++){
                        int nextBegin = changedFileImpl.getMethodDeclarations().elementAt(j).getRange().get().begin.line;
                        int prevEnd= changedFileImpl.getMethodDeclarations().elementAt(i).getRange().get().end.line;
                        changedFileImpl.addMethodDistance(nextBegin - prevEnd);
                    }
                }
            }else {
                changedFileImpl.addLineDistance(0);
            }
        }
    }

    @Override
    public void calculatePathDistances(DiffImpl diffs) {
        if (diffs.getChangedFileImpls().size() > 1) {
            for (int i = 0; i < diffs.getChangedFileImpls().size(); i++) {
                char[] leftPath = diffs.getChangedFileImpls().get(i).getFile().getPath().toCharArray();
                for (int j = i + 1; j < diffs.getChangedFileImpls().size(); j++) {
                    char[] rightPath = diffs.getChangedFileImpls().get(j).getFile().getPath().toCharArray();
                    if (leftPath.length >= rightPath.length) {
                        for (int k = 0; k < rightPath.length; k++) {
                            if (leftPath[k] != rightPath[k]) {
                                diffs.getChangedFileImpls().get(i).addPathDistance((double) leftPath.length - (k + 1));
                                diffs.getChangedFileImpls().get(j).addPathDistance((double) leftPath.length - (k + 1));
                                break;
                            }
                        }
                    } else {
                        for (int k = 0; k < leftPath.length; k++) {
                            if (leftPath[k] != rightPath[k]) {
                                diffs.getChangedFileImpls().get(i).addPathDistance((double) rightPath.length - (k + 1));
                                diffs.getChangedFileImpls().get(j).addPathDistance((double) rightPath.length - (k + 1));
                                break;
                            }
                        }
                    }

                }
            }
        } else {
            diffs.getChangedFileImpls().get(0).addPathDistance(0.0);
        }
    }

}
