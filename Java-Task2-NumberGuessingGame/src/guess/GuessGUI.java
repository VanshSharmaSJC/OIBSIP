package guess;

import javax.swing.*;
import java.awt.*;

/** Swing version. Run: java -cp out guess.GuessGUI */
public class GuessGUI extends JFrame {
    private final GameEngine game = new GameEngine();

    private final JComboBox<Difficulty> difficultyBox = new JComboBox<>(Difficulty.values());
    private final JLabel promptLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel attemptsLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel feedbackLabel = new JLabel("Choose a difficulty and press New Game", SwingConstants.CENTER);
    private final JTextField guessField = new JTextField(8);
    private final JButton guessButton = new JButton("Guess");
    private final JButton newGameButton = new JButton("New Game");
    private final DefaultListModel<String> scoreModel = new DefaultListModel<>();
    private final JTextArea guessesArea = new JTextArea(2, 20);

    public GuessGUI() {
        super("Number Guessing Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        difficultyBox.setSelectedItem(Difficulty.MEDIUM);

        // top: difficulty selector
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        top.add(new JLabel("Difficulty:"));
        top.add(difficultyBox);
        top.add(newGameButton);
        add(top, BorderLayout.NORTH);

        // centre: prompt, attempts, input, feedback
        JPanel center = new JPanel(new GridLayout(0, 1, 5, 8));
        promptLabel.setFont(promptLabel.getFont().deriveFont(15f));
        attemptsLabel.setFont(attemptsLabel.getFont().deriveFont(Font.BOLD, 18f));
        feedbackLabel.setFont(feedbackLabel.getFont().deriveFont(Font.BOLD, 26f));
        guessField.setFont(guessField.getFont().deriveFont(20f));
        guessField.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel input = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        input.add(guessField);
        input.add(guessButton);
        guessesArea.setEditable(false);
        guessesArea.setLineWrap(true);
        guessesArea.setOpaque(false);
        guessesArea.setBorder(BorderFactory.createTitledBorder("Your guesses this round"));
        center.add(promptLabel);
        center.add(attemptsLabel);
        center.add(input);
        center.add(feedbackLabel);
        add(center, BorderLayout.CENTER);

        // south: guesses + scoreboard
        JPanel south = new JPanel(new BorderLayout(5, 5));
        south.add(guessesArea, BorderLayout.NORTH);
        JList<String> scoreList = new JList<>(scoreModel);
        JScrollPane sp = new JScrollPane(scoreList);
        sp.setBorder(BorderFactory.createTitledBorder("Scoreboard"));
        sp.setPreferredSize(new Dimension(400, 130));
        south.add(sp, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        guessButton.addActionListener(e -> submitGuess());
        guessField.addActionListener(e -> submitGuess());
        newGameButton.addActionListener(e -> startRound());

        setInputEnabled(false);
        pack();
        setMinimumSize(new Dimension(520, 520));
        setLocationRelativeTo(null);
    }

    private void startRound() {
        Difficulty d = (Difficulty) difficultyBox.getSelectedItem();
        game.startRound(d);
        promptLabel.setText("Round " + game.roundNumber() + ": guess a number between " + d.min + " and " + d.max);
        feedbackLabel.setText("Make your first guess!");
        feedbackLabel.setForeground(Color.DARK_GRAY);
        guessesArea.setText("");
        updateAttempts();
        setInputEnabled(true);
        guessField.setText("");
        guessField.requestFocusInWindow();
    }

    private void submitGuess() {
        if (game.isOver() || game.roundNumber() == 0) return;
        Difficulty d = game.difficulty();
        int g;
        try {
            g = Integer.parseInt(guessField.getText().trim());
        } catch (NumberFormatException ex) {
            warn("Enter a whole number!");
            return;
        }
        if (!game.isValidGuess(g)) {
            warn("Must be " + d.min + "-" + d.max + " (not counted)");
            return;
        }

        GameEngine.Result r = game.guess(g);
        guessesArea.append((guessesArea.getText().isEmpty() ? "" : ", ") + g);
        updateAttempts();
        guessField.setText("");
        guessField.requestFocusInWindow();

        switch (r) {
            case TOO_HIGH: setFeedback("Too High!", new Color(200, 100, 0)); break;
            case TOO_LOW:  setFeedback("Too Low!", new Color(0, 90, 200)); break;
            default:       setFeedback("Correct!", new Color(0, 140, 0)); break;
        }
        if (game.isOver()) endRound();
    }

    private void endRound() {
        setInputEnabled(false);
        scoreModel.addElement(game.history().get(game.history().size() - 1).toString());
        String msg;
        if (game.hasWon()) {
            msg = "Correct! You guessed it in " + game.attempts() + " attempt(s).";
        } else {
            setFeedback("You Lost!", new Color(190, 0, 0));
            msg = "You Lost! The number was " + game.secret() + ".";
        }
        int c = JOptionPane.showConfirmDialog(this, msg + "\n\nPlay again?", "Round Over",
                JOptionPane.YES_NO_OPTION, game.hasWon() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            startRound();
        } else {
            feedbackLabel.setText(game.hasWon() ? "You won! Press New Game to play again" : "The number was " + game.secret());
        }
    }

    private void updateAttempts() {
        attemptsLabel.setText("Attempts: " + game.attempts() + " / " + game.maxAttempts()
                + "   (left: " + game.attemptsLeft() + ")");
    }

    private void setFeedback(String text, Color c) { feedbackLabel.setText(text); feedbackLabel.setForeground(c); }
    private void warn(String text) { setFeedback(text, Color.RED); guessField.selectAll(); guessField.requestFocusInWindow(); }
    private void setInputEnabled(boolean on) { guessField.setEnabled(on); guessButton.setEnabled(on); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuessGUI().setVisible(true));
    }
}
