package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;

/**
 * Extracts decision knowledge elements from code comments of Java classes.
 */
public class JavaCodeCommentParser implements CodeCommentParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(CodeCommentParser.class);

	@Override
	public List<CodeComment> getComments(File inspectedFile) {
		ParseResult<CompilationUnit> parseResult = parseJavaFile(inspectedFile);
		CompilationUnit compilationUnit = parseResult.getResult().get();
		if (compilationUnit == null) {
			return null;
		} else {
			return getComments(compilationUnit);
		}
	}

	public static ParseResult<CompilationUnit> parseJavaFile(File inspectedFile) {
		ParseResult<CompilationUnit> parseResult = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(inspectedFile.toString());
			JavaParser javaParser = new JavaParser();
			parseResult = javaParser.parse(fileInputStream);
			fileInputStream.close();
		} catch (ParseProblemException | IOException | NullPointerException e) {
			LOGGER.error(e.getMessage());
		}
		return parseResult;
	}

	private List<CodeComment> getComments(CompilationUnit compilationUnit) {
		List<Comment> comments = compilationUnit.getAllComments();
		List<CodeComment> positionedComments = comments.stream()
				.filter(comment -> (comment.getBegin().isPresent() && comment.getEnd().isPresent())).map(comment -> {
					Position begin = comment.getBegin().get();
					Position end = comment.getEnd().get();
					return new CodeComment(comment.getContent(), begin.column, begin.line, end.column, end.line);
				}).collect(Collectors.toList());
		return positionedComments;
	}
}
