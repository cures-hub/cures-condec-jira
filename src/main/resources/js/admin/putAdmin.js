var xhr = new XMLHttpRequest();
xhr.open("PUT", AJS.contextPath() + "/rest/admin/1.0/config.json?projectKey=" + thisToggle.dataset.projectkey + "&isIssueStrategy=" + thisToggle.checked, true);
xhr.setRequestHeader("Content-type", "application/json", "charset=utf-8");
xhr.setRequestHeader("Accept", "application/json");
xhr.responseType = "json";
xhr.onload = function() {
	var status = xhr.status;
	if (status == 200) {
		console.log('success');
	} else {
		thisToggle.checked = !thisToggle.checked;
		console.error('display an error message');
	}
	thisToggle.busy = false;
};
xhr.send();