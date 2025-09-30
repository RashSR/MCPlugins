package dna;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.logger.Logman;
import net.canarymod.api.world.blocks.BlockType;

public class FileLoader extends EZPlugin{
  private File file; //gibt die Datei des zu ladenden Objekts zurück
  private ArrayList<String> ar;//Ausgelesene Daten der Datei
  //Konstruktor überprüft ob der Pfad verfügbar ist
  public FileLoader(String fileName) {
    File file = new File(fileName);
    if(file.exists()) {
      this.file = file;
      logger.info("[FileLoader] File vorhanden!");
      String eventName=this.readFile().get(0);
      logger.info("[FileLoader] eventName="+eventName);
      if(eventName.equalsIgnoreCase("halloween")){
        logger.info("[FileLoader] Event HALLOWEEN erkannt!");
        DNA.setBlockType(BlockType.JackOLantern, BlockType.SoulSand, BlockType.SpiderWeb);
      }else if(eventName.equalsIgnoreCase("christmas")){
        logger.info("[FileLoader] Event CHRISTMAS erkannt");
        DNA.setBlockType(BlockType.LapisBlock, BlockType.GoldBlock, BlockType.DiamondBlock);
      }else{
        DNA.setBlockType(BlockType.AcaciaLog, BlockType.AcaciaLeaves, BlockType.RedstoneBlock);
      }
    }else {
      logger.info("[FileLoader] File existiert nicht!");
      this.file = null;
    }
  }
  //Liest die Datei zeilenweise und speichert jede Zeile in einer String ArrayList
  public ArrayList<String> readFile() {
    Scanner sc;
    ar = new ArrayList<>();
    try {
      if(file==null){
        logger.info("[FileLoader] File nicht lesbar!");
        return null;
      }
      sc = new Scanner(this.file);
      while(sc.hasNext()) {
        String sr = sc.nextLine();
        ar.add(sr);
      }
      sc.close();
      return ar;
      
    } catch (FileNotFoundException e) {
      logger.info("[FileLoader] FILE NOT FOUND EXCEPTION");
    }
    return null;
  }
}