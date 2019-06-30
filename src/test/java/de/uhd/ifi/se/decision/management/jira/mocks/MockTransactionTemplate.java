package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class MockTransactionTemplate implements TransactionTemplate {

	@Override
	public <T> T execute(TransactionCallback<T> transactionCallback) {
		return transactionCallback.doInTransaction();
	}
}
