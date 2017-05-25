package ucsd.shoppingApp;

/*import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;*/
import java.sql.*;

public class ConnectionManager {
	private static final String INIT_LOOOKUP = "java:comp/env";
	private static final String DB_LOOKUP = "jdbc/shoppingAppDB";
	public static Connection con = null;
	
	public ConnectionManager(){	
	}
	
	public static Connection getConnection() {
		try {
			/*Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup(INIT_LOOOKUP);
            DataSource ds = (DataSource) envContext.lookup(DB_LOOKUP);
            con = ds.getConnection();*/
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/CSE135Solution",
					"postgres", "postgres");
            con.setAutoCommit(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public static void closeConnection(Connection con) {
		try {
			if(con != null) {
				con.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
