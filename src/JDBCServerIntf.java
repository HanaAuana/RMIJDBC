//Michael Lim

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface JDBCServerIntf extends Remote {
	public String performQuery(String query) throws RemoteException;
    
    public int performUpdate(String update) throws RemoteException;
}
