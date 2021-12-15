package common;


import Server_Client_Node.Client_ServerImplementation;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface Client_ServerInterface extends Remote, Serializable {

    void saveFile(String file) throws RemoteException;
    String getFile_Abspath() throws RemoteException;
    void registerNode(Client_ServerInterface client) throws RemoteException;
    void set_connect() throws RemoteException;
    boolean get_connected() throws RemoteException;
    void set_connections(String ip, int port) throws RemoteException;
    String getIp()throws RemoteException;
    int getPort()throws RemoteException;
    ArrayList Node_clients() throws RemoteException;
    void saveBind(Client_ServerInterface bind) throws RemoteException;
    Client_ServerInterface getBind() throws RemoteException;
    void set_flags(String file_name,String name, String keywords, String description) throws RemoteException, IOException;
    HashMap<String, HashMap<String,String>> get_flags() throws RemoteException;
    void update_files_node(String file_old,String file_new, String modify) throws IOException;
    String[] files_names() throws RemoteException;
    File getFile() throws RemoteException;
    void saveFileList()throws RemoteException;
    File[] getAll_files() throws RemoteException;
    int get_size(String hash_file, String folder) throws IOException;
    File getFile_to_download() throws RemoteException;
}

