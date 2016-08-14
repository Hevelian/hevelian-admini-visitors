
var barData = {
    labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
    datasets: [
        {
            label: '2016 A',
            backgroundColor: 'rgba(255, 206, 86, 0.5)',
            data: [2500, 1902, 1041, 610, 1245, 952, 3104, 1689, 1318, 589, 1199, 1436]
        },
        {
            label: '2016 B',
            backgroundColor: 'rgba(54, 162, 235, 0.5)',
            data: [3104, 1689, 1318, 589, 1199, 1436, 2500, 1902, 1041, 610, 1245, 952]
        }
    ]
};

var calcVisits = null;
function initCalculator() {
	var calcYear = document.getElementById("calcFilterYear").value;
	document.getElementById("calcHeaderYear").innerHTML = calcYear;

	var ajax = new AJAX();
	calcVisits = JSON.parse(ajax.GetNowAsText("GET", "api/visit.svc/all/"+calcYear+"/entire_year"));

	calcYearToDate(calcYear);
	calcUpdateChart(calcYear);
	calcQuarters(calcYear);
	
}

function calcUpdateChart(year) {
	
	// we need to break out the visits by month
	var _months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
	var _visitsByMonth = [];
	
	// initialise the monthly stats stuff
	for(var i=0; i<12; i++) {
		_visitsByMonth[i] = new calcMonthlyStats(_months[i]);
	}
	
	// break out the stats by month
	for(var i=0 ; i<calcVisits.length; i++) {
		var _visit = calcVisits[i];
		var _thisMonth = _visit.visit.when.split(' ')[1];
		
		for(var m=0; m<_months.length; m++) {
			if(_months[m]==_thisMonth) {
				_visitsByMonth[m].visits[_visitsByMonth[m].visits.length] = _visit;
			}
		}
	}
	
	console.log(_visitsByMonth);
	
	var _datasetVisits 		= new calcDataset("visits", "rgba(255, 206, 86, 0.5)", "y-axis-left");
	var _datasetVisitors 	= new calcDataset("visitors", "rgba(54, 162, 235, 0.5)", "y-axis-left");
	var _datasetRevenue		= new calcDataset("revenue", "rgba(255, 99, 132, 0.5)", "y-axis-right")
	
	for(var i=0; i<_visitsByMonth.length; i++) {
		_visitsByMonth[i].stats = calcUsingThis(_visitsByMonth[i].visits);
		
		_datasetVisits.data[i] 		= _visitsByMonth[i].stats.visits;
		_datasetVisitors.data[i] 	= _visitsByMonth[i].stats.visitors;
		_datasetRevenue.data[i] 	= _visitsByMonth[i].stats.total_revenue;
	}
	
	barData.datasets = [_datasetVisits, _datasetVisitors, _datasetRevenue];
	
	var ctx = document.getElementById("chartByMonth").getContext("2d");
	var barChart = Chart.Bar(ctx, {
        data: barData,
        options: {
            stacked: false,
            title:{
                display:false
            },
            scales: {
                yAxes: [{
                    display: true,
                    position: "left",
                    id: "y-axis-left",
                    scaleLabel: {
                    	display: true,
                    	labelString: "visits & visitors"
                    }
                }, {
                    display: true,
                    position: "right",
                    id: "y-axis-right",
                        scaleLabel: {
                        	display: true,
                        	labelString: "revenue"
                        }
                }]
            }
        }    
	});
}

function calcDataset(_title, _color, _yaxis) {
	this.label = _title;
	this.backgroundColor = _color;
	this.data = [];
	this.yAxisID = _yaxis;
}

/**
 * Storage for the monthly stats breakdown
 */
function calcMonthlyStats(_name) {
	this.name = _name;
	this.visits = [];
	this.stats = null;
}

