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
 * Competition entry tab.
 * Table: Competitions(Competition_ID, Competition_Name, Competition_Role,
 *                     Competition_Date, Location)
 */
public class CompetitionTab extends JPanel {

    private final JTextField txtId    = new JTextField(10);
    private final JTextField txtName  = new JTextField(22);
    private final JTextField txtRole  = new JTextField(22);
    private final JTextField txtDate  = new JTextField(12); // yyyy-MM-dd
    private final JTextField txtPlace = new JTextField(22);

    private final JButton btnSave   = new JButton("Save Competition");
    private final JButton btnClear  = new JButton("Clear");
    private final JButton btnRefresh = new JButton("Refresh Competitions");

    private final JTextArea txtStatus = new JTextArea(4, 40);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Competition_ID", "Name", "Role", "Date", "Location"}, 0
    );
    private final JTable competitionTable = new JTable(tableModel);

    public CompetitionTab() {
        Theme.styleTabBackground(this);
        setLayout(new BorderLayout(16, 16));

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setOpaque(false);

        left.add(buildHeaderBanner(), BorderLayout.NORTH);
        left.add(buildFormCard(),    BorderLayout.CENTER);

        Theme.styleStatusArea(txtStatus);
        JScrollPane statusScroll = new JScrollPane(
                txtStatus,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        left.add(statusScroll, BorderLayout.SOUTH);

        // right: table + refresh
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBackground(Theme.BG_PANEL);
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel rightHeader = new JLabel("Competitions in Database", SwingConstants.CENTER);
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

        competitionTable.setFillsViewportHeight(true);
        JScrollPane tableScroll = new JScrollPane(competitionTable);
        right.add(tableScroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.5);
        split.setContinuousLayout(true);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        btnSave.addActionListener(e -> saveCompetition());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadCompetitions());

        loadCompetitions(); // initial DB check
    }

    private JComponent buildHeaderBanner() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_PANEL);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        header.setPreferredSize(new Dimension(10, 70));

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Competitions", SwingConstants.CENTER);
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

        // ID
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblId = new JLabel("Competition ID:");
        Theme.styleFormLabel(lblId);
        card.add(lblId, gbc);
        gbc.gridx = 1;
        card.add(txtId, gbc);
        row++;

        // Name
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblName = new JLabel("Name:");
        Theme.styleFormLabel(lblName);
        card.add(lblName, gbc);
        gbc.gridx = 1;
        card.add(txtName, gbc);
        row++;

        // Role
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblRole = new JLabel("Role:");
        Theme.styleFormLabel(lblRole);
        card.add(lblRole, gbc);
        gbc.gridx = 1;
        card.add(txtRole, gbc);
        row++;

        // Date
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblDate = new JLabel("Date (yyyy-MM-dd):");
        Theme.styleFormLabel(lblDate);
        card.add(lblDate, gbc);
        gbc.gridx = 1;
        card.add(txtDate, gbc);
        row++;

        // Location
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblPlace = new JLabel("Location:");
        Theme.styleFormLabel(lblPlace);
        card.add(lblPlace, gbc);
        gbc.gridx = 1;
        card.add(txtPlace, gbc);
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

    private void saveCompetition() {
        txtStatus.setText("");

        String idText = txtId.getText().trim();
        String name   = txtName.getText().trim();
        String role   = txtRole.getText().trim();
        String date   = txtDate.getText().trim();
        String place  = txtPlace.getText().trim();

        if (idText.isEmpty() || name.isEmpty()) {
            txtStatus.setText("Competition ID and Name are required.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);

            Date sqlDate = null;
            if (!date.isEmpty()) {
                LocalDate ld = LocalDate.parse(date);
                sqlDate = Date.valueOf(ld);
            }

            String sql = "INSERT INTO Competitions " +
                    "(Competition_ID, Competition_Name, Competition_Role, Competition_Date, Location) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DBManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, role.isEmpty() ? null : role);
                if (sqlDate != null) {
                    ps.setDate(4, sqlDate);
                } else {
                    ps.setNull(4, java.sql.Types.DATE);
                }
                ps.setString(5, place.isEmpty() ? null : place);

                int rows = ps.executeUpdate();
                txtStatus.setText("Inserted " + rows + " row(s) into Competitions.");
            }

            loadCompetitions();
        } catch (NumberFormatException ex) {
            txtStatus.setText("Competition ID must be an integer.");
        } catch (Exception ex) {
            txtStatus.setText("Error:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadCompetitions() {
        tableModel.setRowCount(0);
        txtStatus.setText("");

        String sql = "SELECT Competition_ID, Competition_Name, Competition_Role, " +
                     "Competition_Date, Location FROM Competitions ORDER BY Competition_ID";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("Competition_ID"),
                        rs.getString("Competition_Name"),
                        rs.getString("Competition_Role"),
                        rs.getDate("Competition_Date"),
                        rs.getString("Location")
                });
            }

            if (tableModel.getRowCount() == 0) {
                txtStatus.setText("Connected to DB, but Competitions table is empty.");
            } else {
                txtStatus.setText("Loaded " + tableModel.getRowCount() + " competition(s) from DB.");
            }
        } catch (SQLException ex) {
            txtStatus.setText("Failed to load competitions:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtRole.setText("");
        txtDate.setText("");
        txtPlace.setText("");
        txtStatus.setText("");
    }
}
