import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {

    private final int PORT = 9999;
    private final String HOST = "localhost";
    private final String MSG_SORTIR = "sortir";

    private boolean sortir = false;
    private Hashtable<String, GestorClients> clients = new Hashtable<>();
    private ServerSocket serverSocket;

    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);

            while (!sortir) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket);
                GestorClients gestor = new GestorClients(clientSocket, this);
                new Thread(gestor).start();
            }

        } catch (Exception e) {
            System.err.println("Error al conectar-se: " + e.getMessage());
        }
    }

    public void pararServidor() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error tancant servidor: " + e.getMessage());
        }
    }

    public synchronized void finalitzarXat() {
        enviarMissatgeGrup(MSG_SORTIR);
        clients.clear();
        sortir = true;
        pararServidor();
        System.out.println("Tancant tots els clients.");
        System.exit(0);
    }

    public synchronized void afegirClient(GestorClients client) {
        clients.put(client.getNom(), client);
        enviarMissatgeGrup("Entra: " + client.getNom());
        System.out.println("DEBUG: multicast Entra: " + client.getNom());
    }

    public synchronized void eliminarClient(String nom) {
        if (clients.containsKey(nom)) {
            clients.remove(nom);
        }
    }

    public synchronized void enviarMissatgeGrup(String missatge) {
    String msg = Missatge.getMissatgeGrup(missatge);
    for (GestorClients client : clients.values()) {
        try {
            client.enviarRaw(msg);
        } catch (Exception e) {
            System.err.println("Error enviant missatge al client " + client.getNom());
        }
    }
}


    public synchronized void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        GestorClients receptor = clients.get(destinatari);
        if (receptor != null) {
            receptor.enviarMissatge(remitent, missatge);
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
    }
    
}
