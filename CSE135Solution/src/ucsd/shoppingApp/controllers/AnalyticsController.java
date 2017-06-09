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

	private class AnalyticsModelPriceComparator implements Comparator<AnalyticsModel> {
	    @Override
	    public int compare(AnalyticsModel a1, AnalyticsModel a2) {
	    	double a1_sales = a1.getProductTotalSales();
	    	double a2_sales = a2.getProductTotalSales();
	    	if (a1_sales > a2_sales) {
	    		return -1;
	    	} else if (a1_sales < a2_sales) {
	    		return 1;
	    	} else {
	    		return 0;
	    	}
	    }
	}
	
	private class AnalyticsModelNameComparator implements Comparator<AnalyticsModel> {
	    @Override
	    public int compare(AnalyticsModel a1, AnalyticsModel a2) {
	    	return a1.getProductName().compareTo(a2.getProductName());
	    }
	}

	private ArrayList<String> getExclusionList(ArrayList<AnalyticsModel> a1, ArrayList<AnalyticsModel> a2) {
		ArrayList<String> ret = new ArrayList<String>();
		
		for (int y = 0; y < 50; y++) {
			
			boolean contains = false;
			for (int z = 0; z < 50; z++) {
				
				//If oldtop50 item is in newTop50, don't do anything
				if (a2.get(z).getProductName().equals(a1.get(y).getProductName())) {
					
					contains = true;
					break;
				}
			}
			
			//if top 50 item is not in newTop50, add to purpleList
			if (contains == false) {
				ret.add(a1.get(y).getProductName());
			}
		}
		
		return ret;
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
			ArrayList<AnalyticsModel> log = (ArrayList<AnalyticsModel>)application.getAttribute("log_list");
			ArrayList<AnalyticsModel> oldTop50 = new ArrayList<AnalyticsModel>();
			if (application.getAttribute("log_list") == null) {
				// do stuff that involves telling javascript refresh not to do anything
			} else {
				log = (ArrayList<AnalyticsModel>) application.getAttribute("log_list");
				// for testing purposes, always initialize the log arraylist
				/*log = new ArrayList<AnalyticsModel>();
				log.add(new AnalyticsModel("PROD_45", "Idaho", 10000000, 0));
				log.add(new AnalyticsModel("PROD_6", "Alabama", 10000000, 0));
				log.add(new AnalyticsModel("PROD_20", "Oregon", 100, 0));*/
				

				Enumeration<String> en = request.getParameterNames();
				//System.out.println(en.nextElement());
				for (int i = 0; en.hasMoreElements() && i < 50; i++) {
				
					String product_name = en.nextElement();
					double the_sales = Double.parseDouble(request.getParameter(product_name));
					//System.out.println(product_name + " " + the_sales);
					AnalyticsModel toadd = new AnalyticsModel(product_name, the_sales);
					oldTop50.add(toadd);
				}
				
				System.out.println("log size: " + log.size());

				int product_id;
				int state_id;
				String product_name;
				String state_name;
				double amount;
				PrintWriter writer = response.getWriter();
				writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

				writer.append("<theroot>");
				writer.append("<length>" + log.size() + "</length>");
	
				for (AnalyticsModel sale : log) {		
					
					product_id = sale.getProductId();
					state_id = sale.getStateId();
					product_name = sale.getProductName();
					state_name = sale.getName();
					amount = sale.getSales();
					writer.append("<sale>");
					writer.append("<product>" + product_name + "</product>");
					writer.append("<state>" + state_name + "</state>");
					writer.append("<amount>" + amount + "</amount>");
					writer.append("</sale>");
					// also take the time to update the precomputed table
					
				}
				try {
					analyticsDAO.updatePrecomputed(log);
					//sale.setProductTotalSales(analyticsDAO.getTotalSale(product_id));
				} catch (SQLException e) {
					// error
					System.out.println("SQL error: " + e.toString());
					request.setAttribute("error",  true);
					request.setAttribute("message",  e);
					return;
				}
				
				// combine current top 50 and log list, and then sort
				ArrayList<AnalyticsModel> newTop50 = new ArrayList<AnalyticsModel>(oldTop50);
				newTop50.addAll(log);
				Collections.sort(newTop50, new AnalyticsModelNameComparator()); // do this in order to remove the duplicates
				
				// remove duplicates in the newTop50 list
				for (int i = 0; i < newTop50.size() - 1; i++) {
					if (newTop50.get(i).getProductName().equals(newTop50.get(i+1).getProductName())) {
						newTop50.remove(i+1);
					}
				}
				
				Collections.sort(newTop50, new AnalyticsModelPriceComparator());

				// TODO: get the elements that was in the top 50 but are not anymore, and send those elements to javascript
				ArrayList<String> purpleList = getExclusionList(oldTop50, newTop50);
				ArrayList<String> yellowList = getExclusionList(newTop50, oldTop50);
				
				//TODO: append the purpleList to writer
				
				writer.append("<length>" + purpleList.size() + "</length>");
				writer.append("<purple>");
				for (int i = 0; i < purpleList.size(); i++) {
					writer.append("<product>" + purpleList.get(i) + "</product>");
					System.out.println("!!!purple element: " + purpleList.get(i));
				}
				writer.append("</purple>");
				writer.append("<length>" + yellowList.size() + "</length>");
				writer.append("<yellow>");
				for (int i = 0; i < yellowList.size(); i++) {
					writer.append("<product>" + yellowList.get(i) + "</product>");
					System.out.println("!!!yellow element: " + yellowList.get(i));
				}
				writer.append("</yellow>");
				writer.append("</theroot>");
				writer.flush();
				application.setAttribute("log_list",  null); // after a successful refresh, wipe the log_list
			} 
		}				

		else {
			response.setContentType("text/html");
			HttpSession session = request.getSession();
	
			int category_id;
			
			// skipping this means we came from hitting the next buttons in the analytics page
			if (request.getParameter("dd_cat") != null) {
				// being in here means we came directly from the dashboard
				// initialize the session variables
	
				category_id = Integer.parseInt(request.getParameter("dd_cat"));
	
				// save these values in session because dashboard will no longer appear after first query
	
				session.setAttribute("dd_cat",  category_id);
			}
			category_id = (int) session.getAttribute("dd_cat");
			System.out.println("category_id in analytics controller: " + category_id);
			ArrayList<AnalyticsModel> analytics;
			
			try {
				analytics = (ArrayList<AnalyticsModel>) analyticsDAO.getAnalytics(category_id);
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
