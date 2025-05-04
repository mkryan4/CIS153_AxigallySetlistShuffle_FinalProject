import javax.swing.SwingUtilities;

/**
 * Entry point for the Axigally™ Setlist Shuffle application.
 * Initializes the SetlistManager, loads songs from the CSV,
 * and launches the GUI on the Swing event-dispatching thread.
 */
public class Main {
    public static void main(String[] args) {
        // Create the data manager for song pool and setlist
        SetlistManager manager = new SetlistManager();

        // Load song data from the CSV file
        manager.loadSongsFromFile("AxigallyDatabase.csv");

        // Launch the GUI on the Swing thread
        SwingUtilities.invokeLater(() -> new SetlistGUI(manager));
    }
}