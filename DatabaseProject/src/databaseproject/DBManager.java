import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    private static final String HOST = "triton.towson.edu";
    private static final int    PORT = 3360;

    private static final String USER = "jfletc6";
    private static final String PASS = "COSC*omh0d";

    // Correct Triton database name
    private static final String DBNAME = "jfletc6db";

    // Correct MySQL-style JDBC URL (Triton requirement)
    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DBNAME;

    // Load MySQL connector (Triton supports this one)
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private DBManager() {}
}
