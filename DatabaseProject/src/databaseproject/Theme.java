// Theme.java
import java.awt.*;
import javax.swing.*;

/**
 * Shared UI theme for the Cyber Club project.
 *
 * Primary palette (university colors):
 *   - Gold:     #FFBB00
 *   - Charcoal: #3C3C3C
 *
 * Dark cyber console vibe with mostly white text.
 */
public final class Theme {

    // Core colors
    public static final Color BG_MAIN      = new Color(0x1A1A1A);
    public static final Color BG_PANEL     = new Color(0x252525);
    public static final Color BG_CARD      = new Color(0x303030);
    public static final Color BORDER_SOFT  = new Color(0x444444);

    public static final Color TEXT_PRIMARY = Color.WHITE;
    public static final Color TEXT_MUTED   = new Color(0xCCCCCC);

    public static final Color CYBER_ACCENT = new Color(0xFFBB00); // gold
    public static final Color HIGHLIGHT    = new Color(0x4FC3F7); // cyber blue

    private Theme() {}

    /** Apply dark background to an entire tab. */
    public static void styleTabBackground(JComponent root) {
        root.setBackground(BG_MAIN);
        root.setForeground(TEXT_PRIMARY);
    }

    /** Big header label (e.g., "Students"). */
    public static void styleHeaderLabel(JLabel label) {
        label.setForeground(TEXT_PRIMARY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
    }

    /** Simple form label. */
    public static void styleFormLabel(JLabel label) {
        label.setForeground(TEXT_PRIMARY);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 13f));
    }

    /** Card-like panel for forms. */
    public static void styleCard(JPanel panel) {
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_SOFT),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    /** Primary button (save/commit actions). */
    public static void stylePrimaryButton(JButton button) {
        button.setBackground(CYBER_ACCENT);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Secondary button (clear/reset). */
    public static void styleSecondaryButton(JButton button) {
        button.setBackground(BG_PANEL);
        button.setForeground(TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BORDER_SOFT));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Status/console text area. */
    public static void styleStatusArea(JTextArea area) {
        area.setBackground(BG_MAIN);
        area.setForeground(TEXT_MUTED);
        area.setCaretColor(CYBER_ACCENT);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createLineBorder(BORDER_SOFT));
    }
}
