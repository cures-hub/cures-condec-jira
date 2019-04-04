package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;
import org.eclipse.jgit.diff.EditList;

import java.util.*;
import java.io.File;
import java.io.IOException;


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
    public Vector<DiffObject> parseMethod(Vector<DiffObject> diffObjects) {
        for (DiffObject diffObject : diffObjects) {
            Object ob = new MethodVisitor().visit(diffObject.getCompilationUnit(), diffObject);
            diffObject = (DiffObject) ob;
        }
        return diffObjects;
    }

    class MethodVisitor extends GenericVisitorAdapter {
        @Override
        public DiffObject visit(MethodDeclaration m, Object arg) {
            System.out.print("call get visit");
            System.out.println("From [" + m.getBegin() + "," + "] to [" + m.getEnd() + ","
                    + "] is method:" + m.getDeclarationAsString() + '\n');
            System.out.println(m);
            DiffObject diffi = (DiffObject) arg;
            diffi.setMethodDeclarations(m);
            return diffi;
        }
    }

    @Override
    public Vector<DiffObject> getPackageDistances(Vector<DiffObject> diffObjects) {
        if (diffObjects.size() > 1) {
            for (int i = 0; i < diffObjects.size(); i++) {
                Vector<String> leftPackageDeclaration = this.parsePackage(diffObjects.elementAt(i).getCompilationUnit().getPackageDeclaration());
                for (int j = i + 1; j < diffObjects.size(); j++) {
                    Vector<String> rightPackageDeclaration = this.parsePackage(diffObjects.elementAt(j).getCompilationUnit().getPackageDeclaration());
                    if (leftPackageDeclaration.size() >= rightPackageDeclaration.size()) {
                        for (int k = 0; k < rightPackageDeclaration.size(); k++) {
                            if (!leftPackageDeclaration.elementAt(k).equals(rightPackageDeclaration.elementAt(k))) {
                                diffObjects.elementAt(i).addPackageDistance((double) (k + 1) / leftPackageDeclaration.size());
                                diffObjects.elementAt(j).addPackageDistance((double) (k + 1) / leftPackageDeclaration.size());
                                break;
                            }

                        }
                    } else {
                        for (int k = 0; k < leftPackageDeclaration.size(); k++) {
                            if (!leftPackageDeclaration.elementAt(k).equals(rightPackageDeclaration.elementAt(k))) {
                                diffObjects.elementAt(i).addPackageDistance((double) (k + 1) / rightPackageDeclaration.size());
                                diffObjects.elementAt(j).addPackageDistance((double) (k + 1) / rightPackageDeclaration.size());
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            diffObjects.elementAt(0).addPackageDistance(0.0);
        }

        return diffObjects;
    }


    @Override
    public Vector<DiffObject> getLineDistances(Vector<DiffObject> diffObjects) {
        for (DiffObject diffObject : diffObjects) {
            Vector<Double> distances = new Vector<>();
            if (diffObject.getEditList().size() > 1) {
                for (int i = 0; i < diffObject.getEditList().size(); i++) {
                    for (int j = i + 1; j < diffObject.getEditList().size(); j++) {
                        int lineDistance = diffObject.getEditList().get(j).getBeginB() - diffObject.getEditList().get(i).getEndB();
                        double distance = ((double) (lineDistance) / diffObject.getCompilationUnit().getRange().get().end.line);
                        distances.add(distance);
                    }
                }
                diffObject.setLineDistance(distances);

            } else {
                distances.add(0.0);
                diffObject.setLineDistance(distances);
            }
        }

        return diffObjects;
    }

    @Override
    public Vector<DiffObject> getPathDistance(Vector<DiffObject> diffObjects) {
        if (diffObjects.size() > 1) {
            for (int i = 0; i < diffObjects.size(); i++) {
                char[] leftPath = diffObjects.elementAt(i).getDiffEntry().getNewPath().toCharArray();
                for (int j = i + 1; j < diffObjects.size(); j++) {
                    char[] rightPath = diffObjects.elementAt(j).getDiffEntry().getNewPath().toCharArray();
                    if (leftPath.length >= rightPath.length) {
                        for (int k = 0; k < rightPath.length; k++) {
                            if (leftPath[k] != rightPath[k]) {
                                diffObjects.elementAt(i).addPathDistance((double) (k + 1) / leftPath.length);
                                diffObjects.elementAt(j).addPathDistance((double) (k + 1) / leftPath.length);
                                break;
                            }
                        }
                    } else {
                        for (int k = 0; k < leftPath.length; k++) {
                            if (leftPath[k] != rightPath[k]) {
                                diffObjects.elementAt(i).addPathDistance((double) (k + 1) / rightPath.length);
                                diffObjects.elementAt(j).addPathDistance((double) (k + 1) / rightPath.length);
                                break;
                            }
                        }
                    }

                }
            }
            return diffObjects;
        } else {
            diffObjects.elementAt(0).addPathDistance(0.0);
            return diffObjects;
        }
    }


    /*

        @Override
    public Optional<PackageDeclaration> getPackageDeclaration(File src) {
        // can use cu directly from diffObject
        CompilationUnit cu = new CompilationUnit();
        try {
            cu = JavaParser.parse(src);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cu.getPackageDeclaration();
    }


        @Override
        public Vector<DiffObject> setAllPackageName(Vector<DiffObject> diffObjects) {
            for(DiffObject diffObject : diffObjects){
                diffObject.setPackageDeclaration(this.getPackageDeclaration(diffObject.getFile()));
            }
            return diffObjects;
        }



            @Override
            public Vector<Float> getPackageDistanceList(Vector<Vector<String>> allDeclarations) {
                Vector<Float> distances = new Vector<>();
                for (int i = 0; i < allDeclarations.size(); i++) {
                    for (int j = i + 1; j < allDeclarations.size(); j++) {
                        if (allDeclarations.elementAt(i).size() >= allDeclarations.elementAt(j).size()) {
                            for (int k = 0; k < allDeclarations.elementAt(j).size(); k++) {
                                if (!(allDeclarations.elementAt(i).elementAt(k).equals(allDeclarations.elementAt(j).elementAt(k)))) {
                                    distances.add((float) (k + 1) / allDeclarations.elementAt(i).size());
                                    break;
                                }
                            }
                        } else {
                            for (int k = 0; k < allDeclarations.elementAt(i).size(); k++) {
                                if (!(allDeclarations.elementAt(i).elementAt(k).equals(allDeclarations.elementAt(j).elementAt(k)))) {
                                    distances.add((float) (k + 1) / allDeclarations.elementAt(j).size());
                                    break;
                                }
                            }
                        }
                    }
                }
                return distances;
            }

    @Override
    public float getPrognosis() {
        return 0;
    }
      */

}
