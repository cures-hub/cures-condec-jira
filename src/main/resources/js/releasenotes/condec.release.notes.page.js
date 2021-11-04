/*
 This view provides a table with release notes.
 
 Requires
 * conDecAPI
 * conDecReleaseNotesAPI
 * conDecReleaseNotesDialog

 Is referenced in HTML by
 * releaseNotesPage.vm
 */
(function(global) {

	var ConDecReleaseNotePage = function ConDecReleaseNotePage() {
	};

	ConDecReleaseNotePage.prototype.initView = function () {
		console.log("conDecReleaseNotePage initView");
		
		// subscribe to event on table
		document.getElementById("release-notes-table").addEventListener("updateReleaseNoteTable", function(event) {
			this.getReleaseNotes();
		}.bind(this));
		
		document.getElementById("create-release-notes-button").addEventListener("click", function(event) {
			event.preventDefault();
			conDecReleaseNotesDialog.showCreateReleaseNoteDialog();
		});
		
		document.getElementById("search-release-notes-button").addEventListener("click", function(event) {
			event.preventDefault();
			conDecReleaseNotePage.getReleaseNotes();
		});
	};

	ConDecReleaseNotePage.prototype.getReleaseNotes = function () {
		var searchTerm = $("#search-release-notes-input").val();
		emptyTable();
		conDecReleaseNotesAPI.getReleaseNotes(searchTerm).then(function(response){
			if(response && response.length){
				fillTable(response)
			} else{
				throwAlert("Info","No Release Notes found!")
			}
		}).catch(function (err) {
			throwAlert("Error","Could not load Release Notes", err.toString())
		});
	};

	ConDecReleaseNotePage.prototype.displayReleaseNote = function (id) {
		conDecReleaseNotesDialog.showEditReleaseNoteDialog(id);
	};

	function fillTable(response) {
		var tBody=$("#release-notes-table-body");
		if (response && response.length) {
			response.map(function(releaseNote) {
				tBody.append(createTableRow(releaseNote));
			});
		}
	}
	
	function throwAlert(type,title, message) {
		AJS.flag({
			type: type,
			close: "auto",
			title: title,
			body: message
		});
	}
	
	function emptyTable(){
		var tBody=$("#release-notes-table-body");
		tBody.empty();
	}
	
	function createTableRow(releaseNote){
		var tableRow= "<tr>";
		tableRow += "<td>" + releaseNote.id + "</td>";
		tableRow += "<td>" + releaseNote.title + "</td>";
		tableRow += "<td>" + releaseNote.startDate + "</td>";
		tableRow += "<td>" + releaseNote.endDate + "</td>";
		tableRow += "<td><button class='aui-button' id='openEditReleaseNoteDialogButton_" + releaseNote.id 
					+ "' onclick='conDecReleaseNotePage.displayReleaseNote(" + releaseNote.id + ")'>Display</button></td>";
		tableRow += "</tr>";
		return tableRow;
	}

	global.conDecReleaseNotePage = new ConDecReleaseNotePage();
})(window);