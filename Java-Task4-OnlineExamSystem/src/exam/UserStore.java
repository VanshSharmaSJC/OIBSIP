package exam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple file-backed user database (users.txt in the working directory).
 * Line format: username,sha256(password),displayName
 * Created with demo accounts on first run; profile changes are persisted.
 */
public class UserStore {
    private final Path file = Paths.get("users.txt");
    private final Map<String, String[]> users = new LinkedHashMap<>(); // username -> {hash, displayName}

    public UserStore() {
        if (Files.exists(file)) {
            read();
        }
        if (users.isEmpty()) {
            users.put("student1", new String[]{hash("pass123"), "Student One"});
            users.put("student2", new String[]{hash("pass456"), "Student Two"});
            save();
        }
    }

    public User authenticate(String username, String password) {
        String[] rec = users.get(username);
        if (rec != null && rec[0].equals(hash(password))) {
            return new User(username, rec[1]);
        }
        return null;
    }

    /** Updates display name, and password if newPassword is non-null/non-empty. */
    public void updateProfile(User user, String newDisplayName, String newPassword) {
        String[] rec = users.get(user.username);
        rec[1] = newDisplayName;
        if (newPassword != null && !newPassword.isEmpty()) {
            rec[0] = hash(newPassword);
        }
        user.displayName = newDisplayName;
        save();
    }

    private void read() {
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String[] p = line.split(",", 3);
                if (p.length == 3) users.put(p[0], new String[]{p[1], p[2]});
            }
        } catch (IOException e) {
            System.err.println("Could not read users.txt: " + e.getMessage());
        }
    }

    private void save() {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, String[]> e : users.entrySet()) {
            lines.add(e.getKey() + "," + e.getValue()[0] + "," + e.getValue()[1].replace(",", " "));
        }
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Could not save users.txt: " + e.getMessage());
        }
    }

    private static String hash(String s) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
