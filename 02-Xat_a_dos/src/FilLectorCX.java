import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class FilLectorCX extends Thread{
    
    private ObjectOutputStream sortida;

    public FilLectorCX(ObjectOutputStream sortida) {
        this.sortida = sortida;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Missatge('sortir' per tancar):Fil de lectura iniciat");
        try {
            String missatge;
            while (!(missatge = scanner.nextLine()).equalsIgnoreCase("sortir")) {
                System.out.println("Enviant missatge:" + missatge);
                sortida.writeObject(missatge);
                sortida.flush();
            }
            System.out.println("Enviant missatge:sortir");
            sortida.writeObject("Sortir");
            sortida.flush();
        } catch (IOException e) {
            System.err.println("Error al enviar missatge:" + e.getMessage());
        } finally {
            scanner.close(); 
        }
    }
}
