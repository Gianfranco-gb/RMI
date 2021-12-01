package Server_Client_Node;

import common.Client_ServerInterface;
import common.ServerInterface;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;

public class Server_Client_Node {
    ServerInterface serv;
    public static void main(String args[]) throws IOException, AlreadyBoundException, NotBoundException {
        Scanner scanner = new Scanner(System.in).useDelimiter("\n");
        ArrayList<String[]> flags = new ArrayList<>();
        //Registry registry;
        System.out.println("Desea crear un nodo especificando la IP y PORT ?.");
        if (scanner.next().equals("Si")){
            Client_ServerImplementation node = create_nodeWithIPandPort(scanner, flags);
            while(true) {
                option(node);
            }
        }else{
            Client_ServerImplementation node = create_nodeWithNothing(scanner);
            while(true) {
                option(node);
            }
        }

    }

    public static Client_ServerImplementation create_nodeWithIPandPort(Scanner scanner, ArrayList<String[]> flags) throws IOException, AlreadyBoundException {
        // Registry registry;
        System.out.println("Especifique la IP: ");
        String ip = scanner.next();
        System.out.println("Especifique el PORT");
        int port_connection = Integer.parseInt(scanner.next());
        Client_ServerImplementation node = Node_client(ip, port_connection);
        assert node != null;
        node.set_connections(ip,port_connection);
        System.out.println("Que puerto desea que utilize este nodo?");
        String port_server = scanner.next();
        Registry registry= Node_Server(ip,Integer.parseInt(port_server));
        Client_ServerImplementation node2=bind(registry, port_server);
        node2.set_connections(ip,port_connection);
        System.out.println("Que archivos analizo(Indique la ubicacion)?: ");
        String dir = scanner.next();
        File file = folder(dir);
        node.saveFile(file);
        node.saveBind(node2);
        node2.saveFile(file);
        node2.set_connect();
        node.set_flags("","","","");
        return node2;
    }


    public static Client_ServerImplementation create_nodeWithNothing(Scanner scanner) throws RemoteException, AlreadyBoundException {
        String ip = "127.0.0.1";
        int port_connection = 1099;
        Registry registry= Node_Server(ip, port_connection);
        Client_ServerImplementation node = bind(registry, String.valueOf(port_connection));
        node.set_connections(ip,port_connection);
        System.out.println("Que archivos analizo(Indique la ubicacion)?: ");
        String dir = scanner.next();
        File file = folder(dir);
        node.saveFile(file);
        return node;
    }

    public static void Search(Client_ServerImplementation node,String search) throws RemoteException, NotBoundException {
        ArrayList<ArrayList<HashMap>> files;
        files=get_Top(node);
        ArrayList<ArrayList<HashMap>> output = new ArrayList<>();
        String hash_file;
        for(int i = 0; i < files.size(); i++) {
            for (int j = 0; j < files.get(i).size(); j++) {
                String total_description = (String) files.get(i).get(j).get("Description");
                String total_keywords = (String) files.get(i).get(j).get("Keywords");
                String[] keywords = total_keywords.split(",");
                String[] descriptions = total_description.split(",");

                if (files.get(i).get(j).get("Name").equals(search)) {
                    hash_file = (String) files.get(i).get(j).get("Hash");
                    output.add(get_same_hash(hash_file, files));
                } else {
                    for (String key : keywords) {
                        if (key.equals(search)) {
                            hash_file = (String) files.get(i).get(j).get("Hash");
                            output.add(get_same_hash(hash_file, files));
                        }
                    }
                    for (String descrip : descriptions) {
                        if (descrip.equals(search)) {
                            hash_file = (String) files.get(i).get(j).get("Hash");
                            output.add(get_same_hash(hash_file, files));

                        }
                    }
                }
            }
        }
        print_search(output);
    }

    public static void print_search(ArrayList<ArrayList<HashMap>> output) throws RemoteException{
        ArrayList<ArrayList<String>> list_output = new ArrayList<>();
        for(int i = 0; i<output.size(); i++){
            for(int j = 0; j < output.get(i).size(); j++){
                String hash =
            }
        }

    }

