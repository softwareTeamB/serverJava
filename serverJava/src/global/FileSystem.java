package global;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Package om fileSystem voor write en read data
 *
 * @author michel
 */
public class FileSystem {

    /**
     * Read het bestand
     *
     * @param naamBestand Naam van het bestand
     * @return betand inhoud
     * @throws IOException als er een error is
     */
    public String readFileConfig(String naamBestand) throws IOException {
        return new String(Files.readAllBytes(Paths.get("config/" + naamBestand)), StandardCharsets.UTF_8);
    }

    /**
     * Bestand write
     *
     * @param fileName bestand naam
     * @param data data die in het bestand geschreven moet worden
     * @throws IOException
     */
    public void whriteFile(String fileName, String data) throws IOException {

        //close the buffering
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

        //writter
        bw.write(data);

        //close the writer
        bw.close();
    }

}
