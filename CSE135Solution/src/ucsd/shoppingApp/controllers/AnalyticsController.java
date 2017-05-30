package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ucsd.shoppingApp.ConnectionManager;
import ucsd.shoppingApp.AnalyticsDAO;
import ucsd.shoppingApp.models.AnalyticsModel;

public class AnalyticsController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection con = null;
	private AnalyticsDAO analyticsDAO = null;

	public void init() {
		con = ConnectionManager.getConnection();
		analyticsDAO = new AnalyticsDAO(con);
	}

	public void destroy() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// Same method can be used to retrieve products by id/by search/ by category
	// id/ etc..
	// we can switch based on the incoming parameters to the requests.
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		
		int customer_or_state;
		int alpha_or_sales;
		int category_id;
		int product_offset;
		int customer_offset;
		
		// skipping this means we came from hitting the next buttons in the analytics page
		if (request.getParameter("dd_cvs") != null &&
			request.getParameter("dd_avt") != null &&
			request.getParameter("dd_cat") != null) {
			// being in here means we came directly from the dashboard
			// initialize the session variables
			customer_or_state = Integer.parseInt(request.getParameter("dd_cvs"));
			alpha_or_sales = Integer.parseInt(request.getParameter("dd_avt"));
			category_id = Integer.parseInt(request.getParameter("dd_cat"));

			// save these values in session because dashboard will no longer appear after first query
			session.setAttribute("dd_cvs",  customer_or_state);
			session.setAttribute("dd_avt",  alpha_or_sales);
			session.setAttribute("dd_cat",  category_id);
		} else {
			// if we came from hitting the next buttons, HIDE THE DASHBOARD
			request.setAttribute("show_dashboard", "false");
		}
		
		customer_or_state = (int) session.getAttribute("dd_cvs");
		alpha_or_sales = (int) session.getAttribute("dd_avt");
		category_id = (int) session.getAttribute("dd_cat");
		
		if (request.getParameter("dd_prodoffset") == null) {
			product_offset = (int) session.getAttribute("dd_prodoffset");
		} else {
			product_offset = Integer.parseInt(request.getParameter("dd_prodoffset"));
		}
		
		if (request.getParameter("dd_custoffset") == null) {
			customer_offset = (int) session.getAttribute("dd_custoffset");
		} else {
			customer_offset = Integer.parseInt(request.getParameter("dd_custoffset"));
		}
		
		session.setAttribute("dd_prodoffset",  product_offset);
		session.setAttribute("dd_custoffset",  customer_offset);
		
		ArrayList<AnalyticsModel> analytics;
		
		try {
			analytics = (ArrayList<AnalyticsModel>) analyticsDAO.getAnalytics(customer_or_state, alpha_or_sales, category_id, product_offset, customer_offset);
			request.setAttribute("analytics_matrix", analytics);
		} catch (SQLException e) {
			// error
			request.setAttribute("error",  true);
			request.setAttribute("message",  e);
		}
		request.getRequestDispatcher("./salesanalytics.jsp").forward(request, response);
	}

}
