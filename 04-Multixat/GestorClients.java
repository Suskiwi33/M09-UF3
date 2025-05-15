import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients extends Thread{
    
    private Socket client;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket socket, ServidorXat servidor) {
        this.client = socket;
        this.servidor = servidor;
        try {
            oos = new ObjectOutputStream(client.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.err.println("Error inicialitzant client: " + e.getMessage());
        }
    }

    public String getNom() {
        return nom;
    }

    @Override
    public void run() {
        try {
            while (!sortir) {
                String missatge = (String) ois.readObject();
                processaMissatge(missatge);
            }
        } catch (Exception e) {
            System.err.println("Error rebent missatge. Sortint...");
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.err.println("Error tancant client: " + e.getMessage());
            }
        }
    }

    public void enviarMissatge(String remitent, String missatge) {
        try {
            oos.writeObject(Missatge.getMissatgePersonal(remitent, missatge));
            oos.flush();
        } catch (IOException e) {
            System.err.println("Error enviant missatge a " + nom);
        }
    }

    public void processaMissatge(String missatgeRaw) {
        String codi = Missatge.getCodiMissatge(missatgeRaw);
        String[] parts = Missatge.getPartsMissatge(missatgeRaw);

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                nom = parts[1];
                servidor.afegirClient(this);
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidor.eliminarClient(nom);
                break;
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidor.finalitzarXat();
                break;
            case Missatge.CODI_MSG_PERSONAL:
                servidor.enviarMissatgePersonal(parts[1], nom, parts[2]);
                break;
            case Missatge.CODI_MSG_GRUP:
                servidor.enviarMissatgeGrup(parts[1]);
                break;
            default:
                System.err.println("Codi desconegut: " + codi);
        }
    }

    public void enviarRaw(String missatge) throws IOException {
        oos.writeObject(missatge);
        oos.flush();
    }

}
