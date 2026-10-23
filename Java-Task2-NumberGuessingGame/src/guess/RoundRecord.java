package guess;

/** Summary of one finished round. */
public class RoundRecord {
    public final int round, attempts, secret;
    public final boolean won;
    public final Difficulty difficulty;

    public RoundRecord(int round, Difficulty d, boolean won, int attempts, int secret) {
        this.round = round; this.difficulty = d; this.won = won; this.attempts = attempts; this.secret = secret;
    }

    @Override public String toString() {
        return won
            ? "Round " + round + " \u2014 guessed in " + attempts + (attempts == 1 ? " attempt" : " attempts")
                + " [" + difficulty.label + "]"
            : "Round " + round + " \u2014 lost, number was " + secret + " [" + difficulty.label + "]";
    }
}