function calcQuarters(year) {
	var ajax = new AJAX();
	
	// Q1
	var calcQ1all = JSON.parse(ajax.GetNowAsText("GET", "api/visit.svc/all/"+year+"/q1"));
	var Q1 = calcUsingThis(calcQ1all);
	
	document.getElementById("calcQ1_visits").innerHTML = Q1.visits;
	document.getElementById("calcQ1_visitors").innerHTML = Q1.visitors;
	document.getElementById("calcQ1_revenue").innerHTML = Q1.total_revenue.toFixed(2);
	document.getElementById("calcQ1_BTW").innerHTML = Q1.btw.toFixed(2);

	// Q2
	var calcQ2all = JSON.parse(ajax.GetNowAsText("GET", "api/visit.svc/all/"+year+"/q2"));
	var Q2 = calcUsingThis(calcQ2all);
	
	document.getElementById("calcQ2_visits").innerHTML = Q2.visits;
	document.getElementById("calcQ2_visitors").innerHTML = Q2.visitors;
	document.getElementById("calcQ2_revenue").innerHTML = Q2.total_revenue.toFixed(2);
	document.getElementById("calcQ2_BTW").innerHTML = Q2.btw.toFixed(2);

	// Q3
	var calcQ3all = JSON.parse(ajax.GetNowAsText("GET", "api/visit.svc/all/"+year+"/q3"));
	var q3 = calcUsingThis(calcQ3all);
	
	document.getElementById("calcQ3_visits").innerHTML = q3.visits;
	document.getElementById("calcQ3_visitors").innerHTML = q3.visitors;
	document.getElementById("calcQ3_revenue").innerHTML = q3.total_revenue.toFixed(2);
	document.getElementById("calcQ3_BTW").innerHTML = q3.btw.toFixed(2);
	
	// Q4
	var calcQ4all = JSON.parse(ajax.GetNowAsText("GET", "api/visit.svc/all/"+year+"/q4"));
	var Q4 = calcUsingThis(calcQ4all);
	
	document.getElementById("calcQ4_visits").innerHTML = Q4.visits;
	document.getElementById("calcQ4_visitors").innerHTML = Q4.visitors;
	document.getElementById("calcQ4_revenue").innerHTML = Q4.total_revenue.toFixed(2);
	document.getElementById("calcQ4_BTW").innerHTML = Q4.btw.toFixed(2);
}

function calcYearToDate(year) {
	
	var calcAll = calcUsingThis(calcVisits);
	document.getElementById("calcYTD_TotalVisits").innerHTML = calcAll.visits;
	document.getElementById("calcYTD_TotalVisitors").innerHTML = calcAll.visitors;
	document.getElementById("calcYTD_TotalRevenue").innerHTML = calcAll.total_revenue.toFixed(2);
	document.getElementById("calcYTD_AveragePerVisit").innerHTML = calcAll.average_visit.toFixed(2);
	document.getElementById("calcYTD_AveragePerVisitor").innerHTML = calcAll.average_visitor.toFixed(2);
	document.getElementById("calcYTD_TotalBTW").innerHTML = calcAll.btw.toFixed(2);
	document.getElementById("calcYTD_AverageVisitsPerVisitor").innerHTML = calcAll.average_per_visitor.toFixed(2);
}

function calcUsingThis(data) {
	var visitors = [];
	var totalRevenue = 0;
	for(var i=0; i<data.length; i++) {
		
		// first count unique visitors
		var found = false;
		for(var v=0; v<visitors.length; v++) {
			if(visitors[v]==data[i].contact.firstname + data[i].contact.lastname) {
				found = true;
				break;
			}
			
		}
		if(found==false) {
			visitors[visitors.length] = data[i].contact.firstname + data[i].contact.lastname;
		}
		
		totalRevenue += data[i].visit.amount;
	}

	var averagePerVisit 	= (totalRevenue!=0 && data.length!=0)? totalRevenue / data.length : 0;
	var averagePerVisitor 	= (totalRevenue!=0 && visitors.length!=0)? totalRevenue / visitors.length : 0;
	var totalBTW 			= (totalRevenue!=0)? (totalRevenue/121)*21: 0;
	var averageRPerVisitor	= (data.length!=0 && visitors.length!=0)? data.length / visitors.length : 0;
	
	return {visits: data.length, visitors: visitors.length, total_revenue: totalRevenue, average_per_visitor: averageRPerVisitor,
			average_visit: averagePerVisit, average_visitor: averagePerVisitor, btw: totalBTW}
}

