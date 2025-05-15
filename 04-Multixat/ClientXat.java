import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean sortir = false;

    public void connecta() {
        try {
            socket = new Socket("localhost", 9999);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            System.out.println("Client connectat a localhost:9999");
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            System.err.println("Error connectant: " + e.getMessage());
        }
    }

    public void enviarMissatge(String missatge) {
        try {
            oos.writeObject(missatge);
            oos.flush();
        } catch (IOException e) {
            System.err.println("Error enviant missatge.");
        }
    }

    public void tancarClient() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
            System.out.println("Tancant client...");
        } catch (IOException e) {
            System.err.println("Error tancant client.");
        }
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("  1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("  2.- Enviar missatge personal");
        System.out.println("  3.- Enviar missatge al grup");
        System.out.println("  4.- (o línia en blanc)-> Sortir del client");
        System.out.println("  5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public String getLinea(Scanner sc, String missatge, boolean obligatori) {
        String linia;
        do {
            System.out.print(missatge);
            linia = sc.nextLine();
        } while (obligatori && linia.isEmpty());
        return linia;
    }

    public void run() {
        connecta();

        Thread t = new Thread(() -> {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                System.out.println("DEBUG: Iniciant rebuda de missatges...");
                while (!sortir) {
                    String missatge = (String) ois.readObject();
                    String codi = Missatge.getCodiMissatge(missatge);
                    String[] parts = Missatge.getPartsMissatge(missatge);
                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            sortir = true;
                            break;
                        case Missatge.CODI_MSG_PERSONAL:
                            System.out.println("Missatge de (" + parts[1] + "): " + parts[2]);
                            break;
                        case Missatge.CODI_MSG_GRUP:
                            System.out.println(parts[1]);
                            break;
                        default:
                            System.err.println("Error: codi desconegut");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error rebent missatge. Sortint...");
            }
        });
        t.start();

        Scanner sc = new Scanner(System.in);
        ajuda();

        while (!sortir) {
            String opcio = sc.nextLine();
            switch (opcio) {
                case "1":
                    String nom = getLinea(sc, "Introdueix el nom: ", true);
                    enviarMissatge(Missatge.getMissatgeConectar(nom));
                    break;
                case "2":
                    String dest = getLinea(sc, "Destinatari:: ", true);
                    String text = getLinea(sc, "Missatge a enviar: ", true);
                    enviarMissatge(Missatge.getMissatgePersonal(dest, text));
                    break;
                case "3":
                    String grup = getLinea(sc, "Missatge al grup: ", true);
                    enviarMissatge(Missatge.getMissatgeGrup(grup));
                    break;
                case "4":
                case "":
                    enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                    sortir = true;
                    break;
                case "5":
                    enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                    sortir = true;
                    break;
                default:
                    System.out.println("Opció desconeguda");
            }
        }

        tancarClient();
    }

    public static void main(String[] args) {
        new ClientXat().run();
    }
}

