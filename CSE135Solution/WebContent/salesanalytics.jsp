<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="ucsd.shoppingApp.PersonDAO"%>
<%@ page import="ucsd.shoppingApp.models.*, java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sales Analytics</title>
<link rel="stylesheet" href="./bootstrap-3.3.7-dist/css/bootstrap.css">
</head>
<body>
	<div id="dashboard" class="row">
		<div class="col-sm-4">
			<select name="dd_cvs">
				<option value="0">Customers</option>
				<option value="1">States</option> 
			</select>
		</div>
		<div class="col-sm-4">
			<select name="dd_avt">
				<option value="0">Alphabetical</option>
				<option value="1">Top-K</option>
			</select>
		</div>
			<select name="dd_cat">
				
			</select>
		<div class="col-sm-4">
		</div>
	</div>
	<button>Run Query</button>
</body>
</html>