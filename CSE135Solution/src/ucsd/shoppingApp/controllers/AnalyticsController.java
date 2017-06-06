package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Collections;

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

	private class AnalyticsModelComparator implements Comparator<AnalyticsModel> {
	    @Override
	    public int compare(AnalyticsModel a1, AnalyticsModel a2) {
	    	double a1_sales = a1.getProductTotalSales();
	    	double a2_sales = a2.getProductTotalSales();
	    	if (a1_sales < a2_sales) {
	    		return -1;
	    	} else if (a1_sales > a2_sales) {
	    		return 1;
	    	} else {
	    		return 0;
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
			ArrayList<AnalyticsModel> log;
			ArrayList<AnalyticsModel> currentTop50 = new ArrayList<AnalyticsModel>();
			if (application.getAttribute("log_list") == null) {
				// do stuff that involves telling javascript refresh not to do anything
			} else {
				log = (ArrayList<AnalyticsModel>) application.getAttribute("log_list");
			
				Enumeration<String> en = request.getParameterNames();
				en.nextElement();
				for (int i = 0; i < 50; i++) {
					String product_name = en.nextElement();
					double the_sales = Double.parseDouble(request.getParameter(product_name));
					AnalyticsModel toadd = new AnalyticsModel(product_name, the_sales);
					currentTop50.add(toadd);
				}
				
				System.out.println("log size: " + log.size());

				String product_name;
				String state_name;
				double amount;
				int i = 0;
				PrintWriter writer = response.getWriter();
				writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

				writer.append("<theroot>");
				writer.append("<length>" + log.size() + "</length>");
	
				for (AnalyticsModel sale : log) {				
					product_name = sale.getProductName();
					state_name = sale.getName();
					amount = sale.getSales();
					writer.append("<sale id = '" + i + "'>");
					writer.append("<product>" + product_name + "</product>");
					writer.append("<state>" + state_name + "</state>");
					writer.append("<amount>" + amount + "</amount>");
					writer.append("</sale>");
					
					// also take the time to update the precomputed table
					try {
						analyticsDAO.updatePrecomputed(product_name, state_name, amount);
						sale.setProductTotalSales(analyticsDAO.getTotalSale(product_name));
						
						// combine current top 50 and log list, and then sort
						ArrayList<AnalyticsModel> combined = new ArrayList<AnalyticsModel>(currentTop50);
						combined.addAll(log);
						Collections.sort(combined, new AnalyticsModelComparator());
						
						// remove duplicates in the combined list
						for (i = 0; i < combined.size() - 1; i++) {
							if (combined.get(i).getProductName().equals(combined.get(i+1).getProductName())) {
								combined.remove(i+1);
							}
						}
						
						// TODO: get the elements that was in the top 50 but are not anymore, and send those elements to javascript
						ArrayList<AnalyticsModel> purpleList = new ArrayList<AnalyticsModel>();
						
						for (int y = 0; y < currentTop50.size(); y++) {
							
							boolean contains = false;
							for (int z = 0; z < 50; z++) {
								
								//If top50 item is in combined, don't do anything
								if (combined.get(z).getProductName().equals(currentTop50.get(y).getProductName())) {
									
									contains = true;
									break;
								}
							}
							
							//if top 50 item is not in combined, add to purpleList
							if (contains == false) {
								purpleList.add(currentTop50.get(y));
							}
						}
						
						//TODO: append the purpleList to writer
						
					} catch (SQLException e) {
						// error
						request.setAttribute("error",  true);
						request.setAttribute("message",  e);
						return;
					}
					i += 1;
				}
	
				// at this point, gather necessary updated data from the precomputed table
				
				writer.append("</theroot>");
				writer.flush();
				for (AnalyticsModel sale : log) {
					System.out.println(sale.getProductName());
				}
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
