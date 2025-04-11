import java.io.*;
import java.net.*;

public class Client {
    private final int PORT = 7777;
    private final String HOST = "localhost";
    private Socket socket;
    private PrintWriter out;

    public void connecta() throws IOException {
        socket = new Socket(HOST, PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connectat a servidor en " + HOST + ":" + PORT);
    }

    public void envia(String missatge) {
        out.println(missatge);
        System.out.println("Enviat al servidor: " + missatge);
    }

    public void tanca() throws IOException {
        out.close();
        socket.close();
        System.out.println("Client tancat");
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.connecta();
            client.envia("Prova d'enviament 1");
            client.envia("Prova d'enviament 2");
            client.envia("Adéu!");
            System.out.println("Prem Enter per tancar el client...");
            System.in.read();
            client.tanca();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}