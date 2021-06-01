package de.uhd.ifi.se.decision.management.jira.git.parser;

import java.util.LinkedHashSet;
import java.util.Set;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Retrieves method declarations from a Java class. Used to parse changed files
 * in commits.
 */
public class MethodVisitor extends VoidVisitorAdapter<Void> {

	private Set<MethodDeclaration> methodDeclarations = new LinkedHashSet<MethodDeclaration>();

	public MethodVisitor() {
		this.methodDeclarations = new LinkedHashSet<MethodDeclaration>();
	}

	/**
	 * Identifies method names, their beginning and end, and their annotations
	 * 
	 * @param methodDeclaration
	 *            a method declaration
	 * @param arg
	 *            void
	 */
	@Override
	public void visit(MethodDeclaration methodDeclaration, Void arg) {
		methodDeclarations.add(methodDeclaration);
		super.visit(methodDeclaration, arg);
	}

	public Set<MethodDeclaration> getMethodDeclarations() {
		return methodDeclarations;
	}
}