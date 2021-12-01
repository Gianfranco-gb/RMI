package common;

import Server_Client_Node.ServerImplementation;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote {

    public void registerNode(ClientInterface client) throws RemoteException;
    public ArrayList get_list_clients()throws RemoteException;
    public void save_file(File file) throws RemoteException;
    public String[] getFile_Server() throws RemoteException;
    public boolean hasClients() throws RemoteException;
    public ArrayList get_Nodes() throws RemoteException;
    public void registerNodeServer(ServerImplementation s) throws RemoteException;
}
