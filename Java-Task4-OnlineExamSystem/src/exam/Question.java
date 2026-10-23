package exam;

/** A single multiple-choice question with exactly four options. */
public class Question {
    public final String text;
    public final String[] options;
    public final int correctIndex; // 0..3

    public Question(String text, String a, String b, String c, String d, int correctIndex) {
        this.text = text;
        this.options = new String[]{a, b, c, d};
        this.correctIndex = correctIndex;
    }
}
