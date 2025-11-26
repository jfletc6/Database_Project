import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.text.*;

public class Project {

    private static final Color BG_DARK  = new Color(0x3C3C3C);
    private static final Color ACCENT   = new Color(0xFFBB00);
    private static final Color FG_TEXT  = Color.WHITE;

    public static void main(String[] args) {
        Path root = Paths.get("").toAbsolutePath();
        Config cfg = new Config(root);
        SwingUtilities.invokeLater(() -> new Project().createUI(cfg));
    }

    private void createUI(Config cfg) {
        JFrame frame = new JFrame("Company Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1100, 700));

        BannerPanel left = new BannerPanel(cfg);
        QueryPanel right = new QueryPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.5);
        split.setContinuousLayout(true);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard", split);
        tabs.addTab("Students",      new StudentTab());
        tabs.addTab("Competitions",  new CompetitionTab());
        tabs.addTab("Club Meetings", new ClubMeetingTab());
        tabs.addTab("Events",        new EventTab());
        tabs.addTab("Delete Records", new DeleteTab());

        frame.setContentPane(tabs);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /* ===========================
       CONFIG LOADER (FILES)
       =========================== */
    static class Config {
        private final Path dir;
        private String name;
        private String motd;
        private ImageIcon logo;

        public Config(Path dir) {
            this.dir = dir;
            name = readFirstLine("company_name.txt", "Your Company");
            motd = readAll("motd.txt", "Welcome! Add motd.txt.");
            loadLogo();
        }

        private String readFirstLine(String fname, String def) {
            Path p = dir.resolve(fname);
            try {
                String text = Files.readString(p, StandardCharsets.UTF_8);
                int nl = text.indexOf('\n');
                String first = (nl >= 0 ? text.substring(0, nl) : text).trim();
                return first.isEmpty() ? def : first;
            } catch (IOException e) {
                return def;
            }
        }

        private String readAll(String fname, String def) {
            Path p = dir.resolve(fname);
            try {
                String t = Files.readString(p, StandardCharsets.UTF_8).trim();
                return t.isEmpty() ? def : t;
            } catch (IOException e) {
                return def;
            }
        }

        private void loadLogo() {
            Path p = dir.resolve("logo.png");
            if (!Files.exists(p)) return;
            try {
                BufferedImage img = ImageIO.read(Files.newInputStream(p));
                logo = new ImageIcon(img);
            } catch (Exception ignored) {}
        }

        public String name() { return name; }
        public String motd() { return motd; } 
        public ImageIcon logo() { return logo; }
    }

    /* ===========================
       LEFT PANEL — BANNER & MOTD
       =========================== */
    static class BannerPanel extends JPanel {
        public BannerPanel(Config cfg) {
            setLayout(new BorderLayout(16, 16));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            setBackground(BG_DARK);

            // Company name section
            JLabel nameLabel = new JLabel(cfg.name(), SwingConstants.CENTER);
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 34f));
            nameLabel.setForeground(ACCENT);
            add(nameLabel, BorderLayout.NORTH);

            // Logo section
            JLabel logoLabel = new JLabel("", SwingConstants.CENTER);
            logoLabel.setOpaque(false);
            if (cfg.logo() != null) {
                logoLabel.setIcon(cfg.logo());
            } else {
                logoLabel.setText("(logo.png)");
                logoLabel.setForeground(new Color(180, 180, 180));
            }
            add(logoLabel, BorderLayout.CENTER);

            // MOTD section
            JPanel motdSection = new JPanel(new BorderLayout(8, 8));
            motdSection.setOpaque(false);

            // MOTD Title
            JLabel motdTitle = new JLabel("Message of the Day", SwingConstants.CENTER);
            motdTitle.setFont(motdTitle.getFont().deriveFont(Font.BOLD, 18f)); // larger than body (12)
            motdTitle.setForeground(FG_TEXT);
            motdSection.add(motdTitle, BorderLayout.NORTH);

            // MOTD Body
            JTextPane motdPane = new JTextPane();
            motdPane.setEditable(false);
            motdPane.setOpaque(false);
            motdPane.setText(cfg.motd());
            motdPane.setFocusable(false);
            motdPane.setHighlighter(null);

            StyledDocument doc = motdPane.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_CENTER);
            StyleConstants.setFontFamily(attrs, "SansSerif");
            StyleConstants.setFontSize(attrs, 12);
            doc.setParagraphAttributes(0, doc.getLength(), attrs, false);

            JPanel motdBodyWrap = new JPanel(new BorderLayout());
            motdBodyWrap.setOpaque(false);
            motdBodyWrap.add(motdPane, BorderLayout.CENTER);
            motdBodyWrap.setBorder(BorderFactory.createEmptyBorder(4, 8, 0, 8));

            motdSection.add(motdBodyWrap, BorderLayout.CENTER);

            JPanel southWrap = new JPanel(new BorderLayout());
            southWrap.setOpaque(false);
            southWrap.add(motdSection, BorderLayout.CENTER);
            southWrap.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            add(southWrap, BorderLayout.SOUTH);

            // LATER: replace file MOTD with DB "next upcoming competition"
            // e.g., motdPane.setText(repository.getNextUpcomingCompetitionSummary());
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(440, 440);
        }
    }

    /* ===========================
       RIGHT PANEL — FIVE BUTTONS
       =========================== */
    static class QueryPanel extends JPanel {

        
        private static final float BTN_FONT_PT = 16f;      
        private static final Insets BTN_PAD    = new Insets(12, 20, 12, 20); 
        private static final int    BTN_HEIGHT = 48;       

        public QueryPanel() {
            setLayout(new BorderLayout(16, 16));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            setBackground(BG_DARK);

            JLabel title = new JLabel("Quick Queries", SwingConstants.CENTER);
            title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
            title.setForeground(ACCENT);
            add(title, BorderLayout.NORTH);

            JPanel buttons = new JPanel(new GridLayout(5, 1, 10, 10));
            buttons.setOpaque(false);
            buttons.add(medButton("Members by Classification"));
            buttons.add(medButton("Members in a Competition"));
            buttons.add(medButton("Leadership Officers & Roles"));
            buttons.add(medButton("Members >3 Events Attended"));
            buttons.add(medButton("Events This Semester"));

            add(buttons, BorderLayout.CENTER);

            // TODO: Add ActionListeners later to wire these to your DB/repository.
        }

        private JButton medButton(String label) {
            JButton b = new JButton(label);
            b.setFont(b.getFont().deriveFont(Font.BOLD, BTN_FONT_PT));
            b.setMargin(BTN_PAD);
            b.setFocusPainted(false);
            b.setPreferredSize(new Dimension(0, BTN_HEIGHT));
            b.setBackground(ACCENT);
            b.setForeground(Color.BLACK);

            return b;
        }
    }
}
