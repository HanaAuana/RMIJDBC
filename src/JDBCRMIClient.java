//Michael Lim

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class JDBCRMIClient {
	
	public JDBCServerIntf server;
	
	public JDBCRMIClient(JDBCServerIntf server){
		this.server = server;
	}
	public void performQuery(String query){
		String results = "";
		try {
			results = this.server.performQuery(query);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//Use server performQuery method
		System.out.println(results);
	}
	
public void performUpdate(String update){
		int numUpdatedRows = 0;
		try {
			numUpdatedRows = server.performUpdate(update);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//Use server performUpdate method
		System.out.println("Updated "+numUpdatedRows+" rows");
	}

	public static void main(String[] args) {
		
		JDBCServerIntf server = null;
		
		try {//Connect to our server
			server = (JDBCServerIntf)Naming.lookup("//localhost/JDBCRMIServer");
		}catch (RemoteException e1) {//Catch various exceptions
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		
		JDBCRMIClient client = new JDBCRMIClient(server);
		
		try {//Set up to read in commands from command line
			Reader rdr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(rdr);

			while (true) {//While we are getting input
				System.out.print("\nSQL> "); //Prompt user
				String cmd = br.readLine().trim();//Trim whitespace
				System.out.println();
				if (cmd.startsWith("exit"))
					break; //If user inputs "exit", then quit
				else if (cmd.startsWith("select")){//If user enters a query
					client.performQuery(cmd);
				}
				else{// If user enters an update
					client.performUpdate(cmd);
				}
		    }
	    }
	    catch (Exception e) {
			e.printStackTrace();
		}
	}
}
