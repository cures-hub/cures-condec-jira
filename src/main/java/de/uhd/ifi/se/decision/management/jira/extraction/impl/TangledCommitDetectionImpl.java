package de.uhd.ifi.se.decision.management.jira.extraction.impl;


import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;
import org.eclipse.jgit.diff.EditList;

import java.util.*;


public class TangledCommitDetectionImpl implements TangledCommitDetection {

    @Override
    public Boolean isInMethod(EditList editList) {
       return false;
    }

    @Override
    public Vector<String> parsePackage(Optional<PackageDeclaration> op) {
        return new Vector<>(Arrays.asList(op.get().toString().split("\\.")));
    }

    @Override
    public Diff getPackageDistances(Diff diffs) {
        if (diffs.getChangedFiles().size() > 1) {
            for (int i = 0; i < diffs.getChangedFiles().size(); i++) {
                Vector<String> leftPackageDeclaration = this.parsePackage(diffs.getChangedFiles().elementAt(i).getCompilationUnit().getPackageDeclaration());
                for (int j = i + 1; j < diffs.getChangedFiles().size(); j++) {
                    Vector<String> rightPackageDeclaration = this.parsePackage(diffs.getChangedFiles().elementAt(j).getCompilationUnit().getPackageDeclaration());
                    if (leftPackageDeclaration.size() >= rightPackageDeclaration.size()) {
                        for (int k = 0; k < rightPackageDeclaration.size(); k++) {
                            if (!leftPackageDeclaration.elementAt(k).equals(rightPackageDeclaration.elementAt(k))) {
                                diffs.getChangedFiles().elementAt(i).addPackageDistance((double) (k + 1) / leftPackageDeclaration.size());
                                diffs.getChangedFiles().elementAt(j).addPackageDistance((double) (k + 1) / leftPackageDeclaration.size());
                                break;
                            }

                        }
                    } else {
                        for (int k = 0; k < leftPackageDeclaration.size(); k++) {
                            if (!leftPackageDeclaration.elementAt(k).equals(rightPackageDeclaration.elementAt(k))) {
                                diffs.getChangedFiles().elementAt(i).addPackageDistance((double) (k + 1) / rightPackageDeclaration.size());
                                diffs.getChangedFiles().elementAt(j).addPackageDistance((double) (k + 1) / rightPackageDeclaration.size());
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            diffs.getChangedFiles().elementAt(0).addPackageDistance(0.0);
        }

        return diffs;
    }


    @Override
    public Diff getLineDistances(Diff diffs) {
        for (ChangedFile changedFile : diffs.getChangedFiles()) {
            Vector<Double> distances = new Vector<>();
            if (changedFile.getEditList().size() > 1) {
                for (int i = 0; i < changedFile.getEditList().size(); i++) {
                    for (int j = i + 1; j < changedFile.getEditList().size(); j++) {
                        int lineDistance = changedFile.getEditList().get(j).getBeginB() - changedFile.getEditList().get(i).getEndB();
                        double distance = ((double) (lineDistance) / changedFile.getCompilationUnit().getRange().get().end.line);
                        distances.add(distance);
                    }
                }
                changedFile.setLineDistance(distances);

            } else {
                distances.add(0.0);
                changedFile.setLineDistance(distances);
            }
        }

        return diffs;
    }

    @Override
    public Diff getPathDistance(Diff diffs) {
        if (diffs.getChangedFiles().size() > 1) {
            for (int i = 0; i < diffs.getChangedFiles().size(); i++) {
                char[] leftPath = diffs.getChangedFiles().elementAt(i).getDiffEntry().getNewPath().toCharArray();
                for (int j = i + 1; j < diffs.getChangedFiles().size(); j++) {
                    char[] rightPath = diffs.getChangedFiles().elementAt(j).getDiffEntry().getNewPath().toCharArray();
                    if (leftPath.length >= rightPath.length) {
                        for (int k = 0; k < rightPath.length; k++) {
                            if (leftPath[k] != rightPath[k]) {
                                diffs.getChangedFiles().elementAt(i).addPathDistance((double) (k + 1) / leftPath.length);
                                diffs.getChangedFiles().elementAt(j).addPathDistance((double) (k + 1) / leftPath.length);
                                break;
                            }
                        }
                    } else {
                        for (int k = 0; k < leftPath.length; k++) {
                            if (leftPath[k] != rightPath[k]) {
                                diffs.getChangedFiles().elementAt(i).addPathDistance((double) (k + 1) / rightPath.length);
                                diffs.getChangedFiles().elementAt(j).addPathDistance((double) (k + 1) / rightPath.length);
                                break;
                            }
                        }
                    }

                }
            }
            return diffs;
        } else {
            diffs.getChangedFiles().elementAt(0).addPathDistance(0.0);
            return diffs;
        }
    }

}
