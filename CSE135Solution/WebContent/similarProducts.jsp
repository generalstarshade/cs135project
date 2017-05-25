<%@page import="java.sql.Connection"%>
<%@page import="ucsd.shoppingApp.ConnectionManager"%>
<%@page import="ucsd.shoppingApp.CategoryDAO"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="ucsd.shoppingApp.models.*, ucsd.shoppingApp.*, java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Similar Products</title>
</head>
<body>
<jsp:include page="./menu.jsp"></jsp:include>

	<%
	if(session.getAttribute("roleName") != null) { %>
		<h3>Hello <%= session.getAttribute("personName") %></h3>
	
		<table cellspacing="5">
			<tr>
				<td>
					<h3>Similar Product Pairs</h3> 
					<% Connection con = ConnectionManager.getConnection(); 
						SimilarProductDAO simProductDAO = new SimilarProductDAO(con);
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
						%>
						<table border=1 style="border-collapse: collapse">
				        <thead>
				            <tr>
				                <th>Product 1</th>
				                <th>Product 2</th>
				            </tr>
				        </thead>
				        <tbody>
							<% 	ArrayList<SimilarProductModel> simProducts = (ArrayList<SimilarProductModel>)simProductDAO.getSimilarProducts();
								for(SimilarProductModel simProduct : simProducts) {
							%>
							<tr>
								<td><%=simProduct.getProduct1_name() %></td>
								<td><%=simProduct.getProduct2_name() %></td>

							<% } %>
				        </tbody>
				    </table>
					<% } else { %>
					<h3>This page is available to owners only</h3>
					<% } 
					con.close();
					%>
				</td>
			</tr>
		</table>
	<% } else { %>
		<h3>Please <a href = "./login.jsp">login</a> before viewing the page</h3>
	<% }
	%>
	
</body>
</html>