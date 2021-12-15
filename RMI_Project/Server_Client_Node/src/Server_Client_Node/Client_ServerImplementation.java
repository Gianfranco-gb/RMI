package Server_Client_Node;

import common.Client_ServerInterface;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class Client_ServerImplementation extends UnicastRemoteObject implements Client_ServerInterface, Serializable {
    File file;
    ArrayList<Client_ServerInterface> Node_Register = new ArrayList<>();
    boolean connected = false;
    String ip;
    int port;
    Client_ServerInterface bind;
    HashMap<String,HashMap<String,String>> dict_of_dict_files = new HashMap<>();
    File[] all_files;
    File file_to_download;

    public Client_ServerImplementation() throws RemoteException {
        super();
    }

    public void saveFile(String file_str) throws RemoteException {
        File file = new File(file_str);
        this.file = file;
        saveFileList();
    }

    public void saveFileList()throws RemoteException{
        this.all_files = this.file.listFiles();
    }

    public File[] getAll_files() throws RemoteException{
        return this.all_files;
    }

    public File getFile() throws RemoteException{
        return this.file;
    }

    public String getFile_Abspath() throws RemoteException {
        return this.file.getAbsolutePath();
    }

    public synchronized void registerNode(Client_ServerInterface Node) throws RemoteException {
        this.Node_Register.add(Node);
    }

    public synchronized void set_connect() throws RemoteException {
        this.connected = true;
    }

    public synchronized boolean get_connected() throws RemoteException {
        return this.connected;
    }

    public synchronized void set_connections(String ip, int port) throws RemoteException {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() throws RemoteException {
        return this.ip;
    }

    public int getPort() throws RemoteException {
        return this.port;
    }

    public ArrayList<Client_ServerInterface> Node_clients() throws RemoteException {
        return this.Node_Register;
    }

    public void saveBind(Client_ServerInterface bind) throws RemoteException {
        this.bind = bind;
    }

    public Client_ServerInterface getBind() throws RemoteException {
        return this.bind;
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
        f.close();
        data_file.close();
        return string_bytes.hashCode();
    }

    public HashMap<String, HashMap<String, String>> get_flags() throws RemoteException{
        return this.dict_of_dict_files;
    }

    public String[] files_names() throws RemoteException{
        return this.file.list();
    }


    public void set_flags(String file_name, String name, String keywords, String description) throws IOException, RemoteException {
        if (Objects.equals(name, "")) {
            File[] files = this.file.listFiles();
            for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
                HashMap<String, String> file_map = new HashMap<String, String>();
                int hash_file = Hash_files(files[i]);
                file_map.put("File_name", files[i].getName());
                file_map.put("Hash", String.valueOf(hash_file));
                file_map.put("Name", files[i].getName());
                file_map.put("Keywords", keywords);
                file_map.put("Description", description);
                this.dict_of_dict_files.put(file_map.get("File_name"), file_map);
            }
        }else{
            HashMap <String, String> file_map = this.dict_of_dict_files.get(file_name);
            file_map.replace("Name", name);
            file_map.replace("Keywords", keywords);
            file_map.replace("Description", description);
        }
    }




    public void update_files_node(String name_to_access,String new_name_file, String modify) throws IOException {
        if(modify.equals("Modificar")){
            HashMap<String,String> change=this.dict_of_dict_files.get(name_to_access);
            if(change.get("Name").equals(name_to_access)){
                change.replace("Name", new_name_file);
            }
            change.replace("File_name", new_name_file);

        }else if(modify.equals("Eliminar")){
            this.dict_of_dict_files.remove(name_to_access);

        }else if(modify.equals("Añadir")){
            File new_file_to_add = new File(this.getFile()+"/"+new_name_file);
            int hash =Hash_files(new_file_to_add);
            HashMap<String,String> new_file = new HashMap<>();
            new_file.put("File_name", new_name_file);
            new_file.put("Hash",String.valueOf(hash));
            new_file.put("Name", new_name_file);
            new_file.put("Keywords","");
            new_file.put("Description", "");
            this.dict_of_dict_files.put(new_name_file,new_file);
        }
    }

    public int get_size(String hash_file, String folder) throws IOException {
        Client_ServerImplementation.Download download_request_node = new Client_ServerImplementation.Download(this,0,
                hash_file, folder, 0);
        File f=download_request_node.get_file_to_download();
        this.file_to_download = f;
        URI file_Uri = f.toURI();
        URL file_Url = file_Uri.toURL();
        String file_url_string = file_Url.toString();
        URLConnection conn = new URL(file_url_string).openConnection();
        conn.connect();
        InputStream in = conn.getInputStream();
        int size =in.available();
        in.close();
        return size;
    }


    public File getFile_to_download() throws RemoteException{
        return this.file_to_download;
    }


    public static class Download implements Runnable, Serializable{

        Client_ServerInterface node;
        int skip;
        String hash_file;
        File file_to_download;
        String folder;
        int num_temp_file;


        public Download(Client_ServerInterface node, int skip, String hash_file, String folder, int num_temp_file) throws RemoteException {
            this.node = node;
            this.skip = skip;
            this.hash_file = hash_file;
            File file_to_download = get_file_to_download();
            this.file_to_download = file_to_download;
            this.folder = folder;
            this.num_temp_file = num_temp_file;

        }

        public File get_file_to_download() throws RemoteException {
            File[] files = this.node.getAll_files();
            String[] files_names=this.node.files_names();
            HashMap<String, HashMap<String,String>> node_dict=this.node.get_flags();

            for(String file_name_dic : node_dict.keySet()){
                if(node_dict.get(file_name_dic).get("Hash").equals(this.hash_file)) {
                    for(int i = 0; i < files_names.length; i++){
                        if (files_names[i].equals(file_name_dic)) {
                            assert files != null;
                            return files[i];
                        }
                    }
                }
            }
            return null;
        }
        @Override
        public void run() {
            try{
                File file = new File(this.folder + ("Temp_file_" + this.num_temp_file));
                URI file_Uri = this.node.getFile_to_download().toURI();
                URL file_Url = file_Uri.toURL();
                String file_url_string = file_Url.toString();
                URLConnection conn = new URL(file_url_string).openConnection();
                conn.connect();
                InputStream in = conn.getInputStream();
                OutputStream out = new FileOutputStream(file);


                int b = 0;
                int count = 1;
                try {
                    in.skip(this.skip);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (b != -1  && count <= 1000000) {
                    try {
                        b = in.read();
                    } catch (IOException e) {
                    e.printStackTrace();
                    }
                    count++;
                    if (b != -1 ) {
                        try {
                            out.write(b);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
