import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServidorXat {

    private final int PORT = 9999;
    private final String HOST = "localhost";
    private final String MSG_SORTIR = "sortir";
    private ServerSocket serverSocket;
    private Socket clientSocket;
    
    public void iniciarServidor() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (Exception e) {
            System.err.println("Error al conectar-se: " + e.getMessage());
        }
    }

    public void pararServidor() {
        try {
            serverSocket.close();
            System.out.println("Servidor aturat");
        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
        }
    }

    public String getNom(ObjectInputStream input) throws IOException, ClassNotFoundException {
        return (String) input.readObject();
    }
    
    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.iniciarServidor();
        try {
            servidor.clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + servidor.clientSocket.getInetAddress());

            ObjectOutputStream sortida = new ObjectOutputStream(servidor.clientSocket.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(servidor.clientSocket.getInputStream());

            String nomClient = (String) entrada.readObject();
            System.out.println("Nom rebut: " + nomClient);

            FilServidorXat fil = new FilServidorXat(entrada);
            fil.start();
            System.out.println("Fil de xat creat");
            System.out.println("Fil de " + nomClient + " iniciat");
            Scanner scanner = new Scanner(System.in);
            String missatge;
            while (!(missatge = scanner.nextLine()).equalsIgnoreCase(servidor.MSG_SORTIR)) {
                sortida.writeObject(missatge);
                sortida.flush();
            }
            sortida.writeObject("sortir");
            sortida.flush();
            
            fil.join();
            scanner.close();
            servidor.clientSocket.close();
            servidor.pararServidor();

        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
        }
    }
    
}
