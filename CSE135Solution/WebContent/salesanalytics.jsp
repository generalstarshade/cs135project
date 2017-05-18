<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="ucsd.shoppingApp.PersonDAO"%>
<%@ page import="ucsd.shoppingApp.models.*, java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sales Analytics</title>
<link rel="stylesheet" href="./bootstrap-3.3.7-dist/css/bootstrap.css">
</head>
<body>
	</br>
	
	<!-- -------------------------------Dashboard Code----------------------------- -->
	
	<div id="dashboard" class="container" align="center"
			style="border-style:solid; border-color:#DCDCDC;
			border-radius:15px; padding-bottom:20px;">
	
		<h3 style="color:#000000;">Dashboard</h3> </br>
		
		<form method="get" action="salesanalytics.jsp">
		
		<div id="dropRows" class="row">	
		
			<div class="col-xs-4">
				<select name="dd_cvs" class="form-control">
					<option value="0">Customers</option>
					<option value="1">States</option>
				</select>
			</div>
			
			<div class="col-xs-4">
				<select name="dd_avt" class="form-control">
					<option value="0">Alphabetical</option>
					<option value="1">Top-K</option>
				</select>
			</div>	
					
			<div class="col-xs-4" class="form-group">
			<select name="dd_cat" class="form-control">
			
			<!-- Insert GET categories DAO -->
			</select>
			</div>
			
		</div>
		<div class="row" align="right">
			</br>
			<input type="submit" name="btn_runQuery" 
			class="btn btn-primary" value="Run Query" style="margin-right:15px;">
		</div>
		</form>
	</div>
	
	<!-- ------------------------------Matrix Code--------------------------------- -->
	
	<div>
		<table>
		
		</table>
	</div>
</body>
</html>