package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;


/**
 * Extracts decision knowledge elements from code comments of Java classes.
 */
public class JavaCodeParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(CodeCommentParser.class);

	public static ParseResult<CompilationUnit> parseJavaFile(String inspectedFileContent) {
		ParseResult<CompilationUnit> parseResult = null;
		try {
			JavaParser javaParser = new JavaParser();
			parseResult = javaParser.parse(inspectedFileContent);
		} catch (ParseProblemException | NullPointerException e) {
			LOGGER.error(e.getMessage());
		}
		return parseResult;
	}
}