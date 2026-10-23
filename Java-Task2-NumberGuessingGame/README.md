# Number Guessing Game (Java)

Two front-ends share one game engine:
- **Console** (Scanner): `./run-console.sh` / `run-console.bat`
- **Swing GUI** (JTextField): `./run-gui.sh` / `run-gui.bat`

Requires JDK 8+. Manual: `mkdir out && javac -d out src/guess/*.java && java -cp out guess.GuessGUI`

## Features
- Random number per round (java.util.Random) in the difficulty's range
- Feedback: "Too High!", "Too Low!", "Correct!"
- Attempt counter always visible (e.g. `Attempts: 3 / 7`)
- Attempt limit -> "You Lost!" and the number is revealed
- Play Again prompt after each round
- Scoreboard across rounds: `Round 2 — guessed in 5 attempts`
- Difficulty: Easy 1-50 / 10 attempts, Medium 1-100 / 7, Hard 1-200 / 5
- Invalid or out-of-range input is rejected and does not use up an attempt

## Files
`GameEngine` (logic), `Difficulty`, `RoundRecord`, `ConsoleGame`, `GuessGUI`
