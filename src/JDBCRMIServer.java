//Michael Lim

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class JDBCRMIServer extends UnicastRemoteObject implements JDBCServerIntf {

	Connection conn;

	protected JDBCRMIServer() throws RemoteException {
		super(0);
		conn = null;
		try { //Initialize our connection to the database
			conn = DriverManager.getConnection("jdbc:mysql://database.pugetsound.edu/mlim?" //Setup JDBC connection
					+ "user=mlim&password=Aikahi96734");
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}

	//Given an SQL query String, perform the query on our database 
	public String performQuery(String query) throws RemoteException {
		String result = "";
		try {
			Statement stmt = conn.createStatement(); //Create Statement
			ResultSet rs = stmt.executeQuery(query);//Execute query and get results
			
			ResultSetMetaData md = rs.getMetaData(); //Set up info for formatting
			int numcols = md.getColumnCount();
			int totalwidth = 0;
			
			for(int i=1; i<=numcols; i++) { //Set up result headers
				int width = md.getColumnDisplaySize(i);
				totalwidth += width;
				String fmt = "%" + width + "s"; //Format the headers and append them to our result String
				result += String.format(fmt, md.getColumnName(i));
			}
			System.out.println(); //Add spacing
			for(int i=0; i<totalwidth; i++)
				System.out.print("-");
			System.out.println();

			while(rs.next()) {//Add each record
				for (int i=1; i<=numcols; i++) {
					String fldname = md.getColumnName(i);
					int fldtype = md.getColumnType(i);
					String fmt = "%" + md.getColumnDisplaySize(i);
					if (fldtype == Types.INTEGER) //Format according to field type and append to result String
						result += String.format(fmt + "d", rs.getInt(fldname));
					else
						result += String.format(fmt + "s", rs.getString(fldname));
				}
				System.out.println();
			}
			rs.close();//Close ResultSet
		}
		catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
			e.printStackTrace();
		}
		return result; //Return our completed result String
	}

	//Given an SQL String, perform an update on the database
	public int performUpdate(String update) throws RemoteException {
		int result = 0;
		try {//Create Statement and execute update. Store number of rows affected;
			Statement stmt = conn.createStatement();
			result = stmt.executeUpdate(update);
		}
		catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
			e.printStackTrace();
		}//Return result
		return result;
	}

	public static void main(String args[]) throws Exception {
		System.out.println("RMI server started");

		try { //special exception handler for registry creation
			LocateRegistry.createRegistry(1099); 
			System.out.println("java RMI registry created.");
		} catch (RemoteException e) {
			//do nothing, error means registry already exists
			System.out.println("java RMI registry already exists.");
		}

		//Create an instance of JDBCRMIServer
		JDBCRMIServer server = new JDBCRMIServer();

		// Bind this instance to the name "JDBCRMIServer." We'll use this name to connect with our client
		Naming.rebind("//localhost/JDBCRMIServer", server);
		System.out.println("JDBCRMIServer bound in registry");
	}

}
