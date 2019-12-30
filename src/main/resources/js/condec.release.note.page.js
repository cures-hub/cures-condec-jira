/*
 This view provides a table with release notes.
 
 Requires
 * conDecAPI
 * conDecObservable
 * conDecContextMenu
 * conDecTreant
 * conDecTreeViewer

 Is referenced in HTML by
 * decisionKnowledgePage.vm
 */
(function(global) {
	/* private vars */
	var conDecObservable = null;
	var conDecAPI = null;
	var conDecDialog = null;


	var ConDecReleaseNotePage = function ConDecReleaseNotePage() {
	};

	ConDecReleaseNotePage.prototype.init = function(_conDecAPI, _conDecObservable, _conDecDialog) {
		console.log("conDecReleaseNotePage init");

		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)
				&& isConDecDialogType(_conDecDialog) ) {

			conDecAPI = _conDecAPI;
			conDecObservable = _conDecObservable;
			conDecDialog = _conDecDialog;

			// subscribe to event on table
			document.getElementById("release-notes-table").addEventListener("updateReleaseNoteTable",function(event){
				this.getReleaseNotes();
			}.bind(this));
			return true;
		}
		return false;
	};

	ConDecReleaseNotePage.prototype.getReleaseNotes = function() {
		var query=$("#searchReleaseNotesInput").val();
		emptyTable();
		conDecAPI.getAllReleaseNotes(query).then(function(response){
			if(response && response.length){
				fillTable(response)
			}else{
				throwAlert("Info","No Release Notes found!")
			}
		}).catch(function (err) {
			throwAlert("Error","Could not load Release Notes",err.toString())
		}).finally(function () {
			showLoadingIndicator(false);
		})

	};


	ConDecReleaseNotePage.prototype.addReleaseNote = function() {
		conDecDialog.showCreateReleaseNoteDialog();
	};

	ConDecReleaseNotePage.prototype.displayReleaseNote = function(id) {
		conDecDialog.showEditReleaseNoteDialog(id);
	};

	function fillTable(response) {
		var tBody=$("#release-notes-table-body");
		if(response && response.length){
		response.map(function(releaseNote){
			showLoadingIndicator(false);
			tBody.append(createTableRow(releaseNote));
		})
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
		showLoadingIndicator(true);
		var tBody=$("#release-notes-table-body");
		tBody.empty();
	}
	function showLoadingIndicator(display){
		if(display){
			$("#release-note-table-loader").css("display","block")
		}else{
			$("#release-note-table-loader").css("display","none");
		}
	}
	function createTableRow(releaseNote){
		var tableRow="<tr>";
		tableRow +="<td>"+releaseNote.id+"</td>";
		tableRow +="<td>"+releaseNote.title+"</td>";
		tableRow +="<td>"+releaseNote.startDate+"</td>";
		tableRow +="<td>"+releaseNote.endDate+"</td>";
		tableRow +="<td><button class='aui-button' id='openEditReleaseNoteDialogButton_"+releaseNote.id+"' onclick='displayReleaseNote("+releaseNote.id+")'>Display</button></td>";
		tableRow +="</tr>";
		return tableRow
	}

	/*
	 * Init Helpers
	 */
	function isConDecAPIType(conDecAPI) {
		if (!(conDecAPI !== undefined && conDecAPI.getDecisionKnowledgeElement !== undefined && typeof conDecAPI.getDecisionKnowledgeElement === 'function')) {
			console.warn("ConDecKnowledgePage: invalid ConDecAPI object received.");
			return false;
		}
		return true;
	}

	function isConDecObservableType(conDecObservable) {
		if (!(conDecObservable !== undefined && conDecObservable.notify !== undefined && typeof conDecObservable.notify === 'function')) {
			console.warn("ConDecKnowledgePage: invalid ConDecObservable object received.");
			return false;
		}
		return true;
	}

	function isConDecDialogType(conDecDialog) {
		if (!(conDecDialog !== undefined && conDecDialog.showCreateDialog !== undefined && typeof conDecDialog.showCreateDialog === 'function')) {
			console.warn("ConDecKnowledgePage: invalid conDecDialog object received.");
			return false;
		}
		return true;
	}


	// export ConDecKnowledgePage
	global.conDecReleaseNotePage = new ConDecReleaseNotePage();
})(window);