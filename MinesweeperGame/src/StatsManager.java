import java.io.*;
import java.util.*;

public class StatsManager {
    private static final String FILE_NAME = "stats.txt";

    public static Map<String, Integer> loadStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("wins", 0);
        stats.put("losses", 0);

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    stats.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Stats file not found. Creating a new one.");
        }

        return stats;
    }

    public static void saveStats(Map<String, Integer> stats) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving stats: " + e.getMessage());
        }
    }
}
