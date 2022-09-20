/**
 * Enables to export decision knowledge and related knowledge elements, such as requirements, code, and work items.
 *
 * Requires: conDecAPI, conDecViewAPI
 *
 * Is required by: conDecDialog
 */
(function(global) {

	var ConDecExport = function ConDecExport() {
	};

	ConDecExport.prototype.addOnClickEventToExportAsTable = function() {
		console.log("ConDecExport addOnClickEventToExportAsTable");

		var exportMenuItem = document.getElementById("export-as-table-link");

		exportMenuItem.addEventListener("click", function(event) {
			event.preventDefault();
			event.stopPropagation();
			conDecDialog.showExportDialog(JIRA.Issue.getIssueId(), "i");
		});
	};

	/**
	 * external references: condec.dialog
	 */
	ConDecExport.prototype.exportLinkedElements = function(exportFormat) {
		var filterSettings = conDecFiltering.getFilterSettings("export");
		if (exportFormat === "markdown") {
			conDecViewAPI.getMarkdown(filterSettings).then(response => {
				download(response.markdown, "decisionKnowledge", exportFormat);
			});
		} else {
			conDecAPI.getKnowledgeElements(filterSettings, function(elements) {
				elements = elements.sort((a, b) => a["key"].localeCompare(b["key"]));
				download(elements, "decisionKnowledge", exportFormat);
			});
		}
	};

	function download(elements, filename, exportType) {
		var dataString = "";
		switch (exportType) {
			case "document":
				filename += ".doc";
				var htmlString = createHtmlStringForWordDocument(elements);
				dataString = "data:text/html," + encodeURIComponent(htmlString);
				break;
			case "json":
				filename += ".json";
				dataString = "data:text/plain;charset=utf-8," + encodeURIComponent(JSON.stringify(elements));
				break;
			case "markdown":
				filename += ".md";
				dataString = "data:text/plain;charset=utf-8," + encodeURIComponent(elements);
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
		var table = "<table><tr>";
		table += "<th>Type</th><th>Summary</th><th>Description</th><th>Decision Groups</th><th>Status</th>";
		table += "<th>Creator</th><th>Creation Date</th><th>Latest Author</th><th>Latest Update</th>";
		table += "<th>Documentation Origin</th><th>Key</th></tr>";
		for (const element of elements) {
			table += "<tr color='#FF0000'>";
			table += `<td> ${element["type"]} </td>`;
			table += `<td> ${element["summary"]} </td>`;
			table += `<td> ${element["description"]} </td>`;
			table += `<td> ${element["groups"]} </td>`;
			var status = element["status"] !== "undefined" ? element["status"] : "";
			table += `<td> ${status} </td>`;
			table += `<td> ${element["creator"]} </td>`;
			table += "<td>" + new Date(element["creationDate"]) + "</td>";
			table += `<td> ${element["latestAuthor"]} </td>`;
			table += "<td>" + new Date(element["latestUpdatingDate"]) + "</td>";
			table += `<td> ${element["origin"]} </td>`;
			table += `<td><a href='${element["url"]}'> ${element["key"]} </a></td>`;
			table += "</tr>";
		}
		table += "</table>";

		var styleString = "table{font-family:arial,sans-serif;border-collapse:collapse;width:100%}td,th{border:1px solid #ddd;text-align:left;padding:8px}tr:nth-child(even){background-color:#ddd}";
		var htmlString = $("<html>").html("<head><style>" + styleString + "</style></head><body>" + table + "</body>")
			.html();
		return htmlString;
	}

	global.conDecExport = new ConDecExport();
})(window);