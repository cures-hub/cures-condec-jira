AJS.$.post(AJS.contextPath() + "/rest/admin/1.0/config.json?projectKey=" + thisToggle.dataset.projectkey + "&isActivated=" + thisToggle.checked, {})
	.done(function () {
		console.log('success');
	})
	.fail(function () {
		thisToggle.checked = !thisToggle.checked;
		console.error('display an error message');
	})
	.always(function () {
		thisToggle.busy = false;
	});