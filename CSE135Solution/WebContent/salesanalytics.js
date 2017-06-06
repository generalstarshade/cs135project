/**
 * 
 */

function refresh() {
	var xmlHttp = new XMLHttpRequest();
	var url = "AnalyticsController";
	url = url + "?getLog=1";
	
	var top50 = [];
	var table = document.getElementById("analytics_table");
	for (var c = 1; r < 50; r++) {
		url = url + "?" + table.rows[0].cells[c].innerHTML + "=" + table.rows[0].cells[c].getAttribute("data-totalsale");
	}
	
	xmlHttp.onreadystatechange = function() {
		
		if (xmlHttp.readyState == 4) {
			
			//document.write("hi2");
			 var xmlDoc = xmlHttp.responseXML;
			 var response_length = xmlDoc.getElementsByTagName("length")[0].childNodes[0].nodeValue;
			 var sales = xmlDoc.getElementsByTagName("sale");
	
			 for (var i = 0; i < response_length; i++) {
				 var sale = sales[i];
			 
				// extract the elements of the new sale
				var product_name = sale.getElementsByTagName("product")[0].childNodes[0].nodeValue;
				var state_name = sale.getElementsByTagName("state")[0].childNodes[0].nodeValue;
				var amount = sale.getElementsByTagName("amount")[0].childNodes[0].nodeValue;
				
				alert(product_name);
				alert(state_name);
				alert(amount);
				
				var identifier = state_name + "_" + product_name;
				var cell = document.getElementById(identifier);
				var updated_sales = Number(cell.innerHTML) + amount;
				cell.innerHTML = updated_sales;
				cell.style.color = "red";
			 }
		} else {
			//document.write("not 4");
		}
	}
	xmlHttp.open("GET", url, true);
	xmlHttp.send(null);
}