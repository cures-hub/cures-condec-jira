package de.uhd.ifi.se.decision.documentation.jira.mocks;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class MockTransactionTemplate implements TransactionTemplate{
	@Override
	@SuppressWarnings("unchecked")
	public <T> T execute(TransactionCallback<T> arg0) {
		return (T) "true";
	}
}
