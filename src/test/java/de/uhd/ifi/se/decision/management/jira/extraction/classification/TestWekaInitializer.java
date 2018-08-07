package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.persistence.ActiveObjectStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.activeobjectstrategy.ActiveObjectStrategyTestSetUp;
import net.java.ao.EntityManager;

public class TestWekaInitializer {



	@Ignore
	@Test
	public void testClassify() {

		Comment comment = new Comment("This is a testsentence");
		List<Comment> commentList = new ArrayList<Comment>();
		commentList.add(comment);
		System.out.println("hier");
		try {
			assertNotNull(WekaInitializer.classifySentencesBinary(commentList));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
