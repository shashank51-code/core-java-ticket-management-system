
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TicketDAO {

  

    public static void addTicketHistory(
        int ticketId,
        String action,
        int performedBy) {

    String sql = """
            INSERT INTO ticket_history
            (ticket_id, action, performed_by)
            VALUES (?, ?, ?)
            """;

    try {

        Connection connection = DBConnection.getConnection();

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, ticketId);
        ps.setString(2, action);
        ps.setInt(3, performedBy);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("Ticket history added successfully!");
        }

        ps.close();
        connection.close();

    } catch (Exception e) {

        e.printStackTrace();

    }
}


public static boolean isValidStatusTransition(
        String currentStatus,
        String newStatus) {

    if (currentStatus.equalsIgnoreCase("OPEN")
            && newStatus.equalsIgnoreCase("ASSIGNED")) {

        return true;
    }

    if (currentStatus.equalsIgnoreCase("ASSIGNED")
            && newStatus.equalsIgnoreCase("IN_PROGRESS")) {

        return true;
    }

    if (currentStatus.equalsIgnoreCase("IN_PROGRESS")
            && newStatus.equalsIgnoreCase("RESOLVED")) {

        return true;
    }

    if (currentStatus.equalsIgnoreCase("RESOLVED")
            && newStatus.equalsIgnoreCase("CLOSED")) {

        return true;
    }

    return false;
}
public static void updateTicketStatus(
        int ticketId,
        String newStatus,
        int performedBy) {

    String selectSql = """
            SELECT status
            FROM tickets
            WHERE ticket_id = ?
            """;

    String updateSql = """
            UPDATE tickets
            SET status = ?
            WHERE ticket_id = ?
            """;

    String historySql = """
            INSERT INTO ticket_history
            (ticket_id, action, performed_by)
            VALUES (?, ?, ?)
            """;

    Connection connection = null;

    try {

        connection = DBConnection.getConnection();

        // Start transaction
        connection.setAutoCommit(false);

        // 1. Get current status
        PreparedStatement selectPs =
                connection.prepareStatement(selectSql);

        selectPs.setInt(1, ticketId);

        ResultSet rs = selectPs.executeQuery();

        if (!rs.next()) {

            System.out.println("Ticket not found.");

            rs.close();
            selectPs.close();

            connection.rollback();
            connection.close();

            return;
        }

        String currentStatus =
                rs.getString("status");

        rs.close();
        selectPs.close();

        // 2. Validate transition
        if (!isValidStatusTransition(
                currentStatus,
                newStatus)) {

            System.out.println(
                    "Invalid status transition: "
                    + currentStatus
                    + " -> "
                    + newStatus
            );

            connection.rollback();
            connection.close();

            return;
        }

        // 3. Update ticket
        PreparedStatement updatePs =
                connection.prepareStatement(updateSql);

        updatePs.setString(1, newStatus);
        updatePs.setInt(2, ticketId);

        int rows = updatePs.executeUpdate();

        updatePs.close();

        if (rows == 0) {

            System.out.println("Ticket update failed.");

            connection.rollback();
            connection.close();

            return;
        }

        // 4. Insert history
        PreparedStatement historyPs =
                connection.prepareStatement(historySql);

        historyPs.setInt(1, ticketId);
        historyPs.setString(2, newStatus);
        historyPs.setInt(3, performedBy);

        historyPs.executeUpdate();

        historyPs.close();

        // 5. Save both operations
        connection.commit();

        System.out.println(
                "Ticket status updated successfully!"
        );

        System.out.println(
                "Ticket history added successfully!"
        );

        connection.close();

    } catch (Exception e) {

        try {

            if (connection != null) {
                connection.rollback();
            }

        } catch (Exception rollbackException) {

            rollbackException.printStackTrace();
        }

        System.out.println(
                "Transaction failed. Changes rolled back."
        );

        e.printStackTrace();
    }
}
    public static boolean isTicketAlreadyAssigned(int ticketId) {

    String sql = """
            SELECT assigned_to, status
            FROM tickets
            WHERE ticket_id = ?
            """;

    try {

        Connection connection = DBConnection.getConnection();

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, ticketId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            Object assignedTo = rs.getObject("assigned_to");
            String status = rs.getString("status");

            rs.close();
            ps.close();
            connection.close();

            if (assignedTo != null ||
                    status.equalsIgnoreCase("ASSIGNED")) {

                return true;
            }

            return false;
        }

        rs.close();
        ps.close();
        connection.close();

        return false;

    } catch (Exception e) {

        e.printStackTrace();
        return false;
    }
}
    public static boolean isSupportEmployee(int employeeId) {

    String sql = "SELECT role FROM employees WHERE employee_id = ?";

    try {

        Connection connection = DBConnection.getConnection();

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, employeeId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            String role = rs.getString("role");

            rs.close();
            ps.close();
            connection.close();

            return role.equalsIgnoreCase("SUPPORT");
        }

        rs.close();
        ps.close();
        connection.close();

        return false;

    } catch (Exception e) {

        e.printStackTrace();
        return false;
    }
}
  
    public static void assignTicket(int ticketId, int supportEmployeeId) {

    if (!isSupportEmployee(supportEmployeeId)) {

        System.out.println("Employee is not a SUPPORT employee.");
        return;
    }

    if (isTicketAlreadyAssigned(ticketId)) {

        System.out.println("Ticket is already assigned.");
        return;
    }

    String sql = """
            UPDATE tickets
            SET assigned_to = ?,
                status = 'ASSIGNED'
            WHERE ticket_id = ?
            """;

    try {

        Connection connection = DBConnection.getConnection();

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, supportEmployeeId);
        ps.setInt(2, ticketId);

        int rows = ps.executeUpdate();

        if (rows > 0) {

            System.out.println("Ticket assigned successfully!");

        } else {

            System.out.println("Ticket not found.");

        }

        ps.close();
        connection.close();

    } catch (Exception e) {

        e.printStackTrace();

    }
}
public static void getAllTickets() {

    String sql = "SELECT * FROM tickets ORDER BY ticket_id";

    try {

        Connection connection = DBConnection.getConnection();

        PreparedStatement ps = connection.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            System.out.println("Ticket ID: " +
                    rs.getInt("ticket_id"));

            System.out.println("Title: " +
                    rs.getString("title"));

            System.out.println("Description: " +
                    rs.getString("description"));

            System.out.println("Priority: " +
                    rs.getString("priority"));

            System.out.println("Status: " +
                    rs.getString("status"));

            System.out.println("Created By: " +
                    rs.getInt("created_by"));

            System.out.println("Assigned To: " +
                    rs.getObject("assigned_to"));

            System.out.println("---------------------------");
        }

        rs.close();
        ps.close();
        connection.close();

    } catch (Exception e) {

        e.printStackTrace();

    }
}
    public static void createTicket(
            String title,
            String description,
            String priority,
            int createdBy) {

        String sql = """
                INSERT INTO tickets
                (title, description, priority, created_by)
                VALUES (?, ?, ?, ?)
                """;

        try {

            Connection connection = DBConnection.getConnection();

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, priority);
            ps.setInt(4, createdBy);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Ticket created successfully!");
            } else {
                System.out.println("Ticket creation failed.");
            }

            ps.close();
            connection.close();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
    public static void getTicketById(int ticketId) {

    String sql = "SELECT * FROM tickets WHERE ticket_id = ?";

    try {

        Connection connection = DBConnection.getConnection();

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, ticketId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            System.out.println("Ticket ID: " +
                    rs.getInt("ticket_id"));

            System.out.println("Title: " +
                    rs.getString("title"));

            System.out.println("Description: " +
                    rs.getString("description"));

            System.out.println("Priority: " +
                    rs.getString("priority"));

            System.out.println("Status: " +
                    rs.getString("status"));

            System.out.println("Created By: " +
                    rs.getInt("created_by"));

            System.out.println("Assigned To: " +
                    rs.getObject("assigned_to"));

        } else {

            System.out.println("Ticket not found.");

        }

        rs.close();
        ps.close();
        connection.close();

    } catch (Exception e) {

        e.printStackTrace();

    }
}

public static void getMyTickets(int employeeId) {

    String sql = """
            SELECT ticket_id, title, description,
                   priority, status, assigned_to
            FROM tickets
            WHERE created_by = ?
            ORDER BY ticket_id
            """;

    try {

        Connection connection =
                DBConnection.getConnection();

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, employeeId);

        ResultSet rs =
                ps.executeQuery();

        boolean found = false;

        while (rs.next()) {

            found = true;

            System.out.println("---------------------------");

            System.out.println(
                    "Ticket ID: "
                    + rs.getInt("ticket_id"));

            System.out.println(
                    "Title: "
                    + rs.getString("title"));

            System.out.println(
                    "Description: "
                    + rs.getString("description"));

            System.out.println(
                    "Priority: "
                    + rs.getString("priority"));

            System.out.println(
                    "Status: "
                    + rs.getString("status"));

            System.out.println(
                    "Assigned To: "
                    + rs.getObject("assigned_to"));
        }

        if (!found) {

            System.out.println(
                    "No tickets found.");
        }

        rs.close();
        ps.close();
        connection.close();

    } catch (Exception e) {

        e.printStackTrace();
    }
}
}