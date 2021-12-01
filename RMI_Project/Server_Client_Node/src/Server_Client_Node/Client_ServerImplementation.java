package Server_Client_Node;

import common.Client_ServerInterface;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Client_ServerImplementation extends UnicastRemoteObject implements Client_ServerInterface {
    File file;
    ArrayList<Client_ServerInterface> Node_Register = new ArrayList<>();
    boolean connected = false;
    String ip;
    int port;
    Client_ServerInterface bind;
    ArrayList<String[]> flags = new ArrayList<>();
    HashMap<String, String> dic_flags = new HashMap<>();
    ArrayList<HashMap> list_maps = new ArrayList<>();


    public Client_ServerImplementation() throws RemoteException {
        super();
    }

    public void saveFile(File file) throws RemoteException {
        this.file = file;
    }

    public String[] getFile() throws RemoteException {
        return this.file.list();
    }

    public void registerNode(Client_ServerInterface Node) throws RemoteException {
        this.Node_Register.add(Node);
    }

    public void set_connect() throws RemoteException {
        this.connected = true;
    }

    public boolean get_connected() throws RemoteException {
        return this.connected;
    }

    public void set_connections(String ip, int port) throws RemoteException {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() throws RemoteException {
        return this.ip;
    }

    public int getPort() throws RemoteException {
        return this.port;
    }

    public ArrayList Node_clients() throws RemoteException {
        return this.Node_Register;
    }

    public void saveBind(Client_ServerInterface bind) throws RemoteException {
        this.bind = bind;
    }

    public Client_ServerInterface getBind() throws RemoteException {
        return this.bind;
    }

    public void set_flags2(String file_name, String name, String keywords, String description) throws RemoteException {
        String[] files = this.getFile();

        for (int i = 0; i < files.length;i++) {
            String[] def_flags = new String[3];
            if (Objects.equals(file_name, "")) {
                //String[] def_flags = new String[3];
                def_flags[0] = files[i];
                def_flags[1] = keywords;
                def_flags[2] = description;
                this.flags.add(def_flags);
            } else {
                if(file_name.equals(files[i])){
                    String[] flags=this.flags.get(i);
                    flags[0] = name;
                    flags[1] = keywords;
                    flags[2] = description;
                    this.flags.set(i,flags);
                }
            }
        }
    }

    public int Hash_files(File file) throws IOException {
        FileInputStream f = new FileInputStream(file);
        DataInputStream data_file = new DataInputStream(f);
        int length_file = data_file.available();
        byte[] bytes_list = new byte[length_file];
        data_file.readFully(bytes_list);
        StringBuilder bytes_coded = new StringBuilder();
        for (byte by : bytes_list) {
            bytes_coded.append(by);
        }
        String string_bytes = bytes_coded.toString();
        return string_bytes.hashCode();
    }

    public ArrayList get_flags() throws RemoteException{
        return this.list_maps;
    }


    public void set_flags(String file_name, String name, String keywords, String description) throws IOException, RemoteException {
        File[] files = this.file.listFiles();
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            if (Objects.equals(file_name, "")) {
                int hash_file =Hash_files(files[i]);
                this.dic_flags.put("Hash", String.valueOf(hash_file));
                this.dic_flags.put("Name", files[i].getName());
                this.dic_flags.put("Keywords", keywords);
                this.dic_flags.put("Description", description);
                this.list_maps.add(this.dic_flags);
            }else{
                if(file_name.equals(files[i].getName())){
                    this.dic_flags.put("Hash", String.valueOf(list_maps.get(i).get("Hash")));
                    this.dic_flags.put("Name", name);
                    this.dic_flags.put("Keywords", keywords);
                    this.dic_flags.put("Description", description);
                    this.list_maps.set(i,this.dic_flags);
                }
            }
        }
    }
}
