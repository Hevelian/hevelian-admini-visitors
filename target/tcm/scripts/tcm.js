var allContacts = null;
var allVisits = null;
var visitGroups = null;

/**
 * handles the navbar switching between pages
 * @param _switchTo
 */
function NavSwitcher(_switchTo) {
	
	document.getElementById("container_contacts").style.display = 'none';
	document.getElementById("container_visits").style.display = 'none';
	document.getElementById("container_calculator").style.display = 'none';
	
	document.getElementById("container_" + _switchTo).style.display = 'inline-block';
	
	document.getElementById("nav_contacts").setAttribute("class", "");
	document.getElementById("nav_visits").setAttribute("class", "");
	document.getElementById("nav_calculator").setAttribute("class", "");

	document.getElementById("nav_" + _switchTo).setAttribute("class", "active");
}

/*************************************************************************************************************************
 * Below, the functions for the Visits page
 */

function showInvoice() {
	var _selected = document.getElementsByName("visitSelected");

	var _Checked = "";
	for(var i=0; i<_selected.length; i++) {
		if(_selected[i].checked==true) {
			if(_Checked.length>0) {
				_Checked += ",";
			}
			
			_Checked += _selected[i].value;
		}
	}

	console.log("INVOICES: " + _Checked);
	window.open("invoice.html?invoice=" + _Checked, "_blank", "width=850,height=1000");
}

function visitRedraw() {
	var _groupBy = document.getElementById("visitGroupBy").value;

	// first remove any previous groups we may have had
	if(visitGroups!=null) {
		for(var i=0; i<visitGroups.length; i++) {
			try {
				$("#visitRow" + visitGroups[i].replace(/ /g,'') + "Header").remove();
				$("#visitRow" + visitGroups[i].replace(/ /g,'')).remove();
			} catch(e) {
				continue;
			}
		}
	}

	visitGroups = [];
	
	// now define the new groups
	for(var i=0; i<allVisits.length; i++) {
		var _visit = allVisits[i];

		switch(_groupBy) {
		case 'none':
			break;
			
		case 'name':
			addVisitGroup(_visit.contact['firstname'] + " " + _visit.contact['lastname']);
			break;

		case 'month':
			addVisitGroup(_visit.visit['when'].split(" ")[1]);
			break;

		case 'paid':
			addVisitGroup(_visit.visit['paid']);
			break;

		}
	}
	
	if(_groupBy=='none') {
		$("#visitRowNone").show();
	} else {
		$("#visitRowNone").hide();
		var _masterPanel = document.getElementById("visitMasterPanel");
		
		// now create the rows for the headers and data
		for(var i=0; i<visitGroups.length; i++) {
			visitAddPanelHeader(visitGroups[i], _masterPanel);
			visitAddPanelBody(visitGroups[i], _masterPanel);
		}
	}
}

function visitEditVisit() {
	 var _visit = null;
	 var _visitId = _visitSelectedRow.id;
	 for(var i=0; i<allVisits.length; i++) {
		 if(allVisits[i].visit.id==_visitId) {
			 _visit = allVisits[i];
			 break;
		 }
	 }
	 
	if(document.getElementById('modalPanel')==null) {
		var divModal = document.createElement("DIV");
		divModal.setAttribute("id", "modalPanel");
		divModal.setAttribute("class", "modal fade");
		divModal.setAttribute("role", "dialog");
		document.getElementById("main").appendChild(divModal);
	}

	$('#modalPanel').load("panels/edit_visit.html", function() {
		$(this).modal('show');
		if(_visit!=null) {
			
			document.getElementById('frm_amount').value = _visit.visit.amount;
			document.getElementById('frm_when').value = _visit.visit.when;
			
			if(_visit.visit.paid==1) {
				document.getElementById('frm_paid').checked = true;
			} else {
				document.getElementById('frm_paid').checked = false;
			}

			document.getElementById('visitSelectPerson').value = _visit.contact.id;
			$('.selectpicker').selectpicker('refresh');
			
		}
	});
	 
}

