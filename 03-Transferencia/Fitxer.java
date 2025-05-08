import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Fitxer {
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
    }

    public byte[] getContingut() throws IOException {
        File file = new File(nom);
        if (!file.exists()) {
            throw new IOException("El fitxer no existeix: " + nom);
        }
        contingut = Files.readAllBytes(Paths.get(nom));
        return contingut;
    }

    public String getNom() {
        return nom;
    }
}

