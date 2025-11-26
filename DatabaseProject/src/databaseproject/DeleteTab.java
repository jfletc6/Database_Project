import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DeleteTab extends JPanel {

    private final JComboBox<String> tableCombo = new JComboBox<>(new String[]{
            "Students",
            "Competitions",
            "Club_Meetings",
            "Events"
    });
    private final JTextField txtId = new JTextField(10);

    private final JButton btnDelete  = new JButton("Delete Record");
    private final JButton btnClear   = new JButton("Clear");
    private final JButton btnRefresh = new JButton("Refresh Table");

    private final JTextArea txtStatus = new JTextArea(4, 40);

    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable recordTable = new JTable(tableModel);

    public DeleteTab() {
        // Same base styling as your other tabs
        Theme.styleTabBackground(this);
        setLayout(new BorderLayout(16, 16));

        // ===== Left: header + form + status =====
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

        // ===== Right: table + refresh =====
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBackground(Theme.BG_PANEL);
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel rightHeader = new JLabel("Records in Database", SwingConstants.CENTER);
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

        recordTable.setFillsViewportHeight(true);
        right.add(new JScrollPane(recordTable), BorderLayout.CENTER);

        // ===== Split pane (same as EventTab) =====
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.5);
        split.setContinuousLayout(true);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        // ===== Wiring =====
        btnDelete.addActionListener(e -> deleteRecord());
        btnClear.addActionListener(e -> txtId.setText(""));
        btnRefresh.addActionListener(e -> loadTable());
        tableCombo.addActionListener(e -> loadTable());

        // Initial load
        loadTable();
    }

    // ---------- UI builders ----------

    private JComponent buildHeaderBanner() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_PANEL);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        header.setPreferredSize(new Dimension(10, 70));

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Delete Records", SwingConstants.CENTER);
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
        gbc.weightx = 1.0;

        int row = 0;

        // Table selector
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblTable = new JLabel("Table:");
        Theme.styleFormLabel(lblTable);
        card.add(lblTable, gbc);
        gbc.gridx = 1;
        card.add(tableCombo, gbc);
        row++;

        // ID field
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblId = new JLabel("Primary Key ID:");
        Theme.styleFormLabel(lblId);
        card.add(lblId, gbc);
        gbc.gridx = 1;
        card.add(txtId, gbc);
        row++;

        // Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonRow.setOpaque(false);
        Theme.stylePrimaryButton(btnDelete);
        Theme.styleSecondaryButton(btnClear);
        buttonRow.add(btnDelete);
        buttonRow.add(btnClear);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        card.add(buttonRow, gbc);

        return card;
    }

    // ---------- DB helpers ----------

    private String primaryKeyFor(String tableName) {
        if ("Students".equals(tableName))       return "Student_ID";
        if ("Competitions".equals(tableName))   return "Competition_ID";
        if ("Club_Meetings".equals(tableName))  return "Meeting_ID";
        if ("Events".equals(tableName))         return "Event_ID";
        return "id";
    }

    private void loadTable() {
        String tableName = (String) tableCombo.getSelectedItem();
        if (tableName == null) return;

        txtStatus.setText("Loading " + tableName + "...");
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        String sql = "SELECT * FROM " + tableName + " ORDER BY 1";

        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            String[] headers = new String[cols];
            for (int i = 1; i <= cols; i++) {
                headers[i - 1] = meta.getColumnLabel(i);
            }
            tableModel.setColumnIdentifiers(headers);

            int count = 0;
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 1; i <= cols; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
                count++;
            }

            if (count == 0) {
                txtStatus.setText("Connected to DB, but " + tableName + " is empty.");
            } else {
                txtStatus.setText("Loaded " + count + " record(s) from " + tableName + ".");
            }
        } catch (SQLException ex) {
            txtStatus.setText("Failed to load " + tableName + ":\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteRecord() {
        txtStatus.setText("");

        String tableName = (String) tableCombo.getSelectedItem();
        if (tableName == null) {
            txtStatus.setText("Select a table first.");
            return;
        }

        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            txtStatus.setText("Enter an ID to delete.");
            return;
        }

        String pk = primaryKeyFor(tableName);
        String sql = "DELETE FROM " + tableName + " WHERE " + pk + " = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int id = Integer.parseInt(idText);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            txtStatus.setText("Deleted " + rows + " row(s) from " + tableName
                    + " where " + pk + " = " + id + ".");
            loadTable();  // refresh view
        } catch (NumberFormatException nfe) {
            txtStatus.setText("ID must be an integer.");
        } catch (SQLException ex) {
            txtStatus.setText("Failed to delete record:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
