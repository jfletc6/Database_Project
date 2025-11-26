import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Event entry tab (DB-backed, themed).
 * Table: Events(Event_ID, Event_Date, Event_Topic)
 *
 * Left:  form to add events.
 * Right: event.png image + table dump with refresh.
 */
public class EventTab extends JPanel {

    // --- form fields ---
    private final JTextField txtId    = new JTextField(10);
    private final JTextField txtDate  = new JTextField(20);
    private final JTextField txtTopic = new JTextField(20);

    // --- buttons ---
    private final JButton btnSave    = new JButton("Save Event");
    private final JButton btnClear   = new JButton("Clear");
    private final JButton btnRefresh = new JButton("Refresh Table");

    // --- status area ---
    private final JTextArea txtStatus = new JTextArea(3, 40);

    // --- table model ---
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable eventTable            = new JTable(tableModel);

    public EventTab() {
        // base theme + layout
        Theme.styleTabBackground(this);
        setLayout(new BorderLayout(16, 16));

        // top banner
        add(buildHeaderBanner(), BorderLayout.NORTH);

        // left = form, right = image + table
        JPanel left  = buildFormCard();
        JPanel right = buildRightCard();   // image + table

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.45);
        split.setContinuousLayout(true);
        split.setBorder(null);

        add(split, BorderLayout.CENTER);

        // status bar
        Theme.styleStatusArea(txtStatus);
        JScrollPane statusScroll = new JScrollPane(
                txtStatus,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        statusScroll.setBorder(null);
        add(statusScroll, BorderLayout.SOUTH);

        // wiring
        btnSave.addActionListener(e -> saveEvent());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadEvents());

