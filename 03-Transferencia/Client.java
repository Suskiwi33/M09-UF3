import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;

public class Client {
    private static final String DIR_ARRIBADA = "C:/tmp";
    private Socket socket;
    private ObjectInputStream entrada;
    private ObjectOutputStream sortida;


    public void connectar() {
        try {
            socket = new Socket("localhost", 9999);
            sortida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            System.out.println("Client connectat a localhost:9999");
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
        }
    }

    public void rebreFitxers() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Nom complet del fitxer a rebre: ");
            String nomFitxer = scanner.nextLine();

            
            sortida.writeObject(nomFitxer);
            sortida.flush();

            
            byte[] contingut = (byte[]) entrada.readObject();

            
            File dir = new File(DIR_ARRIBADA);
            if (!dir.exists()) dir.mkdirs();
    
            
            File outputFile = new File(DIR_ARRIBADA, new File(nomFitxer).getName());
            Files.write(outputFile.toPath(), contingut);
    
            System.out.println("Fitxer rebut i guardat a: " + outputFile.getAbsolutePath());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void tancarClient() {
        try {
            socket.close();
            sortida.close();
            entrada.close();
            System.out.println("Tancant client...");
            System.out.println("Client tancat");
        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
        }
    }



    public static void main(String[] args) {

        Client client = new Client();

        try {
            client.connectar();
            client.rebreFitxers();
        } finally {
            client.tancarClient();
        }
    }

}
