/*
 This module provides all functionality to export decision knowledge.

 Requires

 Is referenced in HTML by
 * exportDialog.vm
 */
(function (global) {

	var ConDecExport = function ConDecExport() {
	};
	/**
	 * Only Public function
	 * @param elementKey
	 * @param exportFormat
	 * @param exportType
	 */
	ConDecExport.prototype.getSelectedRadioBoxForExport = function getSelectedRadioBoxForExport(exportType, exportFormat, elementKey) {
		var expFormat = "";
		if (exportFormat === "exportAsDocument") {
			expFormat = "document";
		}
		if (exportFormat === "exportAsJson") {
			expFormat = "json";
		}
		if (exportType === "exportLinked") {
			exportLinkedElements(expFormat, elementKey);
		}
		if (exportType === "exportLinkedAndQuery") {
			exportAllMatchedAndLinkedElements(expFormat, elementKey);
		}
		// close dialog
		AJS.dialog2('#export-dialog').hide();
	};


	function getURLsSearch() {
		// get jql from url
		var search = global.location.search.toString();
		search = search.toString().replace("&", "§");
		// if search query does not exist check
		return search;
	}

	/**
	 * returns jql if empty or nonexistent create it returning jql for one issue or elementKey
	 *
	 * @returns {string}
	 */
	function getQueryFromUrl(bAllIssues, elementKey) {

		var userInputJql = getURLsSearch();
		var baseUrl = AJS.params.baseURL;
		var sPathName = document.location.href;
		var sPathWithoutBaseUrl = sPathName.split(baseUrl)[1];

		// check if jql is empty or non existent
		var myJql = "";
		if (userInputJql && userInputJql.indexOf("?jql=") > -1 && userInputJql.split("?jql=")[1]) {
			myJql = userInputJql;
		} else if (userInputJql && userInputJql.indexOf("?filter=") > -1 && userInputJql.split("?filter=")[1]) {
			myJql = userInputJql;
		} else if (sPathWithoutBaseUrl && sPathWithoutBaseUrl.indexOf("/browse/") > -1) {
			// user on url of a single issue
			if (bAllIssues) {
				myJql = "?filter=allissues";
			} else {
				var issueKey = sPathWithoutBaseUrl.split("/browse/")[1];
				if (issueKey.indexOf("?jql=")) {
					issueKey = issueKey.split("?jql=")[0];
				}
				if (issueKey.indexOf("?filter=")) {
					issueKey = issueKey.split("?filter=")[0];
				}
				myJql = "?jql=issue=" + issueKey;
			}
		} else {
			//it has to be at the Decision knowledge site
			if (elementKey) {
				myJql = "?jql=issue=" + elementKey;
			}
		}
		return myJql;
	}

	function exportLinkedElements(exportType, elementKey) {
		var jql = getQueryFromUrl(true, elementKey);
		var jiraIssueKey = conDecAPI.getIssueKey();
		//handle Exception when no issueKey could be defined
		if(!jiraIssueKey){
			jiraIssueKey=elementKey;
		}
		conDecAPI.getLinkedElementsByQuery(jql, jiraIssueKey, "i", function (elements) {
			if (elements && elements.length > 0 && elements[0] !== null) {
				download(elements, "decisionKnowledgeGraph", exportType);
			}
		});
	}

	function exportAllMatchedAndLinkedElements(exportType, elementKey) {
		var jql = getQueryFromUrl(false, elementKey);
		conDecAPI.getAllElementsByQueryAndLinked(jql, function (elements) {
			if (elements && elements.length > 0 && elements[0] !== null) {
				download(elements, "decisionKnowledgeGraphWithLinked", exportType, true);
			}
		});
	}

	function download(elements, filename, exportType, multipleArrays) {
		var dataString = "";
		switch (exportType) {
			case "document":
				filename += ".doc";
				var htmlString = "";
				if (multipleArrays) {
					elements.map(function (aElement) {
						htmlString += createHtmlStringForWordDocument(aElement) + "<hr>";
					});
				} else {
					htmlString = createHtmlStringForWordDocument(elements);
				}
				dataString = "data:text/html," + encodeURIComponent(htmlString);
				break;
			case "json":
				dataString = "data:text/plain;charset=utf-8," + encodeURIComponent(JSON.stringify(elements));
				filename += ".json";
				break;
		}

		var link = document.createElement('a');
		link.style.display = 'none';
		link.setAttribute('href', dataString);
		link.setAttribute('download', filename);
		document.body.appendChild(link);
		link.click();
		document.body.removeChild(link);
	}

	function createHtmlStringForWordDocument(elements) {
		var table = "<table><tr><th>Key</th><th>Summary</th><th>Description</th><th>Type</th></tr>";
		elements.map(function (element) {
			var summary = element["summary"] === undefined ? "" : element["summary"];
			var description = element["description"] === undefined ? "" : element["description"];
			var type = element["type"] === undefined ? "" : element["type"];

			table += "<tr>";
			table += "<td><a href='" + element["url"] + "'>" + element["key"] + "</a></td>";
			table += "<td>" + summary + "</td>";
			table += "<td>" + description + "</td>";
			table += "<td>" + type + "</td>";
			table += "</tr>";
		});
		table += "</table>";

		var styleString = "table{font-family:arial,sans-serif;border-collapse:collapse;width:100%}td,th{border:1px solid #ddd;text-align:left;padding:8px}tr:nth-child(even){background-color:#ddd}";
		var htmlString = $("<html>").html("<head><style>" + styleString + "</style></head><body>" + table + "</body>")
			.html();
		return htmlString;
	}


	// export ConDecExport
	global.conDecExport = new ConDecExport();
})(window);