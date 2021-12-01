package Server_Client_Node;

import common.ClientInterface;
import common.ServerInterface;

import java.io.File;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientImplementation extends UnicastRemoteObject implements ClientInterface {
    File file;
    ServerInterface serv;
    protected ClientImplementation() throws RemoteException {
        super();
    }

    public void saveFile(File file){
        this.file = file;
    }

    public String[] getFile(){
        return this.file.list();
    }

    public void save_lookup(ServerInterface s){
        this.serv = s;
    }

    public ServerInterface get_lookup(){
        return this.serv;
    }


}
