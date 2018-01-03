package ut.mocks;

import java.util.ArrayList;
import java.util.Collection;

import com.atlassian.jira.issue.Issue;

/**
 * 
 * @author Tim Kuchenbuch
 * 
 * @description This function is not implemented in the Jira Mock Class
 * TODO Check later for Updated. If possible remove this Class
 *
 */
public class MockIssueManager extends com.atlassian.jira.mock.MockIssueManager {

	public Collection<Long> getIssueIdsForProject(Long id){
		Collection<Long> col = new ArrayList<>();
		// Iterate over the IssueTypes that are added in the TestIssueStartegySup
		for(int i=2;i<=15;i++) {
			Issue issue=this.getIssueObject((long)i);
			if(id==issue.getProjectId()) {
				col.add(issue.getId());
			}
		}
		return col;		
	}
}
