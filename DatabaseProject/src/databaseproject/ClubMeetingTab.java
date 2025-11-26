import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Club meeting entry tab.
 * Table: Club_Meetings(Meeting_Date, Meeting_Topic)
 */
public class ClubMeetingTab extends JPanel {

    private final JTextField txtDate  = new JTextField(12); // yyyy-MM-dd
    private final JTextField txtTopic = new JTextField(28);

    private final JButton btnSave    = new JButton("Save Meeting");
    private final JButton btnClear   = new JButton("Clear");
    private final JButton btnRefresh = new JButton("Refresh Meetings");

    private final JTextArea txtStatus = new JTextArea(4, 40);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Meeting_Date", "Meeting_Topic"}, 0
    );
    private final JTable meetingTable = new JTable(tableModel);

    public ClubMeetingTab() {
        Theme.styleTabBackground(this);
        setLayout(new BorderLayout(16, 16));

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setOpaque(false);

        left.add(buildHeaderBanner(), BorderLayout.NORTH);
        left.add(buildFormCard(),     BorderLayout.CENTER);

        Theme.styleStatusArea(txtStatus);
        JScrollPane statusScroll = new JScrollPane(
                txtStatus,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        left.add(statusScroll, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBackground(Theme.BG_PANEL);
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel rightHeader = new JLabel("Club Meetings in Database", SwingConstants.CENTER);
        rightHeader.setForeground(Theme.TEXT_PRIMARY);
        rightHeader.setFont(rightHeader.getFont().deriveFont(Font.BOLD, 14f));

        JPanel rightHeaderPanel = new JPanel(new BorderLayout());
        rightHeaderPanel.setOpaque(false);
        rightHeaderPanel.add(rightHeader, BorderLayout.CENTER);

        Theme.stylePrimaryButton(btnRefresh);
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        refreshPanel.setOpaque(false);
        refreshPanel.add(btnRefresh);
        rightHeaderPanel.add(refreshPanel, BorderLayout.EAST);

        right.add(rightHeaderPanel, BorderLayout.NORTH);

        meetingTable.setFillsViewportHeight(true);
        right.add(new JScrollPane(meetingTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.5);
        split.setContinuousLayout(true);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        btnSave.addActionListener(e -> saveMeeting());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadMeetings());

        loadMeetings();
    }

    private JComponent buildHeaderBanner() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_PANEL);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        header.setPreferredSize(new Dimension(10, 70));

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Club Meetings", SwingConstants.CENTER);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
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
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Date
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblDate = new JLabel("Meeting Date (yyyy-MM-dd):");
        Theme.styleFormLabel(lblDate);
        card.add(lblDate, gbc);
        gbc.gridx = 1;
        card.add(txtDate, gbc);
        row++;

        // Topic
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblTopic = new JLabel("Meeting Topic:");
        Theme.styleFormLabel(lblTopic);
        card.add(lblTopic, gbc);
        gbc.gridx = 1;
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

    private void saveMeeting() {
        txtStatus.setText("");

        String dateText = txtDate.getText().trim();
        String topic    = txtTopic.getText().trim();

        if (dateText.isEmpty()) {
            txtStatus.setText("Meeting Date is required.");
            return;
        }

        try {
            LocalDate ld = LocalDate.parse(dateText);
            Date sqlDate = Date.valueOf(ld);

            String sql = "INSERT INTO Club_Meetings (Meeting_Date, Meeting_Topic) VALUES (?, ?)";

            try (Connection conn = DBManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setDate(1, sqlDate);
                ps.setString(2, topic.isEmpty() ? null : topic);

                int rows = ps.executeUpdate();
                txtStatus.setText("Inserted " + rows + " row(s) into Club_Meetings.");
            }

            loadMeetings();
        } catch (Exception ex) {
            txtStatus.setText("Error:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadMeetings() {
        tableModel.setRowCount(0);
        txtStatus.setText("");

        String sql = "SELECT Meeting_Date, Meeting_Topic FROM Club_Meetings ORDER BY Meeting_Date";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getDate("Meeting_Date"),
                        rs.getString("Meeting_Topic")
                });
            }

            if (tableModel.getRowCount() == 0) {
                txtStatus.setText("Connected to DB, but Club_Meetings table is empty.");
            } else {
                txtStatus.setText("Loaded " + tableModel.getRowCount() + " meeting(s) from DB.");
            }
        } catch (SQLException ex) {
            txtStatus.setText("Failed to load meetings:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        txtDate.setText("");
        txtTopic.setText("");
        txtStatus.setText("");
    }
}
