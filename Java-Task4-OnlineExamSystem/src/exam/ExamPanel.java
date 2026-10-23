package exam;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/** Screen 3: one MCQ at a time, countdown timer, navigation and submit. */
public class ExamPanel extends JPanel {
    private final List<Question> questions;
    private final int[] answers;
    private final Consumer<ExamResult> onFinished;
    private final int totalSeconds;
    private final long startMillis = System.currentTimeMillis();
    private final long deadlineMillis;
    private final Timer timer;

    private int current = 0;
    private boolean finished = false;

    private final JLabel timerLabel = new JLabel();
    private final JLabel progressLabel = new JLabel();
    private final JTextArea questionText = new JTextArea();
    private final JRadioButton[] radios = new JRadioButton[4];
    private final ButtonGroup group = new ButtonGroup();
    private final JButton prev = new JButton("\u25C0 Previous");
    private final JButton next = new JButton("Next \u25B6");
    private final JButton[] navButtons;

    public ExamPanel(List<Question> questions, int minutes, Consumer<ExamResult> onFinished) {
        this.questions = questions;
        this.answers = new int[questions.size()];
        Arrays.fill(answers, -1);
        this.onFinished = onFinished;
        this.totalSeconds = minutes * 60;
        this.deadlineMillis = startMillis + totalSeconds * 1000L;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // ---- top bar: progress + timer
        JPanel top = new JPanel(new BorderLayout());
        progressLabel.setFont(progressLabel.getFont().deriveFont(Font.BOLD, 16f));
        timerLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
        timerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        top.add(progressLabel, BorderLayout.WEST);
        top.add(timerLabel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // ---- centre: question + options
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        questionText.setEditable(false);
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setOpaque(false);
        questionText.setFont(questionText.getFont().deriveFont(Font.BOLD, 18f));
        questionText.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        center.add(questionText);

        for (int i = 0; i < 4; i++) {
            radios[i] = new JRadioButton();
            radios[i].setFont(radios[i].getFont().deriveFont(16f));
            radios[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            radios[i].setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
            radios[i].setFocusPainted(false);
            final int idx = i;
            radios[i].addActionListener(e -> { answers[current] = idx; refreshNavigator(); });
            group.add(radios[i]);
            center.add(radios[i]);
        }
        JButton clear = new JButton("Clear answer");
        clear.setAlignmentX(Component.LEFT_ALIGNMENT);
        clear.addActionListener(e -> { group.clearSelection(); answers[current] = -1; refreshNavigator(); });
        center.add(Box.createVerticalStrut(10));
        center.add(clear);
        add(center, BorderLayout.CENTER);

        // ---- bottom: question navigator + prev/next/submit
        JPanel bottom = new JPanel(new BorderLayout(10, 10));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        nav.setBorder(BorderFactory.createTitledBorder("Questions (green = answered)"));
        navButtons = new JButton[questions.size()];
        for (int i = 0; i < navButtons.length; i++) {
            JButton b = new JButton(String.valueOf(i + 1));
            b.setMargin(new Insets(2, 6, 2, 6));
            b.setFocusPainted(false);
            b.setOpaque(true);
            final int target = i;
            b.addActionListener(e -> goTo(target));
            navButtons[i] = b;
            nav.add(b);
        }
        bottom.add(nav, BorderLayout.NORTH);

        JButton submit = new JButton("Submit Exam");
        submit.setFont(submit.getFont().deriveFont(Font.BOLD));
        submit.setForeground(new Color(160, 0, 0));
        JPanel buttons = new JPanel(new BorderLayout());
        JPanel pn = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pn.add(prev); pn.add(next);
        buttons.add(pn, BorderLayout.WEST);
        buttons.add(submit, BorderLayout.EAST);
        bottom.add(buttons, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        prev.addActionListener(e -> goTo(current - 1));
        next.addActionListener(e -> goTo(current + 1));
        submit.addActionListener(e -> confirmSubmit());

        // ---- countdown timer (computed from wall-clock so it never drifts)
        timer = new Timer(250, e -> tick());
        showQuestion();
        tick();
        timer.start();
    }

    /** True while the exam is running (used by the window-close handler). */
    public boolean isInProgress() { return !finished; }

    /** Stops the countdown without submitting (used when the user quits). */
    public void abort() { finished = true; timer.stop(); }

    // ------------------------------------------------------------------

    private void goTo(int index) {
        if (index < 0 || index >= questions.size()) return;
        current = index;
        showQuestion();
    }

    private void showQuestion() {
        Question q = questions.get(current);
        progressLabel.setText("Question " + (current + 1) + " of " + questions.size());
        questionText.setText(q.text);
        group.clearSelection();
        for (int i = 0; i < 4; i++) {
            radios[i].setText((char) ('A' + i) + ".  " + q.options[i]);
        }
        if (answers[current] >= 0) radios[answers[current]].setSelected(true);
        prev.setEnabled(current > 0);
        next.setEnabled(current < questions.size() - 1);
        refreshNavigator();
    }

    private void refreshNavigator() {
        for (int i = 0; i < navButtons.length; i++) {
            navButtons[i].setBackground(answers[i] >= 0 ? new Color(160, 220, 160) : null);
            navButtons[i].setBorder(i == current
                    ? BorderFactory.createLineBorder(Color.BLUE, 3)
                    : UIManager.getBorder("Button.border"));
        }
    }

    private void tick() {
        if (finished) return;
        long remainingMs = deadlineMillis - System.currentTimeMillis();
        if (remainingMs <= 0) {
            timerLabel.setText("Time left: 00:00");
            autoSubmit();
            return;
        }
        long secs = (remainingMs + 999) / 1000;
        timerLabel.setText(String.format("Time left: %02d:%02d", secs / 60, secs % 60));
        timerLabel.setForeground(secs <= 60 ? Color.RED : Color.BLACK);
    }

    private void confirmSubmit() {
        int unanswered = 0;
        for (int a : answers) if (a < 0) unanswered++;
        String msg = unanswered > 0
                ? "You have " + unanswered + " unanswered question(s).\nSubmit the exam anyway?"
                : "Are you sure you want to submit the exam?";
        int choice = JOptionPane.showConfirmDialog(this, msg, "Confirm Submit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION && !finished) {
            finish(false);
        }
    }

    private void autoSubmit() {
        if (finished) return;
        timer.stop();
        JOptionPane.showMessageDialog(this, "Time is up! Your exam is being submitted automatically.",
                "Time Up", JOptionPane.WARNING_MESSAGE);
        if (!finished) finish(true);
    }

    private void finish(boolean auto) {
        finished = true;
        timer.stop();
        int taken = (int) Math.min(totalSeconds, (System.currentTimeMillis() - startMillis) / 1000);
        onFinished.accept(new ExamResult(questions, answers, taken, auto));
    }
}
