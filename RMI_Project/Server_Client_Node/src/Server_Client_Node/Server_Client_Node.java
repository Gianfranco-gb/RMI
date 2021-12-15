package Server_Client_Node;

import common.Client_ServerInterface;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server_Client_Node implements Serializable {
    ArrayList<HashMap<String,String>> output = new ArrayList<>();
    int key;
    ArrayList<HashMap<String, ArrayList<Client_ServerInterface>>> files_to_download = new ArrayList<>();
    HashMap<Client_ServerInterface, HashMap<String,HashMap<String,String>>> node_with_file = new HashMap<Client_ServerInterface, HashMap<String,HashMap<String,String>>>();
    Client_ServerImplementation node_server;
    public Server_Client_Node() throws RemoteException{

    }
    public static void main(String args[]) throws IOException, AlreadyBoundException, NotBoundException {
        Scanner scanner = new Scanner(System.in).useDelimiter("\n");
        Server_Client_Node node_node = new Server_Client_Node();
        System.out.println("Desea crear un nodo conectandose a una IP y PUERTO ?");
        if (scanner.next().equals("Si")) {
            Client_ServerImplementation node_server = node_node.create_nodeWithIPandPort(scanner);
            while (true) {
                node_node.option(node_server);
            }
        } else {
            Client_ServerImplementation node = node_node.create_nodeWithNothing(scanner);
            System.out.println("Se ha creado correctamente.");
            //}else{
            //   System.out.println("Error al crear el nodo, por favor vuelva a ejecutar el proyecto.");
            //}

        }
    }

    public Client_ServerImplementation create_nodeWithIPandPort(Scanner scanner) throws IOException, AlreadyBoundException {
        System.out.println("Especifique la IP a conectarse: ");
        String ip = scanner.next();
        System.out.println("Especifique el PUERTO a conectarse");
        int port_connection = Integer.parseInt(scanner.next());
        Client_ServerImplementation node_client = Node_client(ip, port_connection);
        assert node_client != null;
        node_client.set_connections(ip,port_connection);
        System.out.println("Que PUERTO desea que utilize este nodo?");
        String port_server = scanner.next();
        Registry registry= Node_Server(ip,Integer.parseInt(port_server));
        Client_ServerImplementation node_server=bind(registry, port_server);
        node_server.set_connections(ip,port_connection);
        System.out.println("Que archivos analizara este nodo (Indique la ubicacion absoluta)?: ");
        String dir = scanner.next();
        node_client.saveFile(dir);
        node_client.saveBind(node_server);
        node_server.saveFile(dir);
        node_server.set_connect();
        node_client.set_flags("","","","");
        node_server.set_flags("","","","");
        this.node_server = node_server;
        return node_server;
    }


    public Client_ServerImplementation create_nodeWithNothing(Scanner scanner) throws IOException, AlreadyBoundException {
        System.out.println("Especifique la IP que tendra esta red: ");
        String ip = scanner.next();
        System.out.println("Que PUERTO desea que utilize este nodo?");
        int port = scanner.nextInt();
        Registry registry= Node_Server(ip, port);
        Client_ServerImplementation node = bind(registry, String.valueOf(port));
        node.set_connections(ip,port);
        System.out.println("Que archivos analizara este nodo (Indique la ubicacion)?: ");
        String dir = scanner.next();
        node.saveFile(dir);
        node.saveBind(node);
        node.set_flags("","","","");
        this.node_server = node;
        return node;
    }


    public synchronized void Search(Client_ServerImplementation node, String search) throws RemoteException, NotBoundException {
        this.key = 0;
        ArrayList<HashMap<String,HashMap<String,String>>> files = new ArrayList<>();
        files=get_Top(node,files);
        String hash_file;
        ArrayList<String> hash_files = new ArrayList<>();

        for(Client_ServerInterface n : this.node_with_file.keySet()) {
            for(String file_name : this.node_with_file.get(n).keySet() ){
                    String total_description = (String) this.node_with_file.get(n).get(file_name).get("Description");
                    String total_keywords = (String) this.node_with_file.get(n).get(file_name).get("Keywords");
                    String[] keywords = total_keywords.split(",");
                    String[] descriptions = total_description.split(",");
                    if (this.node_with_file.get(n).get(file_name).get("Name").equals(search)) {
                        hash_file = (String) this.node_with_file.get(n).get(file_name).get("Hash");
                        if (!hash_files.isEmpty()) {
                            if (!hash_files.contains(hash_file)) {
                                hash_files.add(hash_file);
                                this.output.add(get_same_hash(hash_file, files, n));
                            }
                        } else {
                            hash_files.add(hash_file);
                            this.output.add(get_same_hash(hash_file, files,n));
                        }
                    } else {
                        for (String key : keywords) {
                            if (key.equals(search)) {
                                hash_file = (String) this.node_with_file.get(n).get(file_name).get("Hash");
                                if (!hash_files.isEmpty()) {
                                    if (!hash_files.contains(hash_file)) {
                                        hash_files.add(hash_file);
                                        this.output.add(get_same_hash(hash_file, files,n));
                                    }
                                } else {
                                    hash_files.add(hash_file);
                                    this.output.add(get_same_hash(hash_file, files,n));
                                }
                            }
                        }
                        for (String descrip : descriptions) {
                            if (descrip.equals(search)) {
                                hash_file = (String) this.node_with_file.get(n).get(file_name).get("Hash");
                                if (!hash_files.isEmpty()) {
                                    if (!hash_files.contains(hash_file)) {
                                        hash_files.add(hash_file);
                                        this.output.add(get_same_hash(hash_file, files,n));
                                    }
                                } else {
                                    hash_files.add(hash_file);
                                    this.output.add(get_same_hash(hash_file, files,n));
                                }
                            }
                        }
                    }

            }
        }
        print_search(this.output);
    }

    public synchronized void print_search(ArrayList<HashMap<String,String>> output) throws RemoteException{
        System.out.println("\n");
        System.out.println("Resultado: ");
        for(int i = 0; i<output.size(); i++){
            for(Object key : output.get(i).keySet()) {
                    System.out.println("\t"+key + " = " + output.get(i).get(key));
            }
            System.out.println("\n");
        }
    }


    public synchronized HashMap<String, String> get_same_hash(String hash, ArrayList<HashMap<String, HashMap<String, String>>> files, Client_ServerInterface node) throws RemoteException{
        HashMap<String, String> all_same_hashes = new HashMap<>();
        ArrayList<Client_ServerInterface> matches_nodes = new ArrayList<>();
        for(int i = 0; i < files.size(); i++){
            for(Object name_files : files.get(i).keySet()){
                if(files.get(i).get(name_files).get("Hash").equals(hash)){
                    if(all_same_hashes.isEmpty()){
                        matches_nodes.add(node);
                        HashMap<String, ArrayList<Client_ServerInterface>> hash_and_nodes= new HashMap<>();
                        all_same_hashes.put("Key", String.valueOf(key+1));
                        all_same_hashes.put("Hash", hash);
                        all_same_hashes.put("Name", (String) files.get(i).get(name_files).get("Name"));
                        all_same_hashes.put("Description", (String) files.get(i).get(name_files).get("Description"));
                        all_same_hashes.put("Keywords", (String) files.get(i).get(name_files).get("Keywords"));
                        hash_and_nodes.put(hash,matches_nodes);
                        this.files_to_download.add(hash_and_nodes);
                        key++;
                    }else{
                        HashMap<String, ArrayList<Client_ServerInterface>> hash_and_nodes= new HashMap<>();
                        matches_nodes.add(node);
                        all_same_hashes.replace("Name", all_same_hashes.get("Name")+ ", "+files.get(i).get(name_files).get("Name")+ ".");
                        all_same_hashes.replace("Description", all_same_hashes.get("Description")+ ", " +files.get(i).get(name_files).get("Description")+".");
                        all_same_hashes.replace("Keywords", all_same_hashes.get("Keywords")+ ", " +files.get(i).get(name_files).get("Keywords")+".");
                        hash_and_nodes.put(hash,matches_nodes);
                        this.files_to_download.set(key-1, hash_and_nodes);
                    }
                }
            }
        }
        return all_same_hashes;
    }



    public static void Assign_flags_to_file(Client_ServerImplementation node, Scanner scan) throws IOException {
        String file_name = scan.next();
        if(check_file(file_name,node)) {
            System.out.println("Que nombre desea asignar al fichero?: ");
            String name = scan.next();
            System.out.println("Que keywords desea asignar al fichero?: ");
            String keywords = scan.next();
            System.out.println("Que descripcion desea asignar al fichero?: ");
            String description = scan.next();
            node.set_flags(file_name, name, keywords, description);
        }else{
            System.out.println("No existe este fichero en esta carpeta");
        }
    }

    public synchronized static boolean check_file(String file_name, Client_ServerImplementation node) throws RemoteException {
        String[] names=node.files_names();
        List<String> files_names =Arrays.asList(names);
        if(files_names.contains(file_name)){
            return true;
        }
        return false;
    }

    public synchronized static void modify_file(Scanner scan, Client_ServerImplementation node) throws IOException {
        System.out.println("Desea 'Eliminar', 'Modificar' o 'Añadir'?:");
        String resp = scan.next();
        if(resp.equals("Eliminar")){
            System.out.println("Escriba el fichero al que quiere eliminar (con la extensio '.txt,.pdf...'): ");
            String path_delete = scan.next();
            File file_delete = new File(node.getFile_Abspath()+"/"+path_delete);
            if(file_delete.delete()){
                System.out.println("Exito al eliminar el fichero");
                node.update_files_node(path_delete, "","Eliminar");
            }else{
                System.out.println("Error al eliminar el fichero, compruebe si existe en esta carpeta");
            }
        }else if(resp.equals("Modificar")){
            System.out.println("Escriba el nombre del archivo (con la extensio '.txt,.pdf...'): ");
            String path = scan.next();
            File old_name = new File(node.getFile_Abspath()+"/"+path);
            System.out.println("Escriba el nombre nuevo (con la extension): ");
            String new_path = scan.next();
            File new_name = new File(node.getFile_Abspath()+"/"+new_path);
            if(old_name.renameTo(new_name)){
                System.out.println("Exito al cambiar el nombre");
                node.update_files_node(path,new_path,"Modificar");
            }else{
                System.out.println("Error al cambiar el nombre");
            }
        }else if(resp.equals("Añadir")){
            System.out.println("Escriba la ruta de donde se localiza el fichero con el nombre fichero mismo" +
                    " a añadir + la extension ('.txt,.pdf...'): ");
            String new_file_path = scan.next();
            File new_file = new File(new_file_path);
            String[] name_file = new_file_path.split("/");
            if(new_file.renameTo(new File(node.getFile_Abspath()+"/"+name_file[name_file.length-1]))){
                System.out.println("Exito al añadir");
                node.update_files_node("",name_file[name_file.length-1],"Añadir");
            }else{
                System.out.println("Error al añadir");
            }
        }

    }

    public synchronized void download(String number,Scanner scan) throws IOException, NotBoundException {
        int numb = Integer.parseInt(number);
        HashMap<String, ArrayList<Client_ServerInterface>> map_to_get_nodes =this.files_to_download.get(numb-1);
        ArrayList<Client_ServerInterface> nodes_to_get_file = null;
        String hash_file = null;
        for(String h : map_to_get_nodes.keySet()){
            nodes_to_get_file=map_to_get_nodes.get(h);
            hash_file = h;
        }

        assert nodes_to_get_file != null;
        Client_ServerInterface node = nodes_to_get_file.get(0);


        String folder = "carpeta_temp_files/";
        File dir = new File(folder);

        if (!dir.exists()) {
            if (!dir.mkdir()) {
                System.out.println("No se puede crear la carpeta");
            }
        }
        Client_ServerImplementation.Download download_request_node = new Client_ServerImplementation.Download(node,0,
                hash_file, folder, 0);

        System.out.println("Cuantos threads desea utilizar para la descarga?: ");
        int number_threads = scan.nextInt();
        ThreadPoolExecutor threadpool = (ThreadPoolExecutor) Executors.newFixedThreadPool(number_threads);
        ArrayList<Client_ServerInterface> nodes = map_to_get_nodes.get(hash_file);
        File file_to_download = download_request_node.get_file_to_download();
        URI file_Uri = file_to_download.toURI();
        URL file_Url = file_Uri.toURL();
        String file_url_string = file_Url.toString();

        try {
            URLConnection conn = new URL(file_url_string).openConnection();
            conn.connect();
            InputStream in = conn.getInputStream();
            int size = node.get_size(hash_file, folder);
            int get_n_works = size;
            int number_works = 0;
            while(get_n_works >= 1000000){
                number_works++;
                get_n_works = get_n_works -1000000;
            }
            if(get_n_works > 0){
                number_works++;
            }
            int skip = 0;
            int reset = 0;
            int total_nodes = nodes.size();
            for(int i = 0; i< number_works; i++) {
                if(i == 0){
                    Runnable work = new Client_ServerImplementation.Download(nodes.get(reset), skip, hash_file, folder, i);
                    threadpool.execute(work);
                    reset ++;
                } else {

                    skip = skip + 1000000;
                    if((i % total_nodes) == 0){
                        reset = 0;
                    }
                    Runnable work = new Client_ServerImplementation.Download(nodes.get(reset), skip, hash_file, folder, i );
                    threadpool.execute(work);
                    reset++;
                }
            }
            threadpool.getCompletedTaskCount();
            while(threadpool.getCompletedTaskCount()<number_works ){
            }
            threadpool.shutdown();
            while(threadpool.isTerminating() && !threadpool.isTerminated()){
            }
            in.close();

        } catch (MalformedURLException e) {
            System.out.println("La url: " + file_url_string + " no es valida!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        joinFiles(dir);
    }

    public static void joinFiles(File dir_temp_files) throws IOException {
        Scanner scan = new Scanner(System.in).useDelimiter("\n");
        System.out.println("Como quieres que se llame la carpeta de descargas?: ");
        String name_dir = scan.next();
        File dir = new File(name_dir);
        System.out.println("Como quieres que se llame el archivo de la descarga? (con la extensio '.txt,.pdf...'): ");
        String name_download = scan.next();
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                System.out.println("No se puede crear la carpeta");
            }
        }
        File destination = new File (name_dir+"/"+name_download);
        File[] files_temp = dir_temp_files.listFiles();
        OutputStream output= null;
        try{
            output= createAppendableStream(destination);
            assert files_temp != null;
            for(File source : files_temp){
                TimeUnit.SECONDS.sleep(2);
                appendFile(output,source);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
        }
        output.close();
        delete_temp_files(dir_temp_files);
    }

    public static BufferedOutputStream createAppendableStream(File destination) throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(destination, true));
    }

    public static void appendFile(OutputStream output, File source) throws IOException {
        InputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(source));
            IOUtils.copy(input, output);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public static void delete_temp_files(File folder){
        File dir_el = new File(folder.getAbsolutePath());
        String[]name_files = dir_el.list();
        for(int i = 0; i< Objects.requireNonNull(name_files).length; i++){
            File file_to_delete = new File(dir_el.getAbsolutePath()+"/"+name_files[i]);
            if(file_to_delete.delete()){
                System.out.println("Se ha eliminado correctamente el fichero temporal");
            }else{
                System.out.println("Error al eliminar fichero temporal");
            }
        }
        if(dir_el.delete()){
            System.out.println("Se ha eliminado correctamente la carpeta de ficheros temporales");
        }else{
            System.out.println("Error al eliminar la carpeta de ficheros temporales");
        }
    }

    public String option(Client_ServerImplementation node) throws IOException, NotBoundException {

        Scanner scan = new Scanner(System.in).useDelimiter("\n");;
        System.out.println("Que desea hacer? :");
        System.out.println("1 --> Search todos los nodos");
        System.out.println("2 --> Descargar");
        System.out.println("3 --> Modificar un archivo");
        System.out.println("4 --> Asignar 'Name/Keywords/Descriptions' a un fichero");
        String resp = scan.next();
        if(resp.equals("1")) {
            System.out.println("Escriba que desea buscar (Title/Description/Keywords): ");
            String search = scan.next();
            Search(node, search);
        }else if(resp.equals("2")){
            System.out.println("Escriba que numero desea descargar: ");
            String number = scan.next();
            download(number,scan);
        }else if(resp.equals("3")){
            modify_file(scan,node);
        }else if(resp.equals("4")){
            System.out.println("Escribe el filename del fichero con la extension (.pdf,.txt ...): ");
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


    public ArrayList<HashMap<String,HashMap<String,String>>> get_Top(Client_ServerInterface node, ArrayList all_files) throws RemoteException, NotBoundException {
        if(node.get_connected()){
            Registry registry = LocateRegistry.getRegistry(node.getIp(), node.getPort());
            String port_connection = String.valueOf(node.getPort());
            node = (Client_ServerInterface) registry.lookup(port_connection);
            get_Top(node,all_files);
        }else{
            all_files=get_files(node,all_files);
        }
        return all_files;
    }

    public ArrayList<HashMap<String,HashMap<String,String>>> get_files(Client_ServerInterface node, ArrayList files) throws RemoteException{
        if(node.Node_clients().size()==0){
            HashMap<String, HashMap<String,String>> file= node.get_flags();
            this.node_with_file.put(node, file);
            files.add(file);
        }else{
            ArrayList<Client_ServerInterface> Nodes_clients= node.Node_clients();
            for(Client_ServerInterface client :Nodes_clients){
                client=client.getBind();
                get_files(client,files);
            }
            this.node_with_file.put(node, node.get_flags());
            files.add(node.get_flags());
        }
        return files;
    }


    static class IOUtils {
        private static final int BUFFER_SIZE = 1024 * 4;

        public static long copy(InputStream input, OutputStream output)
                throws IOException {
            byte[] buffer = new byte[BUFFER_SIZE];
            long count = 0;
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        }

        public static void closeQuietly(Closeable output) {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }


}
