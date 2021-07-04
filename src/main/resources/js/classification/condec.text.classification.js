/**
 * This module implements the link suggestion and duplicate detection.
 */
(function (global) {

	let ConDecTextClassification = function () {
		this.projectKey = conDecAPI.getProjectKey();
		this.currentNonValidatedElements = [];
	};

	ConDecTextClassification.prototype.init = function () {
		this.issueId = JIRA.Issue.getIssueId();

		this.nonValidatedTableElement = document.getElementById("non-validated-table");
		this.nonValidatedTableContentElement = document.getElementById("non-validated-table-content");
		this.loadingSpinnerElement = document.getElementById("loading-spinner");

		this.loadData();
	}

	//-----------------------------------------
	//			Generate table of non-validated elements
	//-----------------------------------------
	ConDecTextClassification.prototype.displayNonValidatedElements = function (nonValidatedElements) {
		if (nonValidatedElements.length === 0) {
			//reset table content to empty
			this.nonValidatedTableContentElement.innerHTML = "<i>All elements have been validated!</i>";
		} else {
			//reset table content to empty
			this.nonValidatedTableContentElement.innerHTML = "";
			this.currentNonValidatedElements = nonValidatedElements;
			// append the elements
			for (let index of nonValidatedElements) {
				let row = generateTableRow(nonValidatedElements[index]);
				this.nonValidatedTableContentElement.appendChild(row);
			}
			AJS.tabs.setup();
		}
	};

	let generateTableRow = function (nonValidatedElement) {
		let row = document.createElement("tr");
		row.appendChild(generateTableCell(nonValidatedElement.type, "th-type"));
		row.appendChild(generateTableCell(nonValidatedElement.summary, "th-name"));
		row.appendChild(generateTableCell(generateOptionButtons(nonValidatedElement.id), "th-options"));
		return row;
	};

	let generateTableCell = function (content, headersId, attributes) {
		let tableCell = document.createElement("td");
		tableCell.headers = headersId;
		tableCell.innerHTML = content;
		for (let key in attributes) {
			tableCell[key] = JSON.stringify(attributes[key]);
		}
		return tableCell
	};

	let generateOptionButtons = function (elementID) {
		return `<button class='aui-button aui-button-primary' onclick="conDecAPI.setValidated(${elementID})"> <span class='aui-icon aui-icon-small aui-iconfont-link'></span> Validate </button>` +
			`<button class='aui-button aui-button-removed' onclick="conDecDialog.showEditDialog(${elementID}, 's')"> <span class="aui-icon aui-icon-small aui-iconfont-edit-filled"></span> Edit </button>`;
	};

	ConDecTextClassification.prototype.showDialog = function (index) {
		let target = this.currentNonValidatedElements[index].target;
		let self = this;
		conDecDialog.showLinkDialog(this.issueId, "i", target.id, target.documentationLocation, () => self.loadData());
	}


	//-----------------------------------------
	// Load data and call display logic.
	//-----------------------------------------

	ConDecTextClassification.prototype.loadData = function () {
		startLoadingVisualization(this.nonValidatedTableElement, this.loadingSpinnerElement);
		conDecTextClassificationAPI.getNonValidatedElements(this.projectKey, this.issueId)
			.then((nonValidatedElements) => this.displayNonValidatedElements(nonValidatedElements))
			.catch((error) => displayErrorMessage(error))
			.finally(() => stopLoadingVisualization(this.nonValidatedTableElement, this.loadingSpinnerElement));
	}

	//-----------------------------------------
	//		General purpose functions
	//-----------------------------------------

	function displayErrorMessage(error) {
		conDecAPI.showFlag("error", "Something went wrong! <br/>" + error)
	}

	function startLoadingVisualization(table, spinner) {
		table.style.visibility = "hidden";
		spinner.style.display = "flex";
	}

	function stopLoadingVisualization(table, spinner) {
		spinner.style.display = "none";
		table.style.visibility = "visible";
	}

	global.conDecTextClassification = new ConDecTextClassification();
})(window);