        // load table initially + next id (optional)
        loadEvents();
    }

    // =====================  UI BUILDERS  =====================

    private JComponent buildHeaderBanner() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_PANEL);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        header.setPreferredSize(new Dimension(10, 70));

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Events", SwingConstants.CENTER);
        Theme.styleHeaderLabel(title);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        textBox.add(title);
        header.add(textBox, BorderLayout.CENTER);

        JSeparator accent = new JSeparator();
        accent.setForeground(Theme.CYBER_ACCENT);
        accent.setBackground(Theme.CYBER_ACCENT);
        accent.setPreferredSize(new Dimension(1, 3));
        header.add(accent, BorderLayout.SOUTH);

        return header;
    }

    private JPanel buildFormCard() {
        JPanel card = new JPanel(new GridBagLayout());
        Theme.styleCard(card);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Event ID
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblId = new JLabel("Event ID:");
        Theme.styleFormLabel(lblId);
        card.add(lblId, gbc);

        gbc.gridx = 1;
        styleTextField(txtId);
        card.add(txtId, gbc);
        row++;

        // Event Date
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblDate = new JLabel("Event Date (yyyy-MM-dd):");
        Theme.styleFormLabel(lblDate);
        card.add(lblDate, gbc);

        gbc.gridx = 1;
        styleTextField(txtDate);
        card.add(txtDate, gbc);
        row++;

        // Event Topic
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblTopic = new JLabel("Event Topic:");
        Theme.styleFormLabel(lblTopic);
        card.add(lblTopic, gbc);

        gbc.gridx = 1;
        styleTextField(txtTopic);
        card.add(txtTopic, gbc);
        row++;

        // Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonRow.setOpaque(false);
        Theme.stylePrimaryButton(btnSave);
        Theme.styleSecondaryButton(btnClear);
        buttonRow.add(btnSave);
        buttonRow.add(btnClear);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        card.add(buttonRow, gbc);

        return card;
    }

    /** Right-hand card: top = event.png, bottom = table dump with refresh. */
    private JPanel buildRightCard() {
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBackground(Theme.BG_PANEL);
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top: image ---
        JLabel imgHeader = new JLabel("Event Spotlight", SwingConstants.CENTER);
        imgHeader.setForeground(Theme.TEXT_PRIMARY);
        imgHeader.setFont(imgHeader.getFont().deriveFont(Font.BOLD, 14f));
        imgHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setBackground(Theme.BG_PANEL);
        imgPanel.add(imgHeader, BorderLayout.NORTH);

        ImageIcon icon = loadRootImage("event.png"); // image in project root
        if (icon != null) {
            Image img = icon.getImage();
            Image scaled = img.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        }
        JLabel pic = new JLabel(icon);
        pic.setHorizontalAlignment(SwingConstants.CENTER);
        pic.setVerticalAlignment(SwingConstants.CENTER);
        pic.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        pic.setBackground(Theme.BG_PANEL);
        pic.setOpaque(true);

        imgPanel.add(pic, BorderLayout.CENTER);

        // --- Bottom: table + refresh ---
        JPanel tableCard = new JPanel(new BorderLayout(4, 4));
        tableCard.setBackground(Theme.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SOFT),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        JLabel tblHeader = new JLabel("Events in Database");
        tblHeader.setForeground(Theme.TEXT_PRIMARY);
        tblHeader.setFont(tblHeader.getFont().deriveFont(Font.BOLD, 13f));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.add(tblHeader, BorderLayout.WEST);
        Theme.stylePrimaryButton(btnRefresh);
        headerRow.add(btnRefresh, BorderLayout.EAST);

        tableCard.add(headerRow, BorderLayout.NORTH);

        eventTable.setFillsViewportHeight(true);
        JScrollPane tblScroll = new JScrollPane(eventTable);
        tblScroll.getViewport().setBackground(Color.WHITE);
        tableCard.add(tblScroll, BorderLayout.CENTER);

        // stack image over table
        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setOpaque(false);

        imgPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tableCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        stack.add(imgPanel);
        stack.add(Box.createVerticalStrut(8));
        stack.add(tableCard);

        right.add(stack, BorderLayout.CENTER);
        return right;
    }

    private void styleTextField(JTextField field) {
        field.setBackground(Theme.BG_PANEL);
        field.setForeground(Theme.TEXT_PRIMARY);
        field.setCaretColor(Theme.TEXT_PRIMARY);
        field.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SOFT));
    }

    // =====================  IMAGE HELPER  =====================

    /** Load an image from the project root (same place as logo.png). */
    private ImageIcon loadRootImage(String filename) {
        Path root = Paths.get("").toAbsolutePath();
        Path imgPath = root.resolve(filename);

        if (Files.exists(imgPath)) {
            return new ImageIcon(imgPath.toString());
        } else {
            System.err.println("Missing image: " + imgPath.toAbsolutePath());
            return null;
        }
    }

    // =====================  DB LOGIC  =====================

    private void saveEvent() {
        txtStatus.setText("");

        String idText = txtId.getText().trim();
        String date   = txtDate.getText().trim();
        String topic  = txtTopic.getText().trim();

        if (idText.isEmpty() || date.isEmpty() || topic.isEmpty()) {
            txtStatus.setText("Please fill in Event ID, Date, and Topic.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException nfe) {
            txtStatus.setText("Event ID must be an integer.");
            return;
        }

        String sql = "INSERT INTO Events (Event_ID, Event_Date, Event_Topic) VALUES (?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, date);
            ps.setString(3, topic);

            int rows = ps.executeUpdate();
            txtStatus.setText("Saved " + rows + " event record(s).");
            loadEvents();  // refresh table

        } catch (SQLException ex) {
            txtStatus.setText("Failed to save event:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadEvents() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        String sql = "SELECT Event_ID, Event_Date, Event_Topic FROM Events ORDER BY Event_ID";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            tableModel.setColumnIdentifiers(new Object[]{"Event_ID", "Event_Date", "Event_Topic"});

            int count = 0;
            while (rs.next()) {
                Object[] row = new Object[]{
                        rs.getInt("Event_ID"),
                        rs.getString("Event_Date"),
                        rs.getString("Event_Topic")
                };
                tableModel.addRow(row);
                count++;
            }

            if (count == 0) {
                txtStatus.setText("Connected to DB, but Events table is empty.");
            } else {
                txtStatus.setText("Loaded " + count + " event(s) from DB.");
            }
        } catch (SQLException ex) {
            txtStatus.setText("Failed to load events. DB error:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtDate.setText("");
        txtTopic.setText("");
        txtStatus.setText("");
    }
}