function visitDeleteVisit() {
	console.log("VISIT: " + _visitSelectedRow.id);
	
	if(document.getElementById('modalPanel')==null) {
		var divModal = document.createElement("DIV");
		divModal.setAttribute("id", "modalPanel");
		divModal.setAttribute("class", "modal fade");
		divModal.setAttribute("role", "dialog");
		document.getElementById("main").appendChild(divModal);
	}

	$('#modalPanel').load("panels/delete_visit.html", function() {
		$(this).modal('show');
		document.getElementById('frm_visit').innerHTML = _visitSelectedRow.id;
	});
	 
	
}

function visitAddPanelHeader(_name, _root) {
	var _div = document.createElement("DIV");
	_root.appendChild(_div);
	_div.setAttribute("id", "visitRow" + _name.replace(/ /g,'') + "Header");
	_div.setAttribute("class", "row");
	_div.setAttribute("style", "background-color: #d9edf7; height: 40px;");
	
	var _p = document.createElement("P");
	_div.appendChild(_p);
	_p.setAttribute("style", "padding-top: 10px; padding-left: 16px;");
	_p.innerHTML = _name;
}

function visitAddPanelBody(_name, _root) {
	var _xname = _name.replace(/ /g,'');
	
	var _div = document.createElement("DIV");
	_div.setAttribute("class", "row");
	_div.setAttribute("id", "visitRow" + _xname);
	_root.appendChild(_div);
	
	var _div2 = document.createElement("DIV");
	_div2.setAttribute("class", "col-sm-12");
	_div.appendChild(_div2);
	
	var _table = document.createElement("TABLE");
	_table.setAttribute("class", "table table-striped table-condensed table-hover");
	_div2.appendChild(_table);
	
	var _tableHead = document.createElement("THEAD");
	_table.appendChild(_tableHead);
	var _headRow = document.createElement("TR");
	_tableHead.appendChild(_headRow);
	
	// add th stuff
	var _th1 = document.createElement("TH");
	_th1.innerHTML = "&nbsp;";
	_headRow.appendChild(_th1);
	
	var _th2 = document.createElement("TH");
	_th2.innerHTML = "Invoice";
	_headRow.appendChild(_th2);
	
	var _th2 = document.createElement("TH");
	_th2.innerHTML = "Date";
	_headRow.appendChild(_th2);
	
	var _th2 = document.createElement("TH");
	_th2.innerHTML = "Firstname";
	_headRow.appendChild(_th2);
	
	var _th2 = document.createElement("TH");
	_th2.innerHTML = "Lastname";
	_headRow.appendChild(_th2);
	
	var _th2 = document.createElement("TH");
	_th2.innerHTML = "Email";
	_headRow.appendChild(_th2);
	
	var _th2 = document.createElement("TH");
	_th2.innerHTML = "Tel. Home";
	_headRow.appendChild(_th2);
	
	var _th2 = document.createElement("TH");
	_th2.innerHTML = "Tel. Mobile";
	_headRow.appendChild(_th2);
	
	var _th2 = document.createElement("TH");
	_th2.setAttribute("style", "text-align: right");
	_th2.innerHTML = "Amount";
	_headRow.appendChild(_th2);
	
	var _th2 = document.createElement("TH");
	_th2.setAttribute("style", "text-align: right");
	_th2.innerHTML = "Paid?";
	_headRow.appendChild(_th2);
	
	var _tableBody = document.createElement("TBODY");
	_tableBody.setAttribute("id", "tbodyVisits" + _xname);
	_table.appendChild(_tableBody);
}

function addVisitGroup(_name) {
	for(var i=0; i<visitGroups.length; i++) {
		if(visitGroups[i] == _name) {
			return;
		}
	}
	
	visitGroups[visitGroups.length] = _name;
	console.log("ADDING VISIT GROUP TO LIST: " + _name);
}

