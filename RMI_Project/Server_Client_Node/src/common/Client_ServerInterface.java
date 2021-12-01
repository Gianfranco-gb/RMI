package common;

import Server_Client_Node.Client;
import Server_Client_Node.Client_ServerImplementation;

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Client_ServerInterface extends Remote {

    public void saveFile(File file) throws RemoteException;
    public String[] getFile() throws RemoteException;
    public void registerNode(Client_ServerInterface client) throws RemoteException;
    public void set_connect() throws RemoteException;
    public boolean get_connected() throws RemoteException;
    public void set_connections(String ip, int port) throws RemoteException;
    public String getIp()throws RemoteException;
    public int getPort()throws RemoteException;
    public ArrayList Node_clients() throws RemoteException;
    public void saveBind(Client_ServerInterface bind) throws RemoteException;
    public Client_ServerInterface getBind() throws RemoteException;
    public void set_flags(String file_name,String name, String keywords, String description) throws RemoteException, IOException;
    public ArrayList get_flags() throws RemoteException;
}

