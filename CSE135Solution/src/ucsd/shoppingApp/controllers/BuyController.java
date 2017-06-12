package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ucsd.shoppingApp.ConnectionManager;
import ucsd.shoppingApp.ShoppingCartDAO;
import ucsd.shoppingApp.models.AnalyticsModel;
import ucsd.shoppingApp.models.ShoppingCartModel;

public class BuyController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection con = null;
	
	public BuyController() {
		con = ConnectionManager.getConnection();
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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String forward = "./buyPage.jsp";
		ShoppingCartDAO shoppingcartDao = new ShoppingCartDAO(con);
		try {
			HttpSession session = request.getSession();
			request.setAttribute("shoppingCart", shoppingcartDao.getPersonCart(session.getAttribute( "personName" ).toString()));
		} catch(Exception e) {
			request.setAttribute("message", e);
			request.setAttribute("error", true);	
		} finally {
			RequestDispatcher view = request.getRequestDispatcher(forward);
			view.forward(request, response);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// doGet(request, response);
		String forward = "./confirmation.jsp"; // to be written
		ShoppingCartDAO shoppingcartDao = new ShoppingCartDAO(con);
		try {
			HttpSession session = request.getSession();
			ServletContext application = getServletContext();
			ArrayList<ShoppingCartModel> sc = (ArrayList<ShoppingCartModel>) shoppingcartDao.getPersonCart(session.getAttribute( "personName" ).toString());
			request.setAttribute("shoppingCart", sc);
			int done = shoppingcartDao.buyPersonCart(session.getAttribute( "personName" ).toString());
			System.out.println("shopping cart size: " + sc.size());
			AnalyticsModel new_sale;
			ArrayList<AnalyticsModel> log;
			if (session.getAttribute("localLogList") == null) {
				System.out.println("log list was null");
				log = new ArrayList<AnalyticsModel>();
			} else {
				System.out.println("log list was not null! wtf");
				log = (ArrayList<AnalyticsModel>) session.getAttribute("localLogList");
				System.out.println("log size is: " + log.size());
			}

			System.out.println("right before for loop, shopping cart size is: " + sc.size());
			for (ShoppingCartModel sale : sc) {
				String product_name = sale.getProductName();
				int product_id = sale.getProductId();
				String state_name = (String) session.getAttribute("stateName");
				int state_id = (int) session.getAttribute("stateId");
				double sales = (double) sale.getPrice() * sale.getQuantity();
				new_sale = new AnalyticsModel(product_id, product_name, state_id, state_name, sales, 0);
				log.add(new_sale);
				System.out.println("Added new product to log: " + product_name);
			}
			System.out.println("About to set application log list");
			session.setAttribute("localLogList",  log);
			
		} 
		catch(Exception e) {
			System.err.println(e);
			request.setAttribute("message", e);
			request.setAttribute("error", true);
		} 
		finally {
			RequestDispatcher view = request.getRequestDispatcher(forward);
			view.forward(request, response);
		}
	}
}