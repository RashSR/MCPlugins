package events;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.position.Location;

public class BlockLocationLoader extends EZPlugin{

    // Loads or creates the file if missing
    public static Map<String, ArrayList<Location>> load(String path) {
        File file = new File(path);

        // Auto-create with defaults
        if (!file.exists()) {
            createDefaultFile(file);
        }

        Map<String, ArrayList<Location>> blockMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // skip comments

                String[] parts = line.split("\\s+");
                if (parts.length == 4) {
                    String type = parts[0];
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    int z = Integer.parseInt(parts[3]);

                    blockMap.computeIfAbsent(type, k -> new ArrayList<>())
                            .add(new Location(x, y, z));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("[BlockLocationLoader] Loaded: " + path);
        return blockMap;
    }

    // Creates a default file with example data
    private static void createDefaultFile(File file) {
        try {
            file.getParentFile().mkdirs();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("# Example block locations file\n");
                writer.write("# Format: <blockType> <x> <y> <z>\n");
                writer.write("snow 252 71 263\n");
                writer.write("snow 251 71 263\n");
                writer.write("redstone 251 74 263\n");
                writer.write("gold 251 73 261\n");
            }
            logger.info("[BlockLocationLoader] Created default block_locations.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
