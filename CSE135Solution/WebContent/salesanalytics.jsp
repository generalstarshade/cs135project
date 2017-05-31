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
				if (request.getAttribute("show_dashboard") == null || request.getAttribute("show_dashboard") == "true") {
				%>

	<div id="dashboard" class="container" align="center"
			style="border-style:solid; border-color:#DCDCDC;
			border-radius:15px; padding-bottom:20px;">
	
		<h3 style="color:#000000;">Dashboard</h3> </br>
		
		<form method="get" action="AnalyticsController">
		
		<div id="dropRows" class="row">	
		
			<div class="col-xs-4">
				<select name="dd_cvs" class="form-control">
					<%
					if (session.getAttribute("dd_cvs") == null ||
					   (Integer) session.getAttribute("dd_cvs") == 0) {
					%>
					<option selected value="0">Customers</option>
					<option value="1">States</option>
					<%
					} else {
					%>
					<option selected value="1">States</option>
					<option value="0">Customers</option>
					<%
					}
					%>
				</select>
			</div>
			
			<div class="col-xs-4">
				<select name="dd_avt" class="form-control">
					<%
					if (session.getAttribute("dd_avt") == null ||
					   (Integer) session.getAttribute("dd_avt") == 0) {
					%>
					<option selected value="0">Alphabetical</option>
					<option value="1">Top-K</option>
					<%
					} else {
					%>
					<option selected value="1">Top-K</option>
					<option value="0">Alphabetical</option>
					<%
					}
					%>
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
			<input type="hidden" name="dd_custoffset" value="0"/>
			<input type="hidden" name="dd_prodoffset" value="0"/>
			<input type="submit" name="btn_runQuery" 
			class="btn btn-primary" value="Run Query" style="margin-right:15px;">
		</div>
		</form>
	</div>
	<% } %>
	<%} else { %>
	<h3>This page is available to owners only</h3>
	<% } %>
	<!-- ------------------------------Matrix Code--------------------------------- -->
	
	<%
	if (session.getAttribute("dd_prodoffset") == null) {
		session.setAttribute("dd_prodoffset", 0);
	}
	
	if (session.getAttribute("dd_custoffset") == null) {
		session.setAttribute("dd_custoffset", 0);
	}

	ArrayList<AnalyticsModel> analytics = (ArrayList<AnalyticsModel>) request.getAttribute("analytics_matrix"); 
	if (analytics != null) {
		// definitely stuff to display
	%>
	
	<div>
		<table>
		<tr>
		<th>Analytics Table</th>
		<%
		// create the column headers
		int i = 0;
		for (AnalyticsModel analytic : analytics) {
			if (i == 10) {
				break;
			}
			String product_name = analytic.getProductName();
		%>
		<th><b><%=product_name%></b></th>
		<%
		i += 1;
		}
		%>
		</tr>
		<%
		int j = 0;
		for (AnalyticsModel analytic: analytics) {
			if (j == 200) {
				break;
			}
			String name = analytic.getName();
			double sales = analytic.getSales();
			if (j % 10 == 0) {
				%>
				<tr><th><b><%=name%></b></th>
				<%
			}
			%>
			<td><%="$" + sales%></td>
			<%	
			j += 1;
		}
		%>
		

		</br>
		
		
		<%
		int numProductsLeft = analyticsDao.getNumProductsLeft((Integer) session.getAttribute("dd_prodoffset") + 1);
		int numCustsLeft = analyticsDao.getNumCustsLeft((Integer) session.getAttribute("dd_custoffset") + 1, (Integer) session.getAttribute("dd_cvs"));
		
		if (numProductsLeft > 0) {
			// display next 10 products button
		%>
			<form method="get" action="AnalyticsController">
			<input type="hidden" name="dd_prodoffset" value="<%=(Integer) session.getAttribute("dd_prodoffset") + 1%>"/>
			<input type="submit" value="Next 10 Products"/>
			</form>
		<%
		}
		
		if (numCustsLeft > 0) {
			// display next 20 customer/state button
		%>
			<form method="get" action="AnalyticsController">
			<%if (session.getAttribute("dd_cvs") == null ||
				 (Integer) session.getAttribute("dd_cvs") == 0) { 
			%>
			<input type="hidden" name="dd_custoffset" value="<%=(Integer) session.getAttribute("dd_custoffset") + 1%>"/>
			<input type="submit" value="Next 20 Customers"/>
			</form>
			<%
			} else {
			%>
			<input type="hidden" name="dd_custoffset" value="<%=(Integer) session.getAttribute("dd_custoffset") + 1%>"/>
			<input type="submit" value="Next 20 States"/>
			</form>
			<%
			}
			%>
			
		<%
		}
		%>
		
		</table>
	</div>
	
	<%
	} 
	con.close();
	}
	%>
	
	
</body>
</html>