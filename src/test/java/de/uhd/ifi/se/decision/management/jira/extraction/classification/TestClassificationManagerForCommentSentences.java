package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import meka.classifiers.multilabel.LC;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import weka.classifiers.meta.FilteredClassifier;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestComment.AoSentenceTestDatabaseUpdater.class) 
public class TestClassificationManagerForCommentSentences  extends TestSetUp {
	

	private EntityManager entityManager;
	private List<Comment> list = new ArrayList<Comment>();
	private ClassificationManagerForCommentSentences classifier;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
		classifier = new ClassificationManagerForCommentSentences();
	}
	
	
	private void fillCommentList() {
		Comment c = new Comment("This is a testcomment");
		Comment c1 = new Comment("This is a testcomment with a larger sentence");
		
		list.add(new Comment("This is a testcomment"));
		list.add(new Comment("This is a testcomment with a larger sentence"));
		list.add(new Comment("This is a testcomment LIKE THE FIRST ONE"));
		list.add(new Comment("This is a testcomment with a larger sentence and without capslock"));
		
		
	}
	
	@Test
	@NonTransactional
	public void testBinaryClassification() throws Exception {
		fillCommentList();
		
		FilteredClassifier binaryClassifier = (FilteredClassifier) weka.core.SerializationHelper.read(System.getProperty("user.dir")+"\\src\\main\\resources\\classifier\\fc.model");
		classifier.getClassifier().setBinaryClassifier(binaryClassifier);
		
		list = classifier.classifySentenceBinary(list);
		assertNotNull(list.get(0).getSentences().get(0).isRelevant());
		assertTrue(list.get(0).getSentences().get(0).isTagged());
	}
	
	@Test
	@NonTransactional
	public void testFineGrainedClassification() throws Exception {
		fillCommentList();
		FilteredClassifier binaryClassifier = (FilteredClassifier) weka.core.SerializationHelper.read(System.getProperty("user.dir")+"\\src\\main\\resources\\classifier\\fc.model");
		classifier.getClassifier().setBinaryClassifier(binaryClassifier);
		
		LC lc = (LC) weka.core.SerializationHelper.read(System.getProperty("user.dir")+"\\src\\main\\resources\\classifier\\br.model");
		classifier.getClassifier().setFineGrainedClassifier(lc);
		
		list = classifier.classifySentenceBinary(list);
//		list = classifier.classifySentenceFineGrained(list);
		//TODO: Fine grained filter does not run in test mode
		assertNotNull(list.get(0).getSentences().get(0).isRelevant());
		assertTrue(list.get(0).getSentences().get(0).isTagged());
	}
}
