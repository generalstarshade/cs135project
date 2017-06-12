package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.HTMLDocument.Iterator;

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

	private ArrayList<String> getExclusionList(ArrayList<AnalyticsModel> a1, ArrayList<AnalyticsModel> a2, int html_table_size) {
		
		ArrayList<String> ret = new ArrayList<String>();
		int zsaved = 0;
		for (int y = 0; y < html_table_size; y++) {
			
			boolean contains = false;
			for (int z = 0; z < html_table_size; z++) {
				
				//If oldtop50 item is in newTop50, don't do anything
				if (a2.get(z).getProductName().equals(a1.get(y).getProductName())) {
					
					contains = true;
					//System.out.println("Found");
					//System.out.println(a1.get(y).getProductName() + " vs " + a2.get(z).getProductName());
					zsaved = z;
					break;
				}
			}
			
			//if top 50 item is not in newTop50, add to purpleList
			if (contains == false) {
				//System.out.println("\n\n\nnot found\n\n\n");
				//System.out.println(a1.get(y).getProductName() + " vs " + a2.get(zsaved).getProductName());
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
			ServletContext application = getServletContext();
			HttpSession session = request.getSession();

			if (Integer.parseInt(request.getParameter("getLog")) == 1) {
			// return log table in xml form

				response.setContentType("text/xml");
				System.out.println("In analytics controller");
				ArrayList<AnalyticsModel> log = (ArrayList<AnalyticsModel>)session.getAttribute("localLogList");
				ArrayList<AnalyticsModel> oldTop50 = new ArrayList<AnalyticsModel>();
				PrintWriter writer = response.getWriter();
				if (((ArrayList<AnalyticsModel>)session.getAttribute("localLogList")).size() == 0) {
					// do stuff that involves telling javascript refresh not to do anything
					writer.append("<theroot>");
					writer.append("<length>0</length>");
					writer.append("<length>0</length>");
					writer.append("<length>0</length>");
					writer.append("</theroot>");
					writer.flush();
				} else {
					//log = (ArrayList<AnalyticsModel>) application.getAttribute("log_list");
					
					// for testing purposes, always initialize the log arraylist
					//ArrayList<AnalyticsModel> log = new ArrayList<AnalyticsModel>();
					/*log.add(new AnalyticsModel(126, "PROD_125", 49, "Wisconsin", 100000000, 0));
					log.add(new AnalyticsModel(53, "PROD_52", 19, "Maine", 100000000, 0));
					log.add(new AnalyticsModel(40, "PROD_39", 19, "Maine", 50000000, 0));*/
					
					// testing purple/yellow functionality
					log.add(new AnalyticsModel(114, "PROD_113", 49, "Wisconsin", 100000000, 0));
					//log.add(new AnalyticsModel(139, "PROD_138", 19, "Maine", 100000000, 0));
					ResultSet rs;
	
		        	HashMap<String, Double> table_ptotal = new HashMap<String, Double>();
		        	try {
		        		analyticsDAO.insertPrecomputed(log);
		        		rs = analyticsDAO.getProductTotals((int) session.getAttribute("dd_cat"));
		        		while (rs.next()) {
		        			table_ptotal.put(rs.getString("product_name"), rs.getDouble("sum"));
		        		}
		        	} catch (SQLException e) {
						System.err.println(e);
		        	}
					Enumeration<String> en = request.getParameterNames();
					en.nextElement();
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
					writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	
					writer.append("<theroot>");
					if (log.size() == 0) {
						writer.append("<length>0</length>");
						writer.append("</theroot>");
						writer.flush();
					} else {
						System.out.println("Entering large for loop");
						int numSent = 0;
						for (AnalyticsModel sale : log) {		
							
							product_id = sale.getProductId();
							state_id = sale.getStateId();
							product_name = sale.getProductName();
							state_name = sale.getName();
							amount = sale.getSales();
							if (oldTop50.contains(sale)) {
								writer.append("<sale>");
								writer.append("<product>" + product_name + "</product>");
								writer.append("<state>" + state_name + "</state>");
								writer.append("<amount>" + amount + "</amount>");
								writer.append("</sale>");
								sale.setProductTotalSales(table_ptotal.get(product_name) + amount);
								numSent++;
							}
						}
						writer.append("<length>" + numSent + "</length>");
						System.out.println("Finished large for loop");
						
						// combine current top 50 and log list, and then sort
						/*ArrayList<AnalyticsModel> newTop50 = new ArrayList<AnalyticsModel>(oldTop50);
						newTop50.addAll(log);
						
						Collections.sort(newTop50, new AnalyticsModelNameComparator()); // do this in order to remove the duplicates
						System.out.println("Finished sorting the new top 50.");
						
						for (int i = 0; i < 50; i++) {
							System.out.println(newTop50.get(i).getProductName());
						}
						
						// remove duplicates in the newTop50 list
						for (int i = 0; i < newTop50.size() - 1; i++) {
							if (newTop50.get(i).getProductName().equals(newTop50.get(i+1).getProductName())) {
								//newTop50.get(i).setSales() = newTop50.get(i+1).getSales();
								newTop50.remove(i+1);
								i--;
							}
						}
		
						
						
						Collections.sort(newTop50, new AnalyticsModelPriceComparator());*/
					}
				
				ArrayList<AnalyticsModel> newTop50 = new ArrayList<AnalyticsModel>();
				for (Map.Entry<String, Double> entry : table_ptotal.entrySet()) {
					newTop50.add(new AnalyticsModel(entry.getKey(), entry.getValue()));
				}
				
				Collections.sort(newTop50, new AnalyticsModelPriceComparator());
				/*System.out.println("Printing top 50 lists (old vs new)");
				for (int i = 0; i < 50; i++) {
					System.out.println(oldTop50.get(i).getProductName() + " vs " + newTop50.get(i).getProductName());
				}*/

				
				System.out.println("Constructing purple list...");
				System.out.println("NewTop50Size:" + newTop50.size());
				ArrayList<String> purpleList = getExclusionList(oldTop50, newTop50, oldTop50.size());
				System.out.println("Finished constructing purple list. Now constructing yellow list...");
				ArrayList<String> yellowList = getExclusionList(newTop50, oldTop50, oldTop50.size());
				System.out.println("Finished constructing yellow list. Now sending purple/yellow list to front end...");
				System.out.println(yellowList.size());

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
				ArrayList<AnalyticsModel> log_list_new = new ArrayList<AnalyticsModel>();
				session.setAttribute("localLogList",  log_list_new); // after a successful refresh, wipe the log_list
				/*new Thread() {
					public void run() {
						try {
							if (log.size() != 0) {
								analyticsDAO.insertPrecomputed(log); // update precomputed tables
							}
						} catch (SQLException e) {
							System.err.println(e);
						}
					}
				}.start();*/
			}
		}				

		else {
			response.setContentType("text/html");
	
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
				if (session.getAttribute("localLogList") != null) {
					analyticsDAO.insertPrecomputed((ArrayList<AnalyticsModel>) session.getAttribute("localLogList"));
				}
				ArrayList<AnalyticsModel> new_log_list = new ArrayList<AnalyticsModel>();
				session.setAttribute("localLogList",  new_log_list);
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
