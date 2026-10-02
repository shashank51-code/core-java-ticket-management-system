
import java.util.InputMismatchException;
import java.util.Scanner;
class User {

   
    int employeeId;
    String username;
    String password;
  
    

    public User(int employeeId,String username,String password)
    {
        this.employeeId=employeeId;
        this.username=username;
        this.password=password;
    }

    void signup(Application app)
{
    System.out.println("================signup==============");
    System.out.println();
    System.out.println();
    System.out.println();

    System.out.println("username: ");
    String username = app.sc.next();

    boolean usernameExists = false;

    for(User existingUser : app.users)
    {
        if(existingUser != null)
        {
            if(username.equalsIgnoreCase(existingUser.username))
            {
                usernameExists = true;
                break;
            }
        }
    }

    if(usernameExists == true)
    {
        System.out.println("username already exists");
    }
    else
    {
        // Check User capacity
        boolean userSlotAvailable = false;

        for(User user : app.users)
        {
            if(user == null)
            {
                userSlotAvailable = true;
                break;
            }
        }

        if(!userSlotAvailable)
        {
            System.out.println("User capacity full");
            return;
        }


        // Check Employee capacity
        boolean employeeSlotAvailable = false;

        for(Employee employee : app.employees)
        {
            if(employee == null)
            {
                employeeSlotAvailable = true;
                break;
            }
        }

        if(!employeeSlotAvailable)
        {
            System.out.println("Employee capacity full");
            return;
        }


        System.out.println("password: ");
        String userpassword = app.sc.next();

        System.out.println("Name: ");
        app.sc.nextLine();
        String name = app.sc.nextLine();
        String useremail = "";

        while(true)
        {
            System.out.println("Email: ");
            useremail = app.sc.next();
            if(useremail.contains("@") && useremail.contains("."))
            {
                break;
            }
            else
            {
                System.out.println("invalid email, try again");
            }
        }
       
        String phone="";
        while (true) 
        { 
            System.out.println("phone: ");
            phone= app.sc.next();
            if(phone.matches("[0-9]{10}"))
            {
                break;
            }
            else
            {
                System.err.println("Invalid phone number. Enter exactly 10 digits.");
            }
        }
        

        System.out.println("Department: ");
        String departent = app.sc.next();

        int n = 0;
        String role = "";

        while(true)
        {
            System.out.println("Select Role \n 1. Employee \n 2. Support Employee \n 3. Manager ");

            try
            {
                n = app.sc.nextInt();

                if(n == 1)
                {
                    role = "EMPLOYEE";
                    break;
                }
                else if(n == 2)
                {
                    role = "SUPPORT";
                    break;
                }
                else if(n == 3)
                {
                    role = "MANAGER";
                    break;
                }
                else
                {
                    System.out.println("invalid input");
                }
            }
            catch(InputMismatchException e)
            {
                System.out.println("Please enter numbers only.");
                app.sc.nextLine();
            }
        }


        // ==========================================
        // MANAGER CAPACITY CHECK CHEYATANIKI
        // ==========================================

        if(role.equals("MANAGER"))
        {
            boolean managerSlotAvailable = false;

            for(Manager manager : app.managers)
            {
                if(manager == null)
                {
                    managerSlotAvailable = true;
                    break;
                }
            }

            if(!managerSlotAvailable)
            {
                System.out.println("Manager capacity full");
                return;
            }
        }


        // ==========================================
        // SUPPORT CAPACITY CHECK CHEYATANIKI
        // ==========================================

        if(role.equals("SUPPORT"))
        {
            // Application support team capacity cheyaniki

            boolean supportSlotAvailable = false;

            for(Employee supportEmployee : app.supportTeam)
            {
                if(supportEmployee == null)
                {
                    supportSlotAvailable = true;
                    break;
                }
            }

            if(!supportSlotAvailable)
            {
                System.out.println("Support employee capacity is full");
                return;
            }


            // Find matching Manager 

            boolean managerFound = false;

            for(Manager manager : app.managers)
            {
                if(manager != null)
                {
                    if(manager.department.equalsIgnoreCase(departent))
                    {
                        managerFound = true;

                        // Manager support team capacity chudaniki

                        boolean managerSupportSlotAvailable = false;

                        for(Employee supportEmployee : manager.supportTeam)
                        {
                            if(supportEmployee == null)
                            {
                                managerSupportSlotAvailable = true;
                                break;
                            }
                        }

                        if(!managerSupportSlotAvailable)
                        {
                            System.out.println("Support team capacity is full for this department");
                            return;
                        }

                        break;
                    }
                }
            }

            if(!managerFound)
            {
                System.out.println("Manager not found for this department");
                return;
            }
        }


        // ==========================================
        // EMPLOYEE CAPACITY CHECK CHEYATANIKI
        // ==========================================

        if(role.equals("EMPLOYEE"))
        {
            boolean managerFound = false;

            for(Manager manager : app.managers)
            {
                if(manager != null)
                {
                    if(manager.department.equalsIgnoreCase(departent))
                    {
                        managerFound = true;

                        // Manager employee related capacity checking

                        boolean managerEmployeeSlotAvailable = false;

                        for(Employee emp : manager.employee)
                        {
                            if(emp == null)
                            {
                                managerEmployeeSlotAvailable = true;
                                break;
                            }
                        }

                        if(!managerEmployeeSlotAvailable)
                        {
                            System.out.println("Employee roster capacity is full for this department");
                            return;
                        }

                        break;
                    }
                }
            }

            if(!managerFound)
            {
                System.out.println("Manager not found for this department");
                return;
            }
        }


        // ==========================================
        // CREATE EMPLOYEE
        // ==========================================

        Employee employee = new Employee(name, useremail, phone, departent, role, app.sc);


        // ==========================================
        // ADDING  TO APPLICATION EMPLOYEE ARRAY 
        // ==========================================

        for(int i = 0; i < app.employees.length; i++)
        {
            if(app.employees[i] == null)
            {
                app.employees[i] = employee;

                // Add employee to matching department manager 

                for(Manager manager : app.managers)
                {
                    if(manager != null)
                    {
                        if(manager.department.equalsIgnoreCase(employee.getDepartment()))
                        {
                            if(employee.getRole().equals("EMPLOYEE"))
                            {
                                manager.addEmployee(employee);
                            }

                            break;
                        }
                    }
                }

                break;
            }
        }


        // ==========================================
        // SUPPORT EMPLOYEE RELATED
        // ==========================================

        if(role.equals("SUPPORT"))
        {
            for(int i = 0; i < app.supportTeam.length; i++)
            {
                if(app.supportTeam[i] == null)
                {
                    app.supportTeam[i] = employee;

                    for(Manager manager : app.managers)
                    {
                        if(manager != null)
                        {
                            if(manager.department.equalsIgnoreCase(employee.getDepartment()))
                            {
                                manager.addSupportEmployee(employee);
                                break;
                            }
                        }
                    }

                    break;
                }
            }
        }


        // ==========================================
        // MANAGER REALTED
        // ==========================================

        else if(role.equals("MANAGER"))
        {
            int employeeId = employee.getEmployeeId();

            Manager manager = new Manager(employeeId, name, useremail, departent, role, app.sc);


            for(int i = 0; i < app.managers.length; i++)
            {
                if(app.managers[i] == null)
                {
                    app.managers[i] = manager;

                    // Add existing employees/support employees from same department
                 

                    for(Employee emp : app.employees)
                    {
                        if(emp != null)
                        {
                            if(manager.department.equalsIgnoreCase(emp.getDepartment()))
                            {
                                if(emp.getRole().equals("SUPPORT"))
                                {
                                    manager.addSupportEmployee(emp);
                                }
                                else
                                {
                                    manager.addEmployee(emp);
                                }
                            }
                        }
                    }

                    break;
                }
            }
        }


        // ==========================================
        // CREATEING USER
        // ==========================================

        int employeeId = employee.getEmployeeId();

        User user = new User(employeeId, username, userpassword);


        for(int i = 0; i < app.users.length; i++)
        {
            if(app.users[i] == null)
            {
                app.users[i] = user;
                break;
            }
        }


        System.out.println("Signup successful!");
        System.out.println("Employee ID: " + employee.getEmployeeId());
    }
}
    void login(Application app)
    {
        System.out.println("enter username");
        String loginUsername=app.sc.next();
        System.out.println("enter password");
        String password=app.sc.next();
        boolean loginSuccess = false;
        boolean employeeFound = false;
        for(int i=0;i<=app.users.length-1;i++)
        {
            if(app.users[i] !=null)
            {
                if(loginUsername.equalsIgnoreCase(app.users[i].username) && password.equals(app.users[i].password))
                {
                    
                    int employeeId=app.users[i].employeeId;
                    for(Employee emp:app.employees)
                    {
                        if(emp !=null)
                        {           

                            if(emp.getEmployeeId()==employeeId)
                            {
                                
                                 employeeFound = true;
                                 loginSuccess=true;
                                String role=emp.getRole();
                               
                                if(role.equals("EMPLOYEE"))
                                {
                                    emp.showEmployeeMenu();
                                    break;
                                }
                                else if(role.equals("SUPPORT"))
                                {
                                    boolean managerfound=false;
                                    for(Manager manager : app.managers)
                                    {
                                        if(manager != null)
                                        {
                                            if(manager.department.equalsIgnoreCase(emp.getDepartment()))
                                            {
                                                managerfound=true;
                                                emp.showSupportMenu(manager);
                                                break;
                                            }
                                           
                                        }
                                    }
                                    if(!managerfound)
                                    {
                                        System.out.println("Support manager not found");
                                    }
                                }
                            
                                else if(role.equals("MANAGER"))
                                {
                                    boolean managerfound=false;
                                    for(Manager manager : app.managers)
                                        {
                                            if(manager != null)
                                            {
                                                if(manager.managerId == employeeId)
                                                {
                                                    managerfound=true;
                                                    manager.showManagerMenu(app);
                                                    break;
                                                }
                                            }
                                        }
                                        if(!managerfound)
                                        {
                                            System.out.println("Manager account not found");
                                        }
                                
                                }
                            }
                        }    
                    }
                    if(!employeeFound)
                    {
                        System.out.println("Employee profile not found for this account");
                    }
                }
               
            }
        }
        if(!loginSuccess && !employeeFound)
        {
            System.out.println("Invalid username or password");
        }
    }
    public static void loginFromDatabase(Application app) {

    Scanner sc = app.sc;

    System.out.print("Enter username: ");
    String username = sc.next();

    System.out.print("Enter password: ");
    String password = sc.next();

    boolean valid =
            UserDAO.login(username, password);

    if (!valid) {

        return;
    }

    int employeeId =
            UserDAO.getEmployeeIdByLogin(
                    username,
                    password);

    if (employeeId == -1) {

        System.out.println(
                "Employee profile not found.");

        return;
    }

    Employee employee =
            EmployeeDAO.findById(employeeId);
    if (employee.getRole().equalsIgnoreCase("EMPLOYEE")) {

    showDatabaseEmployeeMenu(app, employee);

}
    if (employee == null) {

        System.out.println(
                "Employee profile not found.");

        return;
    }

    System.out.println(
            "Welcome, "
            + employee.getEmployeeName());

    System.out.println(
            "Role: "
            + employee.getRole());

            if (employee.getRole()
        .equalsIgnoreCase("EMPLOYEE")) {

    showDatabaseEmployeeMenu(app, employee);
}
}


public static void showDatabaseEmployeeMenu(
        Application app,
        Employee employee) {

    Scanner sc = app.sc;

    while (true) {

        System.out.println();
        System.out.println("==============================");
        System.out.println("       EMPLOYEE MENU");
        System.out.println("==============================");

        System.out.println("1. View My Tickets");
        System.out.println("2. View Ticket By ID");
        System.out.println("3. Logout");

        System.out.print("Enter your choice: ");

        int choice = sc.nextInt();

        if (choice == 1) {

            TicketDAO.getMyTickets(
                    employee.getEmployeeId());

        } else if (choice == 2) {

            System.out.print(
                    "Enter ticket ID: ");

            int ticketId = sc.nextInt();

            TicketDAO.getTicketById(ticketId);

        } else if (choice == 3) {

            System.out.println(
                    "Logged out successfully.");

            break;

        } else {

            System.out.println(
                    "Invalid choice.");
        }
    }
}


}