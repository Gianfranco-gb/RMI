package Server_Client_Node;

import common.ClientInterface;
import common.ServerInterface;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {
    ArrayList<ClientInterface> Node_clients = new ArrayList<>();
    ArrayList<ServerImplementation> Node_servers = new ArrayList<>();
    File file;
    protected ServerImplementation() throws RemoteException {
        super();
    }
    public void registerNode(ClientInterface client) throws RemoteException {
        Node_clients.add(client);

    }

    public void registerNodeServer(ServerImplementation s) throws RemoteException{
        Node_servers.add(s);
    }

    public ArrayList get_list_clients() throws RemoteException {
        //String[] Files = new String[6];
        ArrayList<String[]> files = new ArrayList<>();
        for(ClientInterface N_client : Node_clients){

            files.add(N_client.getFile());
            //files.add(file.list());
        }
        return files;
    }

    public String[] getFile_Server(){
        return this.file.list();
    }

    public void save_file(File s_file) throws RemoteException {
        this.file = s_file;
    }

    public boolean hasClients() throws RemoteException{
        return Node_clients.size()>0;
    }

    public ArrayList get_Nodes() throws RemoteException{
        return Node_servers;
    }
}
