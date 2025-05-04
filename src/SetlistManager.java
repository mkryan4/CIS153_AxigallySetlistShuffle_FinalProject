import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Manages the master list of songs (songPool) and the current concert setlist.
 * Handles operations like loading songs from file, adding to setlist,
 * clearing, sorting, and computing total runtime.
 */
public class SetlistManager {
    // Maintains the full collection of songs loaded from the CSV
    // LinkedHashMap is used to preserve insertion order for GUI display
    private LinkedHashMap<String, Song> songPool = new LinkedHashMap<>();

    // The current working setlist (ordered LinkedList)
    private LinkedList<Song> setlist = new LinkedList<>();

    /**
     * Adds a Song object to the song pool.
     * Songs are keyed by title for quick lookup.
     */
    public void addSong(Song song) {
        songPool.put(song.getTitle(), song);
    }

    /**
     * Adds a song from the pool to the active setlist by title.
     * Skips if the title is not found.
     */
    public void addToSetlist(String title) {
        Song song = songPool.get(title);
        if (song != null) {
            setlist.add(song);
        }
    }

    /**
     * Clears all songs from the current setlist.
     */
    public void clearSetlist() {
        setlist.clear();
    }

    /**
     * Returns the full song pool (used by GUI).
     */
    public LinkedHashMap<String, Song> getSongPool() {
        return songPool;
    }

    /**
     * Returns the current setlist (used by GUI).
     */
    public LinkedList<Song> getSetlist() {
        return setlist;
    }

    /**
     * Calculates the total duration of all songs in the setlist in seconds.
     */
    public int getTotalDuration() {
        int total = 0;
        for (Song song : setlist) {
            total += song.getDurationSeconds();
        }
        return total;
    }

    /**
     * Sorts the setlist in ascending order of BPM using insertion sort.
     */
    public void sortSetlistByBpm() {
        for (int i = 1; i < setlist.size(); i++) {
            Song key = setlist.get(i);
            int j = i - 1;

            while (j >= 0 && setlist.get(j).getBpm() > key.getBpm()) {
                setlist.set(j + 1, setlist.get(j));
                j = j - 1;
            }

            setlist.set(j + 1, key);
        }
    }

    /**
     * Loads songs from a CSV file and adds them to the song pool.
     * Each line must have at least 11 fields (title through notes).
     */
    public void loadSongsFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                // Only parse lines with 11+ fields
                if (parts.length >= 11) {
                    String title = parts[0].trim();
                    int indexNumber = 0; // Index not currently used for logic

                    int bpm = Integer.parseInt(parts[2].trim());

                    // Convert mm:ss string to total seconds
                    String[] timeParts = parts[3].trim().split(":");
                    int minutes = Integer.parseInt(timeParts[0]);
                    int seconds = Integer.parseInt(timeParts[1]);
                    int durationSeconds = (minutes * 60) + seconds;

                    String key = parts[4].trim();
                    int danceability = Integer.parseInt(parts[5].trim());
                    int happy = Integer.parseInt(parts[6].trim());
                    int sad = Integer.parseInt(parts[7].trim());
                    int relaxed = Integer.parseInt(parts[8].trim());
                    int aggressiveness = Integer.parseInt(parts[9].trim());
                    String notes = (parts.length >= 11) ? parts[10].trim() : "";

                    // Create and add song to pool
                    Song song = new Song(title, indexNumber, bpm, durationSeconds, key,
                            danceability, happy, sad, relaxed, aggressiveness, notes);
                    addSong(song);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error parsing file: " + e.getMessage());
        }
    }
}