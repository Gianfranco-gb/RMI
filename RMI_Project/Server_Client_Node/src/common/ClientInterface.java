package common;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ClientInterface extends Remote {

    public void saveFile(File file) throws RemoteException;
    public String[] getFile() throws RemoteException;
    public void save_lookup(ServerInterface s) throws RemoteException;
    public ServerInterface get_lookup() throws RemoteException;

}
