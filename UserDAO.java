import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public static void main(String[] args) {
int employeeId =
            getEmployeeIdByLogin(
    "testuser",
    "wrong"
);

    System.out.println(
            "Employee ID: " + employeeId);

}
    public static boolean login(
            String username,
            String password) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ? ";

        try {

            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                System.out.println(
                        "Login successful!");

                System.out.println(
                        "Employee ID: "
                        + rs.getInt("employee_id"));

                rs.close();
                ps.close();
                connection.close();

                return true;
            }

            rs.close();
            ps.close();
            connection.close();

            System.out.println(
                    "Invalid username or password.");

            return false;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    public static void signup(
        String name,
        String email,
        String phone,
        String department,
        String role,
        String username,
        String password) {

    String employeeSql = """
            INSERT INTO employees
            (employee_name, email, phone, department, role)
            VALUES (?, ?, ?, ?, ?)
            RETURNING employee_id
            """;

    String userSql = """
            INSERT INTO users
            (employee_id, username, password)
            VALUES (?, ?, ?)
            """;

    Connection connection = null;

    try {

        connection = DBConnection.getConnection();

        connection.setAutoCommit(false);

        // 1. Insert employee
        PreparedStatement employeePs =
                connection.prepareStatement(employeeSql);

        employeePs.setString(1, name);
        employeePs.setString(2, email);
        employeePs.setString(3, phone);
        employeePs.setString(4, department);
        employeePs.setString(5, role);

        ResultSet rs =
                employeePs.executeQuery();

        if (!rs.next()) {

            connection.rollback();

            System.out.println(
                    "Employee creation failed.");

            rs.close();
            employeePs.close();
            connection.close();

            return;
        }

        int employeeId =
                rs.getInt("employee_id");

        rs.close();
        employeePs.close();

        // 2. Insert user
        PreparedStatement userPs =
                connection.prepareStatement(userSql);

        userPs.setInt(1, employeeId);
        userPs.setString(2, username);
        userPs.setString(3, password);

        userPs.executeUpdate();

        userPs.close();

        // 3. Save both operations
        connection.commit();

        connection.close();

        System.out.println(
                "Signup successful!");

        System.out.println(
                "Employee ID: " + employeeId);

    } catch (Exception e) {

        try {

            if (connection != null) {
                connection.rollback();
            }

        } catch (Exception rollbackException) {

            rollbackException.printStackTrace();
        }

        System.out.println(
                "Signup failed. Changes rolled back.");

        e.printStackTrace();
    }
}


public static int getEmployeeIdByLogin(
        String username,
        String password) {

    String sql = """
            SELECT employee_id
            FROM users
            WHERE username = ?
            AND password = ?
            """;

    try {

        Connection connection =
                DBConnection.getConnection();

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs =
                ps.executeQuery();

        if (rs.next()) {

            int employeeId =
                    rs.getInt("employee_id");

            rs.close();
            ps.close();
            connection.close();

            return employeeId;
        }

        rs.close();
        ps.close();
        connection.close();

        return -1;

    } catch (Exception e) {

        e.printStackTrace();

        return -1;
    }
}
}