var invoices = [];

var _totalExBTW = 0;
var _BTW = 0;
var _total = 0;

function processInvoices() {
	var _invoiceNumbers = GetURLParameter('invoice').split(',');
	
	for(var i=0; i<_invoiceNumbers.length; i++) {
		var _invoice = _invoiceNumbers[i];
		
		invoices[invoices.length] = fetchInvoice(_invoice);
	}
	
	addContactDetails();
	addLineItems();
	addTotals();
}

function addTotals() {
	document.getElementById("invoiceTotalEx").innerHTML = _totalExBTW.toFixed(2).replace(/\./g, ',');
	document.getElementById("invoiceBTW").innerHTML = _BTW.toFixed(2).replace(/\./g, ',');
	document.getElementById("invoiceTotal").innerHTML = _total.toFixed(2).replace(/\./g, ',');
}

function addLineItems() {
	var tbody = document.getElementById("lineItems");
	
	for(var i=0; i<invoices.length; i++) {
		var _invoice = invoices[i];
		
		updateTotals(_invoice.visit.amount);
		
		var _tr = document.createElement("tr");
		tbody.appendChild(_tr);
		
		var _td = document.createElement("TD");
		_td.setAttribute("style", "padding-left: 4px;");
		_td.innerHTML = moment(_invoice.visit.when, "MMM DD, YYYY").format("DD-MM-YYYY");
		_tr.appendChild(_td);
		
		var _td = document.createElement("TD");
		_td.setAttribute("style", "padding-left: 4px;");
		_td.innerHTML = "Behandeling acupunctuur";
		_tr.appendChild(_td);
		
		var _td = document.createElement("TD");
		_td.innerHTML = "&nbsp;";
		_tr.appendChild(_td);
		
		// now we need to calculate the BTW stuff
		var _btw = (_invoice.visit.amount/121) *21;
		var _exbtw = _invoice.visit.amount - _btw;
		
		var _td = document.createElement("TD");
		_td.setAttribute("class", "col-align-right");
		_td.innerHTML = _exbtw.toFixed(2).replace(/\./g, ',');
		_tr.appendChild(_td);
		
		var _td = document.createElement("TD");
		_td.setAttribute("class", "col-align-right");
		_td.innerHTML = _btw.toFixed(2).replace(/\./g, ',');
		_tr.appendChild(_td);
		
		var _td = document.createElement("TD");
		_td.setAttribute("class", "col-align-right");
		_td.innerHTML = _invoice.visit.amount.toFixed(2).replace(/\./g, ',');
		_tr.appendChild(_td);
		
	}
}

function updateTotals(amount) {
	_total += amount;
	_BTW = (_total/121) * 21;
	_totalExBTW = _total - _BTW.toFixed(2);	
}

function addContactDetails() {
	document.getElementById("invoiceDate").innerHTML = moment().format("DD-MM-YYYY");
	document.getElementById("invoiceNumbers").innerHTML = GetURLParameter('invoice');
	document.getElementById("contactID").innerHTML = padNumber(invoices[0].contact.id, 3);
	document.getElementById("contactName").innerHTML = invoices[0].contact.firstname + " " + invoices[0].contact.lastname;
	document.getElementById("contactAddress1").innerHTML = invoices[0].contact.streetname + " " + invoices[0].contact.housenumber;
	document.getElementById("contactAddress2").innerHTML = invoices[0].contact.postcode + "&nbsp;&nbsp;" + invoices[0].contact.city;
}

function padNumber(number, size) {
	var s = String(number);
	while (s.length < (size || 2)) {s = "0" + s;}
	return s;
}

function fetchInvoice(_number) {
	var ajax = new AJAX();
	return JSON.parse(ajax.GetNowAsText("GET", "api/visit.svc/invoice/" + _number));
}