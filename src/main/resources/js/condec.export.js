/**
 * Enables to export decision knowledge and related knowledge elements, such as requirements, code, and work items.
 *
 * Requires: conDecAPI
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
	ConDecExport.prototype.exportLinkedElements = function exportLinkedElements(exportFormat, id, documentationLocation) {
		conDecAPI.getKnowledgeElement(id, documentationLocation, function(element) {
			var filterSettings = conDecFiltering.getFilterSettings("export");
			conDecAPI.getKnowledgeElements(filterSettings, function(elements) {
				if (elements && elements.length > 0 && elements[0] !== null) {
					download(elements, "decisionKnowledge", exportFormat);
				}
			});
		});
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
		elements.map(function(element) {
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

	global.conDecExport = new ConDecExport();
})(window);