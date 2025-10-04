package events;
import java.io.File;
import com.pragprog.ahmine.ez.EZPlugin;
import java.io.*;

public class OwnFileWriter extends EZPlugin{
  private File file;

  public OwnFileWriter(String fileName, String text){
    File file = new File(fileName);
    try{
      if (file.createNewFile()){
        logger.info("[FileLoader] File wurde erstellt!");
      }else {
        logger.info("[FileWriter] File bereits vorhanden!");
      }
      FileWriter fw = new FileWriter(fileName);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(text);
      bw.close();
    }catch(Exception e){
      logger.info("[FileWriter] Unhandled IO-Exception.");
    }
  }

}