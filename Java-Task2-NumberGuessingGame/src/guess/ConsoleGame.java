package guess;

import java.util.Scanner;

/** Console version. Run: java -cp out guess.ConsoleGame */
public class ConsoleGame {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        GameEngine game = new GameEngine();
        System.out.println("=== Number Guessing Game ===");

        boolean again = true;
        while (again) {
            Difficulty d = chooseDifficulty(in);
            if (d == null) break; // input ended
            game.startRound(d);
            System.out.println("\nRound " + game.roundNumber() + ": I'm thinking of a number between "
                    + d.min + " and " + d.max + ". You have " + d.maxAttempts + " attempts.");

            while (!game.isOver()) {
                System.out.print("Attempt " + (game.attempts() + 1) + "/" + game.maxAttempts() + " - Enter your guess: ");
                if (!in.hasNextLine()) return;
                String line = in.nextLine().trim();
                int g;
                try {
                    g = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a whole number.");
                    continue;
                }
                if (!game.isValidGuess(g)) {
                    System.out.println("Out of range! Enter a number between " + d.min + " and " + d.max + ". (Not counted)");
                    continue;
                }
                switch (game.guess(g)) {
                    case TOO_HIGH: System.out.println("Too High!"); break;
                    case TOO_LOW:  System.out.println("Too Low!"); break;
                    default:       System.out.println("Correct!"); break;
                }
            }

            if (game.hasWon()) {
                System.out.println("You won in " + game.attempts() + " attempt(s)!");
            } else {
                System.out.println("You Lost! The number was " + game.secret() + ".");
            }

            System.out.println("\n--- Scoreboard ---");
            for (RoundRecord r : game.history()) System.out.println(r.toString().replace("\u2014", "-")); // ASCII-safe for Windows consoles
            System.out.println("------------------");

            again = askYesNo(in, "\nPlay again? (yes/no): ");
        }
        System.out.println("Thanks for playing!");
    }

    private static Difficulty chooseDifficulty(Scanner in) {
        while (true) {
            System.out.println("\nChoose difficulty:");
            Difficulty[] all = Difficulty.values();
            for (int i = 0; i < all.length; i++) System.out.println("  " + (i + 1) + ") " + all[i]);
            System.out.print("Your choice [1-3]: ");
            if (!in.hasNextLine()) return null;
            String s = in.nextLine().trim();
            if (s.equals("1")) return all[0];
            if (s.equals("2")) return all[1];
            if (s.equals("3")) return all[2];
            System.out.println("Invalid choice.");
        }
    }

    private static boolean askYesNo(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!in.hasNextLine()) return false;
            String s = in.nextLine().trim().toLowerCase();
            if (s.equals("yes") || s.equals("y")) return true;
            if (s.equals("no") || s.equals("n")) return false;
            System.out.println("Please answer yes or no.");
        }
    }
}
