package exam;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Screen 4: score, time taken, per-question breakdown, logout. */
public class ResultPanel extends JPanel {

    public ResultPanel(ExamResult r, Runnable onLogout) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // ---- summary
        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Exam Result");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        JLabel score = new JLabel("Score: " + r.correct() + " out of " + r.total()
                + String.format("  (%.0f%%)", 100.0 * r.correct() / r.total()));
        score.setFont(score.getFont().deriveFont(Font.BOLD, 20f));
        score.setForeground(r.correct() * 2 >= r.total() ? new Color(0, 128, 0) : new Color(180, 0, 0));
        JLabel time = new JLabel(String.format("Time taken: %02d:%02d%s", r.secondsTaken / 60, r.secondsTaken % 60,
                r.autoSubmitted ? "  (auto-submitted: time ran out)" : ""));
        JLabel counts = new JLabel("Correct: " + r.correct() + "    Incorrect: " + r.incorrect()
                + "    Unanswered: " + r.unanswered());
        for (JLabel l : new JLabel[]{title, score, time, counts}) {
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            l.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
            summary.add(l);
        }
        add(summary, BorderLayout.NORTH);

        // ---- breakdown table
        String[] cols = {"#", "Question", "Your answer", "Correct answer", "Result"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        for (int i = 0; i < r.total(); i++) {
            Question q = r.questions.get(i);
            int a = r.answers[i];
            String mine = a < 0 ? "(not answered)" : (char) ('A' + a) + ". " + q.options[a];
            String right = (char) ('A' + q.correctIndex) + ". " + q.options[q.correctIndex];
            String res = a < 0 ? "Unanswered" : (a == q.correctIndex ? "Correct" : "Incorrect");
            model.addRow(new Object[]{i + 1, q.text, mine, right, res});
        }
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(320);
        table.getColumnModel().getColumn(4).setMaxWidth(100);
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String s = String.valueOf(v);
                c.setForeground(s.equals("Correct") ? new Color(0, 128, 0)
                        : s.equals("Incorrect") ? new Color(190, 0, 0) : Color.GRAY);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                return c;
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---- logout
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> onLogout.run());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(logout);
        add(south, BorderLayout.SOUTH);
    }
}
