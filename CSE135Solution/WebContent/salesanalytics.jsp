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
<script src="salesanalytics.js"></script>
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
						<h3 style="color:red;">SQL Data Retrieval Failure</h3>
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
						%>
				</td></tr></table></div></br>
				</div>
				<%
				// code to not show dashboard if user clicked on the next buttons
				
				%>

	<div id="dashboard" class="container" align="center"
			style="border-style:solid; border-color:#DCDCDC;
			border-radius:15px; padding-bottom:20px;">
	
		<h3 style="color:#000000;">Dashboard</h3> </br>
		
		<form method="get" action="AnalyticsController">
		<input type="hidden" name="getLog" value="0"/>
		
		<div id="dropRows" class="row">	
		
			<div class="col-xs-4">
				<select name="dd_cvs" class="form-control">
					<option selected value="1">States</option>
				</select>
			</div>
			
			<div class="col-xs-4">
				<select name="dd_avt" class="form-control">
					<option selected value="1">Top-K</option>
				</select>
			</div>	
					
			<div class="col-xs-4" class="form-group">
			<select name="dd_cat" class="form-control">
			<option value="-1">All categories</option>
			<!-- Insert GET categories DAO -->
			<% for (CategoryModel category : categories) {
				int cat_id = category.getId();
				if (session.getAttribute("dd_cat") != null &&
					(Integer) session.getAttribute("dd_cat") == cat_id) {
				%>
					<option selected value=<%=cat_id%>><%=category.getCategoryName()%></option>
				<%
				} else {
			%> <option value=<%=category.getId()%>><%=category.getCategoryName()%></option>
			<% } } %>
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
	<% }
	else { %>
	<h3>This page is available to owners only</h3>
	<% } %>
	<!-- ------------------------------Matrix Code--------------------------------- -->

	<% ArrayList<AnalyticsModel> analytics = (ArrayList<AnalyticsModel>) request.getAttribute("analytics_matrix"); 
	if (analytics != null) {
		// definitely stuff to display
	%>
	
	<button id="btn_refresh" class="btn btn-primary" onclick="refresh()">Refresh</button>
	
	<div>
		<table id=analytics_table>
		<tr>
		<th>Analytics Table</th>
		<%
		// create the column headers
		List<String> seen_products = new ArrayList<String>();
		
		int i = 0;
		for (AnalyticsModel analytic : analytics) {
			if (i == 50) {
				break;
			}
			
			String product_name = analytic.getProductName();
			double product_total = analytic.getProductTotalSales();
			String header_id = "header_" + product_name;
			if (seen_products.contains(product_name)) {
				// then break and start displaying at the next row
				break;
			}
			seen_products.add(product_name);

		%>
		<th id="<%=header_id%>" data-totalsale="<%=product_total%>"><b><%=product_name%></b></th>
		<%
		i += 1;
		}
		%>
		</tr>
		<%
		int j = 0;
		for (AnalyticsModel analytic: analytics) {
			if (j == 2500 || j == 50 * i) {
				break;
			}
			String product_name = analytic.getProductName();
			String name = analytic.getName();
			double sales = analytic.getSales();
			String id = name + "_" + product_name;
			if (j % i == 0) {
				%>
				<tr><th><b><%=name%></b></th>
				<%
			}
			%>
			<td id="<%=id%>" class="<%=product_name%>"><%=sales%></td>
			<%	
			j += 1;
		}
		%>
		

		</br>
		
		</table>
	</div>
	
	<%
	}
	con.close();
	}
	%>
	
	
</body>
</html>