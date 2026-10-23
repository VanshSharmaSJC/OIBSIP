package exam;

/** Logged-in user. */
public class User {
    public final String username;
    public String displayName;

    public User(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }
}
