<%@page import="java.sql.Connection"%>
<%@page import="ucsd.shoppingApp.ConnectionManager"%>
<%@page import="ucsd.shoppingApp.CategoryDAO"%>
<%@page import="ucsd.shoppingApp.AnalyticsDAO"%>
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
	<%
	if(session.getAttribute("roleName") != null) { %>

		<div name="headerdiv" align="center">
		<h3>Hello <%= session.getAttribute("personName") %></h3>
		</br>
		<div name="tableDiv" align="left" style="padding-left:50px">
		<table cellspacing="5">
			<tr>
				<td valign="top"><jsp:include page="./menu.jsp"></jsp:include></td>
				<td></td>
				<td>
					
					<% Connection con = ConnectionManager.getConnection(); 
						CategoryDAO categoryDao = new CategoryDAO(con);
						AnalyticsDAO analyticsDao = new AnalyticsDAO(con);
						String role = session.getAttribute("roleName").toString();
						
						if("owner".equalsIgnoreCase(role)) { %>
						<% if(request.getAttribute("error") != null && (Boolean)request.getAttribute("error")) { %>
						<h3 style="color:red;">Data Modification Failure</h3>
						<h4 style="color:red;"><%= request.getAttribute("message").toString() %></h4>
						<% request.setAttribute("message", null);
							request.setAttribute("error", false);
							} 
						
						if(request.getAttribute("message")!= null && !(Boolean)request.getAttribute("error")) { %>
						<h4 style="color:green;"><%= request.getAttribute("message").toString() %></h4>
						<% 
						request.setAttribute("message", null);
						request.setAttribute("error", false);
							}
						
						ArrayList<CategoryModel> categories = (ArrayList<CategoryModel>)categoryDao.getCategories();
						//ArrayList<>
						%>
				</td></tr></table></div></br>
	</div>
	<div id="dashboard" class="container" align="center"
			style="border-style:solid; border-color:#DCDCDC;
			border-radius:15px; padding-bottom:20px;">
	
		<h3 style="color:#000000;">Dashboard</h3> </br>
		
		<form method="get" action="AnalyticsController">
		
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
			<option value="-1">All categories</option>
			<!-- Insert GET categories DAO -->
			<% for (CategoryModel category : categories) {
			%> <option value=<%=category.getId()%>><%=category.getCategoryName()%></option>
			<% } %>
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
	<%} else { %>
	<h3>This page is available to owners only</h3>
	<% } } %>
	<!-- ------------------------------Matrix Code--------------------------------- -->
	
	<div>
		<table>
		
		</table>
	</div>
</body>
</html>