/**
 * actual routine that fetches the contacts via ajax.
 */
function fetchAllVisits() {
	var _filterYear = document.getElementById("visitFilterYear").value;
	var _filterDetail = document.getElementById("visitFilterDetail").value;
	 
	var ajax = new AJAX();
	allVisits = JSON.parse(ajax.GetNowAsText("GET", "api/visit.svc/all/"+_filterYear+"/" + _filterDetail));
}

function loadVisits() {
	var _groupBy = document.getElementById("visitGroupBy").value;
	
	fetchAllVisits();
	visitRedraw();
	
	var tbody = document.getElementById("tbodyVisitsNone");
	tbody.innerHTML = "";	// clear all previous rows from the table
	var toTbody = tbody;
	
	for(var i=0; i<allVisits.length; i++) {
		var _visit = allVisits[i];
		
		switch(_groupBy) {
		case 'none':
			toTbody = tbody;
			break;
			
		case 'name':
			toTbody = document.getElementById("tbodyVisits" + _visit.contact['firstname'].replace(/ /g,'') + _visit.contact['lastname'].replace(/ /g,''));
			break;
			
		case 'month':
			toTbody = document.getElementById("tbodyVisits" + _visit.visit['when'].split(" ")[1]);
			break;
			
		}
		visitAddTableRow(toTbody, _visit);		
	}
	
	document.getElementById("visitCount").innerHTML = allVisits.length;
}

function fetchVisitYears() {
	var ajax = new AJAX();
	var years = JSON.parse(ajax.GetNowAsText("GET", "api/visit.svc/years"));
	var _select = document.getElementById("visitFilterYear");
	var _selectCalc = document.getElementById("calcFilterYear");
	
	$("#visitFilterYear").empty();
	$("#calcFilterYear").empty();
	
  	for(var i=0; i<years.length; i++) {
  		_select.options[_select.length] = new Option(years[i], years[i]);
  		_selectCalc.options[_selectCalc.length] = new Option(years[i], years[i]);
  	}
  	
  	_select.options[_select.length] = new Option("all years", "all_years");

  	$('#visitFilterYear').selectpicker('refresh');
  	$('#calcFilterYear').selectpicker('refresh');
}

function visitSearch() {
	var criteria = document.getElementById("visitSearchCriteria");
	
	if(criteria.value=="") {
		loadVisits();
		return;
	}
	
	if(allVisits==null || allVisits.length==0) {
		fetchAllVisits();
	}
	
	if(allVisits==null || allVisits.length==0) {
		return;
	}
	
	var tbody = document.getElementById("tbodyVisitsNone");
	tbody.innerHTML = "";	// clear all previous rows from the table

	var _groupBy = document.getElementById("visitGroupBy").value;
	// clear all the table bodies
	for(var i=0; i<visitGroups.length; i++) {
		document.getElementById("tbodyVisits" + visitGroups[i].replace(/ /g,'')).innerHTML = "";
	}
	
	var _cnt = 0;
	for(var i=0; i<allVisits.length; i++) {
		var _visit = allVisits[i];
		
		var _searchString = _visit.visit["invoice"] + "^" + ((_visit.visit["paid"]=="1")? "yes":"no") + "^" + _visit.visit["when"] + "^" + _visit.visit["amount"] + "^" +
							_visit.contact["firstname"] + "^" + _visit.contact["lastname"] + "^" + _visit.contact["tel_home"] +
							_visit.contact["tel_mobile"] + "^" + _visit.contact["email"];
			
		if(_searchString.toUpperCase().search(criteria.value.toUpperCase()) != -1) {
			switch(_groupBy) {
			case 'none':
				toTbody = tbody;
				break;
				
			case 'name':
				toTbody = document.getElementById("tbodyVisits" + _visit.contact['firstname'].replace(/ /g,'') + _visit.contact['lastname'].replace(/ /g,''));
				break;
				
			case 'month':
				toTbody = document.getElementById("tbodyVisits" + _visit.visit['when'].split(" ")[1]);
				break;
				
			}
			visitAddTableRow(toTbody, _visit);		
			_cnt++;
		}
	}
	
	document.getElementById("visitCount").innerHTML = _cnt + " of " + allVisits.length;
}

