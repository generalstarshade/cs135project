/**
 * 
 */

function refresh() {
	var xmlHttp = new XMLHttpRequest();
	var url = "AnalyticsController";
	url = url + "?getLog=1";
	
	var top50 = [];
	var table = document.getElementById("analytics_table");
	for (var c = 1; c <= 50; c++) {
		url = url + "&" + table.rows[0].cells[c].innerText + "=" + table.rows[0].cells[c].getAttribute("data-totalsale");
	}
	url = url.replace(/\./g, "%2e");
	console.log(url);

	xmlHttp.onreadystatechange = function() {
		
		if (xmlHttp.readyState == 4) {
			
			//document.write("hi2");
			 var xmlDoc = xmlHttp.responseXML;
			 var log_length = xmlDoc.getElementsByTagName("length")[0].childNodes[0].nodeValue;
			 var purple_length = xmlDoc.getElementsByTagName("length")[1].childNodes[0].nodeValue;
			 var yellow_length = xmlDoc.getElementsByTagName("length")[2].childNodes[0].nodeValue;
			 var sales = xmlDoc.getElementsByTagName("sale");
	
			 for (var i = 0; i < log_length; i++) {
				var sale = sales[i];
			 
				// extract the elements of the new sale
				var product_name = sale.getElementsByTagName("product")[0].childNodes[0].nodeValue;
				var state_name = sale.getElementsByTagName("state")[0].childNodes[0].nodeValue;
				var amount = sale.getElementsByTagName("amount")[0].childNodes[0].nodeValue;
				
				var identifier = state_name + "_" + product_name;
				var cell = document.getElementById(identifier);
				if (cell != null) {
					var updated_sales = Number(cell.innerText) + Number(amount);
					cell.innerHTML = updated_sales;
					cell.style.color = "red";
				}
			 }
			 
			 // parse purple list
			 var purple_list = [];
			 var purple = xmlDoc.getElementsByTagName("purple")[0];

			 for (var i = 0; i < purple_length; i++) {
				 var product_name = purple.getElementsByTagName("product")[i].childNodes[0].nodeValue;
				 purple_list.push(product_name);
			 }
			 
			 // parse yellow list
			 var yellow_list = [];
			 var yellow = xmlDoc.getElementsByTagName("yellow")[0];

			 for (var i = 0; i < yellow_length; i++) {
				 var product_name = yellow.getElementsByTagName("product")[i].childNodes[0].nodeValue;
				 yellow_list.push(product_name);
			 }
			 
			 // highlight purple columns
			 for (var i = 0; i < purple_length; i++) {
				 var columns_to_purple = document.getElementsByClassName(purple_list[i]);
				 for (var j = 0; j < columns_to_purple.length; j++) {
					 columns_to_purple[j].style.color = "purple";
				 }
			 }
			 
			 var display = document.getElementById("yellow_list");
			 display.innerHTML = "New top 50 products:</br>";
			 for (var i = 0; i < yellow_length; i++) {
				 display.innerHTML += yellow_list[i] + ", ";
			 }
			 
		} else {
			//document.write("not 4");
		}
	}
	xmlHttp.open("GET", url, true);
	xmlHttp.send(null);
}