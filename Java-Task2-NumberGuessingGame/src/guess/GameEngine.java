package guess;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Game logic shared by the console and Swing versions (no I/O here). */
public class GameEngine {
    public enum Result { TOO_HIGH, TOO_LOW, CORRECT }

    private final Random random;
    private final List<RoundRecord> history = new ArrayList<>();
    private Difficulty difficulty = Difficulty.MEDIUM;
    private int secret, attempts, roundNumber;
    private boolean over, won;

    public GameEngine()             { this(new Random()); }
    public GameEngine(Random r)     { this.random = r; }

    /** Starts a new round: picks a random number in the difficulty's range. */
    public void startRound(Difficulty d) {
        difficulty = d;
        secret = d.min + random.nextInt(d.max - d.min + 1);
        attempts = 0;
        over = false;
        won = false;
        roundNumber++;
    }

    public boolean isValidGuess(int g) { return g >= difficulty.min && g <= difficulty.max; }

    /** Evaluates a guess. Caller should check isValidGuess first and not call when the round is over. */
    public Result guess(int g) {
        if (over) throw new IllegalStateException("Round is over");
        attempts++;
        Result r = g > secret ? Result.TOO_HIGH : g < secret ? Result.TOO_LOW : Result.CORRECT;
        if (r == Result.CORRECT) {
            over = true; won = true;
        } else if (attempts >= difficulty.maxAttempts) {
            over = true;
        }
        if (over) history.add(new RoundRecord(roundNumber, difficulty, won, attempts, secret));
        return r;
    }

    public int attempts()           { return attempts; }
    public int attemptsLeft()       { return difficulty.maxAttempts - attempts; }
    public int maxAttempts()        { return difficulty.maxAttempts; }
    public int secret()             { return secret; }
    public int roundNumber()        { return roundNumber; }
    public boolean isOver()         { return over; }
    public boolean hasWon()         { return won; }
    public Difficulty difficulty()  { return difficulty; }
    public List<RoundRecord> history() { return history; }
}