function visitAddTableRow(tbody, _visit) {

	var _row = document.createElement("tr");
	if(_visit.visit['paid']==0) {
		_row.setAttribute("class", "danger");
	}
	_row.setAttribute("onClick", "visitRowClicked(this)");
	_row.setAttribute("id", _visit.visit['id']);
	tbody.appendChild(_row);

	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = '<input name="visitSelected" type="checkbox" value="'+_visit.visit['id']+'" onClick="visitCheckboxClicked()"/>'

	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = _visit.visit['id'];
	
	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _visit.visit['when']!='undefined')? _visit.visit['when'] : "";
	
	var _column = document.createElement("td");
	_column.setAttribute("class", "hevelian-pale");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _visit.contact['firstname']!='undefined')? _visit.contact['firstname'] : "";
	
	var _column = document.createElement("td");
	_column.setAttribute("class", "hevelian-pale");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _visit.contact['lastname']!='undefined')? _visit.contact['lastname'] : "";
	
	var _column = document.createElement("td");
	_column.setAttribute("class", "hevelian-pale");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _visit.contact['email']!='undefined')? _visit.contact['email'] : "";
	
	var _column = document.createElement("td");
	_column.setAttribute("class", "hevelian-pale");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _visit.contact['tel_home']!='undefined')? _visit.contact['tel_home'] : "";
	
	var _column = document.createElement("td");
	_column.setAttribute("class", "hevelian-pale");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _visit.contact['tel_mobile']!='undefined')? _visit.contact['tel_mobile'] : "";
	
	var _column = document.createElement("td");
	_column.setAttribute("style", "text-align: right");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _visit.visit['amount']!='undefined')? _visit.visit['amount'] : "";
	
	var _column = document.createElement("td");
	_column.setAttribute("style", "text-align: right");
	_row.appendChild(_column);
	_column.innerHTML = (_visit.visit['paid']==1)? "yes" : "no";
	
}

function visitCheckboxClicked() {	
	var _selected = document.getElementsByName("visitSelected");

	var _cntChecked = 0;
	for(var i=0; i<_selected.length; i++) {
		if(_selected[i].checked==true) {
			_cntChecked++;
		}
	}
	
	if(_cntChecked>0) {
		$("#btnVisitInvoice").removeClass("disabled");
	} else {
		$("#btnVisitInvoice").addClass("disabled");
	}
}

function _handlerClickAddVisitButton() {
	if(document.getElementById('modalPanel')==null) {
		var divModal = document.createElement("DIV");
		divModal.setAttribute("id", "modalPanel");
		divModal.setAttribute("class", "modal fade");
		divModal.setAttribute("role", "dialog");
		document.getElementById("main").appendChild(divModal);
	}

	$('#modalPanel').load("panels/new_visit.html", function() {
		$(this).modal('show');
	});
	
}

var _visitSelectedRow = null;
function visitRowClicked(_ClickedRow) {
	if(_visitSelectedRow!=null) {
		$(_visitSelectedRow).removeClass('info');
	}
	
	if(_ClickedRow==_visitSelectedRow) {
		$(_visitSelectedRow).removeClass('info');
		$("#btnEditVisit").addClass("disabled");
		$("#btnDeleteVisit").addClass("disabled");
		_visitSelectedRow = null;
		return;
	}
	
	_visitSelectedRow = _ClickedRow;
	$(_ClickedRow).addClass('info');

	$("#btnEditVisit").removeClass("disabled");
	$("#btnDeleteVisit").removeClass("disabled");
}

