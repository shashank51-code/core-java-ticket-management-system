import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/ticket_management";

    private static final String USERNAME = "postgres";

    private static final String PASSWORD = "2005";

    public static Connection getConnection() throws Exception {

        return DriverManager.getConnection(
                URL,
                USERNAME,
                PASSWORD
        );
    }
}