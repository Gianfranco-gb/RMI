import Server_Client_Node.Server;

import java.rmi.RemoteException;


public class Node extends Server {


    public static void main(String args[]) throws RemoteException {
        String ip;
        int port;
        if (args.length > 2){
            ip = args[2];
            port = Integer.parseInt(args[3]);
        }else{
            ip = "127.0.0.1";
            port = 1099;
        }

        Server new_node = new Server();
        new_node.startRegister(ip,port);


    }

}