/*************************************************************************************************************************
 * Below, the functions for the Contacts page
 */
function _handlerClickAddContactButton() {

	if(document.getElementById('modalPanel')==null) {
		var divModal = document.createElement("DIV");
		divModal.setAttribute("id", "modalPanel");
		divModal.setAttribute("class", "modal fade");
		divModal.setAttribute("role", "dialog");
		document.getElementById("main").appendChild(divModal);
	}

	$('#modalPanel').load("panels/new_contact.html", function() {
		$(this).modal('show');
	});
	
}


/**
 * performs a free text search on all the contacts. it loads the contacts from the server if necessary.
 */
function contactSearch() {
	var criteria = document.getElementById("contactSearchCriteria");
	console.log("CONTACT SEARCH FOR: " + criteria.value);
	
	if(criteria.value=="") {
		loadContacts();
		return;
	}
	
	if(allContacts==null || allContacts.length==0) {
		fetchAllContacts();
	}
	
	if(allContacts==null || allContacts.length==0) {
		return;
	}
	
	var tbody = document.getElementById("tbodyContacts");
	tbody.innerHTML = "";	// clear all previous rows from the table
	
	var _cnt = 0;
	for(var i=0; i<allContacts.length; i++) {
		var _contact = allContacts[i];
		
		var _searchString = ((typeof _contact['firstname']!='undefined')? _contact['firstname']: "") + "^"+ 
							((typeof _contact['lastname']!='undefined')? _contact['lastname']: "") + "^" + 
							((typeof _contact['streetname']!='undefined')? _contact['streetname']: "") + "^" + 
							((typeof _contact['email']!='undefined')? _contact['email']: "") + "^" + 
							((typeof _contact['postcode']!='undefined')? _contact['postcode']: "") + "^" + 
							((typeof _contact['housenumber']!='undefined')? _contact['housenumber']: "") + "^" + 
							((typeof _contact['city']!='undefined')? _contact['city']: "") + "^" + 
							((typeof _contact['tel_home']!='undefined')? _contact['tel_home']: "") + "^" + 
							((typeof _contact['tel_mobile']!='undefined')? _contact['tel_mobile']: "");
		
		if(_searchString.toUpperCase().search(criteria.value.toUpperCase()) != -1) {
			contactAddTableRow(tbody, _contact);
			_cnt++;
		}
	}
	
	document.getElementById("contactCount").innerHTML = _cnt + " of " + allContacts.length;
}

/**
 * actual routine that fetches the contacts via ajax.
 */
function fetchAllContacts() {
	var ajax = new AJAX();
	allContacts = JSON.parse(ajax.GetNowAsText("GET", "api/contact.svc/all"));
	console.log("ALL CONTACTS: " + allContacts);
	
}

/**
 * fetches all the contacts and stores them in a global array.
 */
function loadContacts() {
	
	fetchAllContacts();
	
	var tbody = document.getElementById("tbodyContacts");
	tbody.innerHTML = "";	// clear all previous rows from the table
	
	for(var i=0; i<allContacts.length; i++) {
		var _contact = allContacts[i];
		
		contactAddTableRow(tbody, _contact);		
	}
	
	document.getElementById("contactCount").innerHTML = allContacts.length;
}

var _contactSelectedRow = null;
function contactRowClicked(_ClickedRow) {
	if(_contactSelectedRow!=null) {
		$(_contactSelectedRow).removeClass('info');
	}
	
	if(_ClickedRow==_contactSelectedRow) {
		$(_contactSelectedRow).removeClass('info');
		$("#btnEditContact").addClass("disabled");
		$("#btnDeleteContact").addClass("disabled");
		_contactSelectedRow = null;
		return;
	}
	
	_contactSelectedRow = _ClickedRow;
	$(_ClickedRow).addClass('info');

	$("#btnEditContact").removeClass("disabled");
	$("#btnDeleteContact").removeClass("disabled");
}

