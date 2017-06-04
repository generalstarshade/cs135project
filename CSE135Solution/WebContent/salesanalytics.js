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
			 alert(xmlDoc);
			 var response_length = xmlDoc.getElementsByTagName("length")[0].childNodes[0].nodeValue;
			 alert(response_length); 
			 
			/* for (var i = 0; i < response_length; i++) {
				 
			 }
			 
			 document.getElementById("company").innerHTML =
			 xmlDoc.getElementsByTagName("comp")[0].childNodes[0].nodeValue;
			 document.getElementById("contact").innerHTML =
			 xmlDoc.getElementsByTagName("cont")[0].childNodes[0].nodeValue;
			 document.getElementById("address").innerHTML =
			 xmlDoc.getElementsByTagName("addr")[0].childNodes[0].nodeValue;
			 document.getElementById("city").innerHTML =
			 xmlDoc.getElementsByTagName("city")[0].childNodes[0].nodeValue;
			 document.getElementById("country").innerHTML =
			 xmlDoc.getElementsByTagName("cntr")[0].childNodes[0].nodeValue;*/
		} else {
			//document.write("not 4");
		}
	}
	xmlHttp.open("GET", url, true);
	xmlHttp.overrideMimeType("text/xml");
	xmlHttp.send(null);
}
/*
function stateChanged() {

	document.write(hihi);
	if (xmlHttp.readyState == 4) {
		
		document.write("hi2");
		 var xmlDoc=xmlHttp.responseXML.documentElement;
		 var response_length = xmlDoc.getElementsByTagName("length")[0].childNodes[0].nodeValue;
		 document.write(response_length);
		 
		/for (var i = 0; i < response_length; i++) {
			 
		 }
		 
		 document.getElementById("company").innerHTML =
		 xmlDoc.getElementsByTagName("comp")[0].childNodes[0].nodeValue;
		 document.getElementById("contact").innerHTML =
		 xmlDoc.getElementsByTagName("cont")[0].childNodes[0].nodeValue;
		 document.getElementById("address").innerHTML =
		 xmlDoc.getElementsByTagName("addr")[0].childNodes[0].nodeValue;
		 document.getElementById("city").innerHTML =
		 xmlDoc.getElementsByTagName("city")[0].childNodes[0].nodeValue;
		 document.getElementById("country").innerHTML =
		 xmlDoc.getElementsByTagName("cntr")[0].childNodes[0].nodeValue;
	} else {
		document.write("not 4");
	}
}*/