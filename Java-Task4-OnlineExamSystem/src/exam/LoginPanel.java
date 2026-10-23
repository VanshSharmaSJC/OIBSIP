package exam;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/** Screen 1: username + password. */
public class LoginPanel extends JPanel {
    private final JTextField userField = new JTextField(16);
    private final JPasswordField passField = new JPasswordField(16);
    private final JLabel error = new JLabel(" ");

    public LoginPanel(UserStore store, Consumer<User> onSuccess) {
        setLayout(new GridBagLayout());
        JPanel box = new JPanel(new GridBagLayout());
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(25, 35, 25, 35)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Online Examination System", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2; box.add(title, g);

        g.gridwidth = 1; g.gridy = 1; box.add(new JLabel("Username:"), g);
        g.gridx = 1; box.add(userField, g);
        g.gridx = 0; g.gridy = 2; box.add(new JLabel("Password:"), g);
        g.gridx = 1; box.add(passField, g);

        error.setForeground(Color.RED);
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2; box.add(error, g);

        JButton login = new JButton("Login");
        g.gridy = 4; box.add(login, g);

        JLabel hint = new JLabel("<html><center><small>Demo: student1 / pass123 &nbsp;|&nbsp; student2 / pass456</small></center></html>",
                SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        g.gridy = 5; box.add(hint, g);

        add(box);

        Runnable attempt = () -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            if (u.isEmpty() || p.isEmpty()) {
                error.setText("Please enter username and password.");
                return;
            }
            User user = store.authenticate(u, p);
            if (user == null) {
                error.setText("Invalid username or password.");
                passField.setText("");
            } else {
                reset();
                onSuccess.accept(user);
            }
        };
        login.addActionListener(e -> attempt.run());
        userField.addActionListener(e -> attempt.run());
        passField.addActionListener(e -> attempt.run());
    }

    /** Clears the form (used on logout). */
    public void reset() {
        userField.setText("");
        passField.setText("");
        error.setText(" ");
        userField.requestFocusInWindow();
    }
}
