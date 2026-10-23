package exam;

import javax.swing.*;
import java.awt.*;

/** Screen 2: optional profile update before starting the exam. */
public class ProfilePanel extends JPanel {
    private final UserStore store;
    private final Runnable onStart;
    private final Runnable onLogout;
    private User user;

    private final JLabel welcome = new JLabel();
    private final JTextField nameField = new JTextField(18);
    private final JPasswordField newPass = new JPasswordField(18);
    private final JPasswordField confirmPass = new JPasswordField(18);
    private final JLabel status = new JLabel(" ");

    public ProfilePanel(UserStore store, Runnable onStart, Runnable onLogout) {
        this.store = store;
        this.onStart = onStart;
        this.onLogout = onLogout;

        setLayout(new GridBagLayout());
        JPanel box = new JPanel(new GridBagLayout());
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(25, 35, 25, 35)));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 20f));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2; box.add(welcome, g);

        JLabel sub = new JLabel("Update your profile (optional), then start the exam.");
        g.gridy = 1; box.add(sub, g);

        g.gridwidth = 1;
        g.gridy = 2; g.gridx = 0; box.add(new JLabel("Display name:"), g);
        g.gridx = 1; box.add(nameField, g);
        g.gridy = 3; g.gridx = 0; box.add(new JLabel("New password:"), g);
        g.gridx = 1; box.add(newPass, g);
        g.gridy = 4; g.gridx = 0; box.add(new JLabel("Confirm password:"), g);
        g.gridx = 1; box.add(confirmPass, g);

        JLabel note = new JLabel("<html><small>Leave the password fields empty to keep your current password.</small></html>");
        note.setForeground(Color.GRAY);
        g.gridy = 5; g.gridx = 0; g.gridwidth = 2; box.add(note, g);

        g.gridy = 6; box.add(status, g);

        JButton save = new JButton("Save Changes");
        JButton start = new JButton("Start Exam");
        JButton logout = new JButton("Logout");
        start.setFont(start.getFont().deriveFont(Font.BOLD));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.add(save); buttons.add(start); buttons.add(logout);
        g.gridy = 7; box.add(buttons, g);

        add(box);

        save.addActionListener(e -> saveProfile());
        start.addActionListener(e -> onStart.run());
        logout.addActionListener(e -> this.onLogout.run());
    }

    public void setUser(User u) {
        this.user = u;
        welcome.setText("Welcome, " + u.displayName + "!");
        nameField.setText(u.displayName);
        newPass.setText("");
        confirmPass.setText("");
        status.setText(" ");
    }

    private void saveProfile() {
        String name = nameField.getText().trim();
        String p1 = new String(newPass.getPassword());
        String p2 = new String(confirmPass.getPassword());

        if (name.isEmpty()) { fail("Display name cannot be empty."); return; }
        if (!p1.isEmpty() || !p2.isEmpty()) {
            if (p1.length() < 4)  { fail("Password must be at least 4 characters."); return; }
            if (!p1.equals(p2))   { fail("Passwords do not match."); return; }
        }
        store.updateProfile(user, name, p1);
        welcome.setText("Welcome, " + user.displayName + "!");
        newPass.setText("");
        confirmPass.setText("");
        status.setForeground(new Color(0, 128, 0));
        status.setText("Profile updated successfully.");
    }

    private void fail(String msg) {
        status.setForeground(Color.RED);
        status.setText(msg);
    }
}
