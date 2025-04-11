import java.io.*;
import java.net.*;

public class Servidor {
    private final int PORT = 7777;
    private final String HOST = "localhost";
    private ServerSocket srvSocket;
    private Socket clientSocket;

    public void connecta() throws IOException {
        System.out.println("Servidor en marxa a " + HOST + ":" + PORT);
        srvSocket = new ServerSocket(PORT);
        System.out.println("Esperant connexions a " + HOST + ":" + PORT);
        clientSocket = srvSocket.accept();
        System.out.println("Client connectat: " + clientSocket.getInetAddress());
    }

    public void repDades() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String missatge;
        while ((missatge = in.readLine()) != null) {
            System.out.println("Rebut: " + missatge);
        }
        in.close();
    }

    public void tanca() throws IOException {
        clientSocket.close();
        srvSocket.close();
        System.out.println("Servidor tancat.");
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try {
            servidor.connecta();
            servidor.repDades();
            servidor.tanca();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}