function contactEditContact() {
	 var _contact = null;
	 var _contactId = _contactSelectedRow.id.split('_')[1];
	 for(var i=0; i<allContacts.length; i++) {
		 if(allContacts[i].id==_contactId) {
			 _contact = allContacts[i];
			 break;
		 }
	 }
	 
	if(document.getElementById('modalPanel')==null) {
		var divModal = document.createElement("DIV");
		divModal.setAttribute("id", "modalPanel");
		divModal.setAttribute("class", "modal fade");
		divModal.setAttribute("role", "dialog");
		document.getElementById("main").appendChild(divModal);
	}

	$('#modalPanel').load("panels/edit_contact.html", function() {
		$(this).modal('show');
		if(_contact!=null) {
			document.getElementById('frm_firstname').value = _contact.firstname;
			document.getElementById('frm_lastname').value = (typeof _contact.lastname!='undefined')? _contact.lastname: "";
			document.getElementById('frm_email').value = (typeof _contact.email!='undefined')? _contact.email : "";
			document.getElementById('frm_tel_home').value = (typeof _contact.tel_home!='undefined')? _contact.tel_home : "";
			document.getElementById('frm_tel_mobile').value = (typeof _contact.tel_mobile!='undefined')? _contact.tel_mobile : "";
			document.getElementById('frm_streetname').value = _contact.streetname;
			document.getElementById('frm_housenumber').value = _contact.housenumber;
			document.getElementById('frm_city').value = _contact.city;
			document.getElementById('frm_postcode').value = _contact.postcode;
		}
	});
	 
 }
 
 function contactDeleteContact() {
	 var _contact = null;
	 var _contactId = _contactSelectedRow.id.split('_')[1];
	 for(var i=0; i<allContacts.length; i++) {
		 if(allContacts[i].id==_contactId) {
			 _contact = allContacts[i];
			 break;
		 }
	 }
	 
	if(document.getElementById('modalPanel')==null) {
		var divModal = document.createElement("DIV");
		divModal.setAttribute("id", "modalPanel");
		divModal.setAttribute("class", "modal fade");
		divModal.setAttribute("role", "dialog");
		document.getElementById("main").appendChild(divModal);
	}

	$('#modalPanel').load("panels/delete_contact.html", function() {
		$(this).modal('show');
		if(_contact!=null) {
			document.getElementById('frm_firstname').innerHTML = _contact.firstname;
			document.getElementById('frm_lastname').innerHTML = (typeof _contact.lastname!='undefined')? _contact.lastname: "";
		}
	});
	 
 }
 
/**
 * adds a single row to the contacts table in the browser
 * @param tbody
 * @param _contact
 */
function contactAddTableRow(tbody, _contact) {

	var _row = document.createElement("tr");
	_row.setAttribute("onClick", "contactRowClicked(this)");
	_row.setAttribute("id", "contact_" + _contact['id']);
	tbody.appendChild(_row);

	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = _contact['id'];
	
	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _contact['firstname']!='undefined')? _contact['firstname'] : "";
	
	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _contact['lastname']!='undefined')? _contact['lastname'] : "";
	
	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _contact['email']!='undefined')? _contact['email'] : "";
	
	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _contact['tel_home']!='undefined')? _contact['tel_home'] : "";
	
	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _contact['tel_mobile']!='undefined')? _contact['tel_mobile'] : "";
	
	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _contact['streetname']!='undefined')? _contact['streetname'] : "";
	
	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _contact['housenumber']!='undefined')? _contact['housenumber'] : "";
	
	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _contact['city']!='undefined')? _contact['city'] : "";
	
	var _column = document.createElement("td");
	_row.appendChild(_column);
	_column.innerHTML = (typeof _contact['postcode']!='undefined')? _contact['postcode'] : "";
}