package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
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
		
		if (Integer.parseInt(request.getParameter("getLog")) == 1) {
			// return log table in xml form
			response.setContentType("text/xml");
			System.out.println("In analytics controller");
			ServletContext application = getServletContext();
			ArrayList<AnalyticsModel> log = (ArrayList<AnalyticsModel>) application.getAttribute("log_list");
			
			System.out.println("log size: " + log.size());
			
			PrintWriter writer = response.getWriter();
			writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			int i = 0;
			writer.append("<theroot>");
			writer.append("<length>" + log.size() + "</length>");

			for (AnalyticsModel sale : log) {
				writer.append("<sale id = '" + i + "'>");
				writer.append("<product>" + sale.getProductName() + "</product>");
				writer.append("<state>" + sale.getName() + "</state>");
				writer.append("<amount>" + sale.getSales() + "</amount>");
				writer.append("</sale>");
			}
			writer.append("</theroot>");
			writer.flush();
			for (AnalyticsModel sale : log) {
				System.out.println(sale.getProductName());
			}
			
		} else {
			response.setContentType("text/html");
			HttpSession session = request.getSession();
	
			int category_id;
			int total_offset;
			
			// skipping this means we came from hitting the next buttons in the analytics page
			if (request.getParameter("dd_cat") != null) {
				// being in here means we came directly from the dashboard
				// initialize the session variables
	
				category_id = Integer.parseInt(request.getParameter("dd_cat"));
	
				// save these values in session because dashboard will no longer appear after first query
	
				session.setAttribute("dd_cat",  category_id);
				session.setAttribute("dd_totaloffset", 200); // default total offset to be 200
			}
			
			category_id = (int) session.getAttribute("dd_cat");
			
			ArrayList<AnalyticsModel> analytics;
			
			if (session.getAttribute("dd_totaloffset") == null) {
				session.setAttribute("dd_totaloffset",  200);
			}
			
			if ((Integer) session.getAttribute("dd_totaloffset") == 0) {
				session.setAttribute("dd_totaloffset",  200);
			}
			
			total_offset = (Integer) session.getAttribute("dd_totaloffset");
			
			try {
				analytics = (ArrayList<AnalyticsModel>) analyticsDAO.getAnalytics(category_id, total_offset);
				request.setAttribute("analytics_matrix", analytics);
			} catch (SQLException e) {
				// error
				request.setAttribute("error",  true);
				request.setAttribute("message",  e);
			}
			request.getRequestDispatcher("./salesanalytics.jsp").forward(request, response);
		}
	}

}
