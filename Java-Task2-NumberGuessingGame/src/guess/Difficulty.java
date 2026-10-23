package guess;

/** Difficulty levels: range and attempt limit. */
public enum Difficulty {
    EASY("Easy", 1, 50, 10),
    MEDIUM("Medium", 1, 100, 7),
    HARD("Hard", 1, 200, 5);

    public final String label;
    public final int min, max, maxAttempts;

    Difficulty(String label, int min, int max, int maxAttempts) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.maxAttempts = maxAttempts;
    }

    @Override public String toString() {
        return label + " (" + min + "-" + max + ", " + maxAttempts + " attempts)";
    }
}
