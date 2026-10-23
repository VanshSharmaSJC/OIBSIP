package exam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Online Examination System - application frame.
 * Screens are switched with a CardLayout: login -> profile -> exam -> result.
 *
 * Usage: java exam.Main [minutes]     (default exam length: 30 minutes)
 */
public class Main extends JFrame {
    private static final String LOGIN = "login", PROFILE = "profile", EXAM = "exam", RESULT = "result";

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final UserStore store = new UserStore();
    private final int examMinutes;

    private final LoginPanel loginPanel;
    private final ProfilePanel profilePanel;
    private ExamPanel examPanel;   // recreated for every attempt
    private ResultPanel resultPanel;
    private User currentUser;

    public Main(int examMinutes) {
        super("Online Examination System");
        this.examMinutes = examMinutes;

        loginPanel = new LoginPanel(store, this::onLogin);
        profilePanel = new ProfilePanel(store, this::startExam, this::logout);
        root.add(loginPanel, LOGIN);
        root.add(profilePanel, PROFILE);
        setContentPane(root);

        // Session management: closing the window during an exam asks for confirmation.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { handleClose(); }
        });

        setSize(900, 650);
        setMinimumSize(new Dimension(760, 560));
        setLocationRelativeTo(null);
        cards.show(root, LOGIN);
    }

    private void onLogin(User u) {
        currentUser = u;
        profilePanel.setUser(u);
        cards.show(root, PROFILE);
    }

    private void startExam() {
        if (examPanel != null) root.remove(examPanel);
        examPanel = new ExamPanel(QuestionBank.load(), examMinutes, this::showResult);
        root.add(examPanel, EXAM);
        cards.show(root, EXAM);
        setTitle("Online Examination System - " + currentUser.displayName);
    }

    private void showResult(ExamResult result) {
        if (resultPanel != null) root.remove(resultPanel);
        resultPanel = new ResultPanel(result, this::logout);
        root.add(resultPanel, RESULT);
        cards.show(root, RESULT);
    }

    private void logout() {
        currentUser = null;
        setTitle("Online Examination System");
        loginPanel.reset();
        cards.show(root, LOGIN);
    }

    private void handleClose() {
        if (examPanel != null && examPanel.isInProgress()) {
            int c = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to quit?\nYour exam is in progress and answers will be lost.",
                    "Quit Exam", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c != JOptionPane.YES_OPTION) return;
            examPanel.abort();
        }
        dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        int minutes = 30;
        if (args.length > 0) {
            try { minutes = Math.max(1, Integer.parseInt(args[0])); } catch (NumberFormatException ignored) { }
        }
        final int m = minutes;
        SwingUtilities.invokeLater(() -> new Main(m).setVisible(true));
    }
}
