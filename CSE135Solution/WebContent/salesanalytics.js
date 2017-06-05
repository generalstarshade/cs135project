/**
 * 
 */

function refresh() {
	var xmlHttp = new XMLHttpRequest();
	var url = "AnalyticsController";
	url = url + "?getLog=1";
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
				
				// iterate through the HTML table to redraw and update elements
				var columns_changed = [];
				var table = document.getElementById("analytics_table");
		        for (var r = 0, n = table.rows.length; r < n; r++) {
		            for (var c = 0, m = table.rows[r].cells.length; c < m; c++) {
		            	var cell = table.rows[r].cells[c];
		            	if (r == 0 && table.rows[r].cells[c].innerHTML == product_name) {
		            		var target_column = c;
		            	}
		            	if (c == 0 && table.rows[r].cells[c] == state_name) {
		            		var target_row = r;
		            	}
		            	
		            	if (target_column == c && target_row == r) {
		            		// then redraw the updated value in red
		            		var updated_sales = Number(value.innerHTML) + amount;
		            		cell.backgroundColor = "red";
		            		cell.innerHTML = updated_sales;
		            		columns_changed.push(c);
		            	}
		            }
		        }
		    
			 }
		} else {
			//document.write("not 4");
		}
	}
	xmlHttp.open("GET", url, true);
	xmlHttp.overrideMimeType("text/xml");
	xmlHttp.send(null);
}