    public static ArrayList<HashMap> get_same_hash(String hash, ArrayList<ArrayList<HashMap>> files) throws RemoteException{
        ArrayList<HashMap> same_files = new ArrayList<>();
        for(int i = 0; i < files.size(); i++){
            for(int j = 0; j <files.get(i).size(); j++){
                if(files.get(i).get(j).get("Hash").equals(hash)){
                    same_files.add(files.get(i).get(j));
                }
            }
        }
        return same_files;
    }

    public static void Assign_flags_to_file(Client_ServerImplementation node, Scanner scan) throws IOException {
        System.out.println("Escriba el nombre del fichero: ");
        String file_name = scan.next();
        System.out.println("Que nombre desea asignar al fichero?: ");
        String name = scan.next();
        System.out.println("Que keywords desea asignar al fichero?: ");
        String keywords = scan.next();
        System.out.println("Que descripcion desea asignar al fichero?: ");
        String description = scan.next();
        node.set_flags(file_name,name, keywords, description);
    }


    public static String option(Client_ServerImplementation node) throws IOException, NotBoundException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Que desea hacer? :");
        System.out.println("1 --> Search todos los nodos");
        System.out.println("2 --> Descargar");
        System.out.println("3 --> Modificar un archivo");
        System.out.println("4 --> Asignar 'Name/Keywords/Descriptions' a un fichero");
        String resp = scan.next();
        if(resp.equals("1")){
            System.out.println("Escriba que desea buscar (Title/Description/Keywords)");
            String search = scan.next();
            Search(node,search);
        }else if(resp.equals("2")){
            System.out.println("Escribe el filename del fichero: ");
            Assign_flags_to_file(node,scan);
        }
        return resp;
    }

    public static Registry Node_Server(String ip, int port) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(ip,port);
            registry.list();
            return registry;
        }
        catch (RemoteException ex) {
            System.out.println("RMI registry cannot be located ");
            Registry registry= LocateRegistry.createRegistry(port);
            System.out.println("RMI registry created at port ");
            return registry;
        }
    }

    public static Client_ServerImplementation bind(Registry registry, String name) throws AlreadyBoundException, RemoteException {
        Client_ServerImplementation obj = new Client_ServerImplementation();
        registry.bind(name, obj);
        return obj;
    }


    public static Client_ServerImplementation Node_client(String ip, int port) throws RemoteException{
        try {
            Registry registry = LocateRegistry.getRegistry(ip,port);
            Client_ServerImplementation node = new Client_ServerImplementation();
            String port_string = String.valueOf(port);
            Client_ServerInterface stub = (Client_ServerInterface) registry.lookup(port_string);
            stub.registerNode(node);

            System.out.println("Node registered");
            return node;
        } catch (RemoteException e) {
            System.err.println("remote exception: " + e.toString()); e.printStackTrace();
        } catch (Exception e){
            System.err.println("client exception: " + e.toString()); e.printStackTrace();
        }
        return null;
    }


    public static File folder(String dir){
        return new File(dir);
    }


    public static ArrayList get_Top(Client_ServerInterface node) throws RemoteException, NotBoundException {
        ArrayList<ArrayList<HashMap>> all_files = new ArrayList<>();
        if(node.get_connected()){
            Registry registry = LocateRegistry.getRegistry(node.getIp(), node.getPort());
            String port_connection = String.valueOf(node.getPort());
            node = (Client_ServerInterface) registry.lookup(port_connection);
            get_Top(node);
        }else{
            all_files=get_files(node,all_files);
        }
        return all_files;
    }

    public static ArrayList<ArrayList<HashMap>> get_files(Client_ServerInterface node, ArrayList files) throws RemoteException{
        if(node.Node_clients().size()==0){
            ArrayList file= node.get_flags();
            files.add(file);
        }else{
            ArrayList<Client_ServerInterface> Nodes_clients=node.Node_clients();
            for(Client_ServerInterface client :Nodes_clients){
                client=client.getBind();
                get_files(client,files);
            }
            files.add(node.get_flags());
        }
        return files;
    }

}
