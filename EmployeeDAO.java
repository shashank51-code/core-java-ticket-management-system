import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmployeeDAO {

    public static void addEmployee(
            String name,
            String email,
            String phone,
            String department,
            String role) {

        String sql = """
                INSERT INTO employees
                (employee_name, email, phone, department, role)
                VALUES (?, ?, ?, ?, ?)
                """;

        try {

            Connection connection = DBConnection.getConnection();

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, department);
            ps.setString(5, role);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Employee added successfully!");
            }

            ps.close();
            connection.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static void getAllEmployees() {

        String sql =
                "SELECT * FROM employees ORDER BY employee_id";

        try {

            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                System.out.println(
                        "Employee ID: "
                        + rs.getInt("employee_id"));

                System.out.println(
                        "Name: "
                        + rs.getString("employee_name"));

                System.out.println(
                        "Email: "
                        + rs.getString("email"));

                System.out.println(
                        "Phone: "
                        + rs.getString("phone"));

                System.out.println(
                        "Department: "
                        + rs.getString("department"));

                System.out.println(
                        "Role: "
                        + rs.getString("role"));

                System.out.println("----------------------");
            }

            rs.close();
            ps.close();
            connection.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static void getEmployeeById(int employeeId) {

        String sql =
                "SELECT * FROM employees WHERE employee_id = ?";

        try {

            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ps.setInt(1, employeeId);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                System.out.println(
                        "Employee ID: "
                        + rs.getInt("employee_id"));

                System.out.println(
                        "Name: "
                        + rs.getString("employee_name"));

                System.out.println(
                        "Email: "
                        + rs.getString("email"));

                System.out.println(
                        "Phone: "
                        + rs.getString("phone"));

                System.out.println(
                        "Department: "
                        + rs.getString("department"));

                System.out.println(
                        "Role: "
                        + rs.getString("role"));

            } else {

                System.out.println(
                        "Employee not found.");
            }

            rs.close();
            ps.close();
            connection.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static void updateDepartment(
            int employeeId,
            String department) {

        String sql = """
                UPDATE employees
                SET department = ?
                WHERE employee_id = ?
                """;

        try {

            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ps.setString(1, department);
            ps.setInt(2, employeeId);

            int rows =
                    ps.executeUpdate();

            if (rows > 0) {

                System.out.println(
                        "Employee department updated successfully!");

            } else {

                System.out.println(
                        "Employee not found.");
            }

            ps.close();
            connection.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static void deleteEmployee(
            int employeeId) {

        String sql =
                "DELETE FROM employees WHERE employee_id = ?";

        try {

            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ps.setInt(1, employeeId);

            int rows =
                    ps.executeUpdate();

            if (rows > 0) {

                System.out.println(
                        "Employee deleted successfully!");

            } else {

                System.out.println(
                        "Employee not found.");
            }

            ps.close();
            connection.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static Employee findById(int employeeId) {

    String sql =
            "SELECT * FROM employees WHERE employee_id = ?";

    try {

        Connection connection =
                DBConnection.getConnection();

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, employeeId);

        ResultSet rs =
                ps.executeQuery();

        if (rs.next()) {

            Employee employee = new Employee(
                    rs.getString("employee_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("department"),
                    rs.getString("role"),
                    null
            );

            rs.close();
            ps.close();
            connection.close();

            return employee;
        }

        rs.close();
        ps.close();
        connection.close();

        return null;

    } catch (Exception e) {

        e.printStackTrace();

        return null;
    }
}

}