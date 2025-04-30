import java.io.IOException;
import java.io.ObjectInputStream;

public class FilServidorXat extends Thread {
    private ObjectInputStream entrada;

    public FilServidorXat(ObjectInputStream e) {
        this.entrada = e;
    }

    @Override
    public void run() {
        try {
            String missatge;
            while (!(missatge = (String) entrada.readObject()).equalsIgnoreCase("sortir")) {
                System.out.println("Missatge('sortir'per tancar): Rebut: " + missatge);
            }
            System.out.println("Fil de xat finalitzat");
        } catch (ClassNotFoundException | IOException e)  {
            e.printStackTrace();            
        }
    }
}
