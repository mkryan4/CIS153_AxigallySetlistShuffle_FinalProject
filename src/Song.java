/**
 * The Song class represents a single musical track in the Axigally™ Setlist Shuffle app.
 * Each song has multiple musical and mood attributes used for sorting, selection, and veto logic.
 */
public class Song {
    // Core song details
    private String title;
    private int indexNumber;         // Index number from the CSV, used for reference or ordering
    private int bpm;                 // Beats per minute (tempo)
    private int durationSeconds;     // Duration of the song in seconds
    private String key;              // Musical key (e.g., C major, E minor)

    // Mood and energy metrics (scaled 0–100)
    private int danceability;
    private int happy;
    private int sad;
    private int relaxed;
    private int aggressiveness;

    private String notes;           // Optional notes or comments

    /**
     * Full constructor to initialize all 11 fields.
     * Used when reading from the full AxigallyDatabase.csv file.
     */
    public Song(String title, int indexNumber, int bpm, int durationSeconds, String key,
                int danceability, int happy, int sad, int relaxed, int aggressiveness, String notes) {
        this.title = title;
        this.indexNumber = indexNumber;
        this.bpm = bpm;
        this.durationSeconds = durationSeconds;
        this.key = key;
        this.danceability = danceability;
        this.happy = happy;
        this.sad = sad;
        this.relaxed = relaxed;
        this.aggressiveness = aggressiveness;
        this.notes = notes;
    }

    /**
     * Simplified constructor used temporarily for manual testing.
     * Sets placeholder values for unused fields.
     */
    public Song(String title, int bpm, int danceability, int aggressiveness, int durationSeconds) {
        this.title = title;
        this.bpm = bpm;
        this.danceability = danceability;
        this.aggressiveness = aggressiveness;
        this.durationSeconds = durationSeconds;

        // Defaults for uninitialized attributes
        this.indexNumber = 0;
        this.key = "C major";
        this.happy = 50;
        this.sad = 50;
        this.relaxed = 50;
        this.notes = "";
    }

    // Getters for accessing song data
    public String getTitle() { return title; }
    public int getIndexNumber() { return indexNumber; }
    public int getBpm() { return bpm; }
    public int getDurationSeconds() { return durationSeconds; }
    public String getKey() { return key; }
    public int getDanceability() { return danceability; }
    public int getHappy() { return happy; }
    public int getSad() { return sad; }
    public int getRelaxed() { return relaxed; }
    public int getAggressiveness() { return aggressiveness; }
    public String getNotes() { return notes; }

    /**
     * Returns a readable string version of the song.
     * Displayed in the GUI song pool and setlist views.
     */
    @Override
    public String toString() {
        return title + " (" + bpm + " BPM, " + durationSeconds + "s, Key: " + key + ")";
    }
}