import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Servidor {

    private final int PORT = 9999;
    private final String HOST = "localhost";
    private final String MSG_SORTIR = "sortir";
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void connectar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (Exception e) {
            System.err.println("Error al conectar-se: " + e.getMessage());
        }
    }

    public void tancarConnexio() {
        try {
            serverSocket.close();
            System.out.println("Servidor aturat");
        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
        }
    }

    public void enviarFitxers(Socket socket) {
        try {
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream sortida = new ObjectOutputStream(socket.getOutputStream());

            String nomFitxer = (String) entrada.readObject();
            System.out.println("Rebent sol·licitud del fitxer: " + nomFitxer);
            
            Fitxer fitxer = new Fitxer(nomFitxer);
            byte[] contingut = fitxer.getContingut();

            
            sortida.writeObject(contingut);
            sortida.flush();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Servidor server = new Servidor();

        try (ServerSocket serverSocket = new ServerSocket(server.PORT)) {
            System.out.println("Servidor escoltant al port " + server.PORT + "...");

            Socket socket = serverSocket.accept();
            System.out.println("Client connectat!");

            server.enviarFitxers(socket);
            server.tancarConnexio();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
