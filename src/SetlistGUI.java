import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

/**
 * The GUI class for the Axigally™ Setlist Shuffle app.
 * Allows the user to view, build, sort, veto, and save a concert setlist
 * using songs loaded from the CSV database.
 */
public class SetlistGUI {
    // UI Components
    private JFrame frame;
    private JList<String> songList;
    private DefaultListModel<String> songListModel;
    private JList<String> setlistList;
    private DefaultListModel<String> setlistListModel;
    private JLabel runtimeLabel;

    // Backend manager for song data and setlist logic
    private SetlistManager manager;

    /**
     * Constructor to build the GUI window.
     * @param manager The SetlistManager object shared across the app
     */
    public SetlistGUI(SetlistManager manager) {
        this.manager = manager;
        initialize();
    }

    /**
     * Initializes all GUI components and layouts.
     */
    private void initialize() {
        frame = new JFrame("Axigally™ Setlist Shuffle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 500);
        frame.setLayout(new BorderLayout(10, 10));

        // Main area: two panels side by side
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Song Pool (left panel)
        songListModel = new DefaultListModel<>();
        songList = new JList<>(songListModel);
        songList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane songScrollPane = new JScrollPane(songList);
        songScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Setlist (right panel)
        setlistListModel = new DefaultListModel<>();
        setlistList = new JList<>(setlistListModel);
        setlistList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane setlistScrollPane = new JScrollPane(setlistList);
        setlistScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Runtime label under setlist
        JPanel setlistPanel = new JPanel(new BorderLayout(5, 5));
        setlistPanel.add(setlistScrollPane, BorderLayout.CENTER);
        runtimeLabel = new JLabel("Total Runtime: 0:00", JLabel.CENTER);
        runtimeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        setlistPanel.add(runtimeLabel, BorderLayout.SOUTH);

        // Add both sides to center panel
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        centerPanel.add(songScrollPane);
        centerPanel.add(setlistPanel);

        // Buttons at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton addButton = new JButton("Add Selected Song");
        JButton clearButton = new JButton("Clear Setlist");
        JButton sortButton = new JButton("Sort Setlist by BPM");
        JButton vetoSelectedButton = new JButton("Veto Selected Song");
        JButton saveButton = new JButton("Save Setlist");

        // Set all button sizes equally
        Dimension buttonSize = new Dimension(180, 30);
        addButton.setPreferredSize(buttonSize);
        clearButton.setPreferredSize(buttonSize);
        sortButton.setPreferredSize(buttonSize);
        vetoSelectedButton.setPreferredSize(buttonSize);
        saveButton.setPreferredSize(buttonSize);

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(vetoSelectedButton);
        buttonPanel.add(saveButton);

        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        populateSongList();
        populateSetlist();

        // --- Button Listeners ---

        // Add selected song to setlist
        addButton.addActionListener(e -> {
        	String selectedHtml = songList.getSelectedValue();
        	if (selectedHtml != null) {
        	    // Extract just the title between <b> and </b>
        	    String cleanTitle = selectedHtml.replaceAll(".*<b>(.*?)</b>.*", "$1").trim();
        	    manager.addToSetlist(cleanTitle);
        	    updateSetlistDisplay();
        	} else {
        	    JOptionPane.showMessageDialog(frame, "Please select a song first!");
        	}
        });

        // Clear setlist
        clearButton.addActionListener(e -> {
            manager.clearSetlist();
            updateSetlistDisplay();
        });

        // Sort setlist by BPM
        sortButton.addActionListener(e -> {
            manager.sortSetlistByBpm();
            updateSetlistDisplay();
        });

        // Remove selected song from setlist
        vetoSelectedButton.addActionListener(e -> {
            String selectedSetlistItem = setlistList.getSelectedValue();
            if (selectedSetlistItem != null) {
                String cleanTitle = selectedSetlistItem.split("\\(")[0].trim();
                manager.getSetlist().removeIf(song -> song.getTitle().equalsIgnoreCase(cleanTitle));
                updateSetlistDisplay();
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a song in the setlist to veto.");
            }
        });

        // Save setlist to file
        saveButton.addActionListener(e -> {
            saveSetlistToFile();
        });

        frame.setVisible(true);
    }

    /**
     * Loads songs from the manager’s pool into the song list display.
     */
    private void populateSongList() {
        songListModel.clear();
        LinkedHashMap<String, Song> songs = manager.getSongPool();

        for (Song song : songs.values()) {
            String songDetails = "<html><b>" + song.getTitle() + "</b> | " +
                                 "BPM: " + song.getBpm() + " | " +
                                 "Length: " + formatDuration(song.getDurationSeconds()) + " | " +
                                 "Key: " + song.getKey() + " | " +
                                 "Danceability: " + song.getDanceability() + " | " +
                                 "Happy: " + song.getHappy() + " | " +
                                 "Sad: " + song.getSad() + " | " +
                                 "Relaxed: " + song.getRelaxed() + " | " +
                                 "Aggressive: " + song.getAggressiveness() + " | " +
                                 "Notes: " + song.getNotes() + "</html>";
            songListModel.addElement(songDetails);
        }
    }

    /**
     * Initializes the setlist list display (blank at launch).
     */
    private void populateSetlist() {
        setlistListModel.clear();
        runtimeLabel.setText("Total Runtime: 0:00");
    }

    /**
     * Updates the setlist display with current songs and runtime.
     */
    private void updateSetlistDisplay() {
        setlistListModel.clear();
        int totalSeconds = 0;

        for (Song song : manager.getSetlist()) {
            setlistListModel.addElement(song.toString());
            totalSeconds += song.getDurationSeconds();
        }

        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        runtimeLabel.setText("Total Runtime: " + minutes + ":" + String.format("%02d", seconds));
    }

    /**
     * Appends the current setlist to the CSV file with timestamp.
     */
    private void saveSetlistToFile() {
        try (FileWriter writer = new FileWriter("AxigallyDatabase.csv", true)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            writer.write("\n--- Setlist saved on " + dtf.format(LocalDateTime.now()) + " ---\n");

            for (Song song : manager.getSetlist()) {
                writer.write(
                    song.getTitle() + "," +
                    song.getIndexNumber() + "," +
                    song.getBpm() + "," +
                    formatDuration(song.getDurationSeconds()) + "," +
                    song.getKey() + "," +
                    song.getDanceability() + "," +
                    song.getHappy() + "," +
                    song.getSad() + "," +
                    song.getRelaxed() + "," +
                    song.getAggressiveness() + "," +
                    song.getNotes() + "\n"
                );
            }

            JOptionPane.showMessageDialog(frame, "Setlist saved to AxigallyDatabase.csv!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving setlist: " + e.getMessage());
        }
    }

    /**
     * Converts total seconds into mm:ss format.
     */
    private String formatDuration(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}