<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.io.*,java.util.*, ucsd.shoppingApp.*, ucsd.shoppingApp.models.AnalyticsModel" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Shopping Application</title>
	</head>
	<body>
		<%
		if (application.getAttribute("log_list") == null) {
			ArrayList<AnalyticsModel> log = new ArrayList<AnalyticsModel>();
			application.setAttribute("log_list", log);
		}
		%>
	
		<% if(request.getAttribute("registration_message") != null) { %>
			<font color=green><%= request.getAttribute("registration_message") %></font>
			</br>
			<% request.setAttribute("registration_message", null); %>
		<% } %>
		<h1>Login</h1>
		
		<form name="loginForm" method="POST" action="LoginController">
			Enter Name: <input type="text" name="username" />
			<input type="submit" value="login" />
		</form>
		<br>
		Don't have a login name? <a href="signup.jsp">Sign Up</a>
	</body>
</html>