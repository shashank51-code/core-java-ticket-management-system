
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
class Manager {

    int managerId;
    String managerName;
    String email;
    String department;
    String role;
    private Scanner sc;
    Employee[] supportTeam = new Employee[5];
    Employee[] employee=new Employee[10];
    public Manager(int managerId, String managerName, String email, String department, String role,Scanner sc) {
        this.managerId = managerId;
        this.managerName = managerName;
        this.email = email;
        this.department = department;
        this.role = role;

        this.sc=sc;
    }

    void displayManager() {
        System.out.println("====MANAGER DETAILS====");
        System.out.println("ManagerId: " + managerId);
        System.out.println("ManagerName: " + managerName);
        System.out.println("Email: " + email);
        System.out.println("Department: " + department);
        System.out.println("Role: " + role);

    }

    void viewTicket(Employee employee) {
        boolean found=false;
        for(int i=0;i<5;i++)
        {
            if(employee.tickets[i] != null)
            {
                employee.tickets[i].displayTicket();
                found=true;
            }
        }
        if(!found) {
            System.out.println("No tickets found");
        }
    }

    void assignTicket(Ticket ticket, Employee supportEmployee) {
    if ("SUPPORT".equals(supportEmployee.getRole())) {

        if (department.equalsIgnoreCase(supportEmployee.getDepartment())) {

            boolean added = false;

            for (int i = 0; i < supportEmployee.tickets.length; i++) {
                if (supportEmployee.tickets[i] == null) {
                    supportEmployee.tickets[i] = ticket;
                    added = true;
                    break;
                }
            }

            if (added) {
                ticket.assignedTo = supportEmployee;

                if (ticket.updateStatus("ASSIGNED", managerName)) {
                    System.out.println("Ticket assigned successfully.");
                    System.out.println("Assigned To: " + supportEmployee.getEmployeeName());
                }
            } else {
                System.out.println("Support employee ticket list is full.");
            }

        } else {
            System.out.println("Cannot assign ticket to different department");
        }

    } else {
        System.out.println("Employee is not a SUPPORT employee.");
        System.out.println("Ticket was not assigned.");
    }
}
    void reassignTicket(Ticket ticket, Employee newSupportEmployee)
    {
    if("SUPPORT".equals(newSupportEmployee.getRole()))
    {
        if(ticket.status.equals("CLOSED") || ticket.status.equals("RESOLVED"))
        {
            System.out.println("Resolved or closed ticket cannot be reassigned");
        }
        else
        {
            if(ticket.assignedTo != null)
            {
                Employee oldSupportEmployee = ticket.assignedTo;

                System.out.println("Old: \n Assigned To: "
                                   + oldSupportEmployee.getEmployeeName());

                if(oldSupportEmployee == newSupportEmployee)
                {
                    System.out.println("Cannot reassign to the same employee.");
                }
                else
                {
                    if(department.equalsIgnoreCase(newSupportEmployee.getDepartment()))
                    {
                        boolean added = false;

                        for(int i = 0; i < newSupportEmployee.tickets.length; i++)
                        {
                            if(newSupportEmployee.tickets[i] == null)
                            {
                                newSupportEmployee.tickets[i] = ticket;
                                added = true;
                                break;
                            }
                        }

                        if(added)
                        {
                            for(int i = 0; i < oldSupportEmployee.tickets.length; i++)
                            {
                                if(oldSupportEmployee.tickets[i] == ticket)
                                {
                                    oldSupportEmployee.tickets[i] = null;
                                    break;
                                }
                            }

                            ticket.assignedTo = newSupportEmployee;

                            ticket.addHistory(
                                "REASSIGNED",
                                oldSupportEmployee.getEmployeeName()
                                + " to "
                                + newSupportEmployee.getEmployeeName()
                            );

                            System.out.println("New: \n Assigned To: "
                                               + newSupportEmployee.getEmployeeName());

                            System.out.println("Ticket reassigned successfully.");
                        }
                        else
                        {
                            System.out.println("New support employee ticket list is full.");
                        }
                    }
                    else
                    {
                        System.out.println("Ticket was not reassigned.");
                    }
                }
            }
            else
            {
                System.out.println("Cannot reassign");
            }
        }
    }
    else
    {
        System.out.println("Employee is not a SUPPORT employee.");
        System.out.println("Ticket was not reassigned.");
    }
}
    void viewTicketSummary(Employee employee)
    {
        boolean found=false;
        for(int i=0;i<5;i++)
        {
            if(employee.tickets[i] !=null)
            {
                employee.tickets[i].displaySummary();
                found=true;
        
            }
        }

        if(!found)
        {
            System.out.println("No tickets found");
        }
        
             
       
    }
    void viewEmployeeTickets(Employee employee)
    {
        boolean found=false;
        Ticket[] tickets=employee.tickets;
        for(int i=0;i<5;i++)
        {
            if(tickets[i] != null)
            {
                tickets[i].displaySummary();
                found=true;
            }
        }
        if(!found)
        {
            System.out.println("No tickets found for this employee.");
        }
    }
    void assignTicket(int ticketId, Employee supportEmployee)
    {
        Ticket ticket=findTicket(ticketId);
        if(ticket==null)
        {
            System.out.println("Ticket not found.");
        }
        else
        {
            if(ticket.status.equalsIgnoreCase("RESOLVED") || ticket.status.equals("CLOSED") )
            {
                System.out.println("Closed ticket cannot be assigned");
            }
            else
            {
                if(ticket.assignedTo==null)
                {
                    assignTicket(ticket, supportEmployee);
                }
                else
                {
                    if(ticket.assignedTo==supportEmployee)
                    {
                        System.out.println("Ticket already assigned to the same employee.");
                    }
                    else
                    {
                        reassignTicket(ticket, supportEmployee);
                    }
                }
            }
           
        }
    }
     void viewHighPriorityTickets(Employee employee)
    {
        boolean found=false;
        for(int i=0;i<5;i++)
        {
            if(employee.tickets[i] != null)
            {
                
                if(employee.tickets[i].priority.equalsIgnoreCase("HIGH") || employee.tickets[i].priority.equalsIgnoreCase("CRITICAL"))
                {
                    employee.tickets[i].displaySummary();
                    found=true;
                }
            }
           
        } 
        if(!found)
            {
                System.out.println("No HIGH or CRITICAL priority tickets found.");
            }
    }
    void viewTicketsByStatus(Employee employee, String status)
    {
        boolean found=false;
        for(int i=0;i<5;i++)
        {
            if(employee.tickets[i] !=null)
            {
                if(employee.tickets[i].status.equalsIgnoreCase(status))
                {
                    employee.tickets[i].displaySummary();
                    found=true;
                }
            }
        }
        if(!found)
        {
            System.out.println("No "+status+" tickets found.");
        }
    }
    void showManagerMenu(Application app)
    {
        while (true) 
        { 
            try
            {

            
            System.out.println("===== MANAGER MENU ===== ");
            System.out.println("1.View Employee Tickets");                        
            System.out.println("2. View High/Critical Tickets");
            System.out.println("3. View Tickets By Status");
            System.out.println("4. Assign Ticket");
            System.out.println("5.Auto Assignment based on priority");
            System.out.println("6. Reassign Ticket");
            System.out.println("7. View Ticket History");
            System.out.println("8. Exit");
            System.out.println("Enter choice:");
            int methodcalling=sc.nextInt();
            
            if(methodcalling==1)
            {
                System.out.println("enter Employee Id: ");
                int empId=sc.nextInt();
                Employee selectedEmployee=findemployeebyId(empId);
               if( selectedEmployee != null)
               {
                    viewEmployeeTickets(selectedEmployee);
               }
               else
               {
                    System.out.println("Employee not found");
               }

                
            }
            else if(methodcalling==2)
            {
                System.out.println("Enter Employee ID:");
                int find=sc.nextInt();
                Employee selectedEmployee=findemployeebyId(find);
                if(selectedEmployee !=null)
                {
                     viewHighPriorityTickets(selectedEmployee);
                }
                else
                {
                    System.out.println("Employee not found");
                }
               
            }
            else if(methodcalling==3)
            {
                System.out.println("Enter Employee ID:");
                int find=sc.nextInt();
                Employee selectedEmployee=findemployeebyId(find);
                if(selectedEmployee != null)
                {
                    System.out.println("Enter status:");
                    String status=sc.next();
                    viewTicketsByStatus(selectedEmployee, status);
                }
                else
                {
                    System.out.println("Employee not found");
                }
                
            }
            else if(methodcalling==4)
            {
                System.out.println("Enter Ticket ID: ");
                int ticketId=sc.nextInt();
                viewSupportTeam();
                
                System.out.println("Enter Support Employee ID: ");
                int supportEmployeeId=sc.nextInt();
                Employee support = findSupportEmployee(supportEmployeeId);
                if(support == null)
                {
                    System.out.println("Support employee not found.");
                }
                else{
                    assignTicket(ticketId, support);
                }
                     
            }
            else if(methodcalling==5)
            {
                System.out.println("enter ticket Id");
                int ticketId=sc.nextInt();
                Ticket ticket=findTicket(ticketId);
                if(ticket!=null)
                {

                    prioritybased(ticket);
                }
                else
                {
                    System.out.println("Ticket not found");
                }
            }
            else if(methodcalling==6)
            {
                System.out.println("===== REASSIGN TICKET =====");
                System.out.println("Enter Ticket ID:");
                int reassignTicketId=sc.nextInt();
                viewSupportTeam();
                System.out.println("Enter New Support Employee ID: ");
                int reassignEmpId=sc.nextInt();
                Ticket ticket = findTicket(reassignTicketId);
                Employee newSupport = findSupportEmployee(reassignEmpId);
                if(ticket==null)
                {
                    System.out.println("Ticket not found.");
                }
                else if(newSupport == null)
                {
                    System.out.println("Support employee not found.");
                } 
                else
                {
                     reassignTicket(ticket, newSupport);
                }
               
            }
            else if(methodcalling == 7)
                {
                System.out.println("Enter Ticket ID:");
                int ticketId = sc.nextInt();

                Ticket ticket = findTicket(ticketId);

                if(ticket == null)
                {
                    System.out.println("Ticket not found.");
                }
                else
                {
                    ticket.displayHistory();
                }
            }
            
            else if(methodcalling==8)
            {
                break;
            }
            else 
            {
                System.out.println("invalid input");
            }
            }
            catch(InputMismatchException e)
            {
                System.out.println("please enter only numbers");
                sc.nextLine();
            }
            catch(NoSuchElementException e)
            {
                System.out.println("No more input available. Exiting...");
                break;
            }
        }
    }
    Employee findemployeebyId(int empid)
    {
        for(Employee emp:employee)
        {
            if(emp != null && emp.getEmployeeId()== empid && this.department.equalsIgnoreCase(emp.getDepartment()))
            {
                return emp;
            }
        }
        return null;
    }
    void addSupportEmployee(Employee employee)
    {
       
        if("SUPPORT".equals(employee.getRole() ))
        {      boolean added =false;
            for(int i=0;i<5;i++)
            {
                if(supportTeam[i] ==null)
                {
                    supportTeam[i]=employee;
                    added=true;
                    System.out.println("Support employee added successfully.");
                    break;
                }
            }
            if(!added)
            {
                System.out.println("Support team is full.");
            }   
        }
        else{
            System.out.println("Employee is not a SUPPORT employee.");
        }
    }
    void viewSupportTeam()
    {
        boolean found=false;
        System.out.println("===== SUPPORT TEAM =====");
        for(int i=0;i<5;i++)
        {
            if(supportTeam[i] !=null)
            {
                found=true;
                supportTeam[i].displayEmployee();
            }
            
            
        }
        if(!found){
                System.out.println("No support employees found.");
            }
        
    }
    Employee findSupportEmployee(int employeeId)
    {
        for(int i=0;i<5;i++)
        {
            if(supportTeam[i]!=null)
            {
                if(supportTeam[i].getEmployeeId()==employeeId)
                {
                    return supportTeam[i];
                }
                
            }
            
        }
        return null;
    }
    void addEmployee(Employee emp)
    {
        boolean added = false;

        for(int i = 0; i < 10; i++)
        {
            if(employee[i] == null)
            {
                employee[i] = emp;
                added = true;
                System.out.println("Employee added successfully.");
                break;
            }
        }

        if(!added)
        {
            System.out.println("Employee list is full.");
        }
    }
    void viewAllEmployees()
    {
    boolean found = false;

    System.out.println("===== ALL EMPLOYEES =====");

    for(int i = 0; i < 10; i++)
    {
        if(employee[i] != null)
        {
            employee[i].displayEmployee();
            found = true;
        }
    }

    if(!found)
    {
        System.out.println("No employees found.");
    }
}

void assignedTicket(Employee supportEmployee)
{
    System.out.println("===== ASSIGNED TICKETS =====");
    boolean found = false;

    for (Employee emp : employee)
    {
        if (emp != null && this.department.equalsIgnoreCase(emp.getDepartment()))
        {
            for (Ticket ticket : emp.tickets)
            {
                if (ticket != null && ticket.assignedTo != null)
                {
                    if (ticket.assignedTo == supportEmployee)
                    {
                        ticket.displayTicket();
                        found = true;
                    }
                }
            }
        }
    }

    if (!found)
    {
        System.out.println("No assigned tickets found.");
    }
}
Ticket findTicket(int ticketId)
{
    for(Employee emp: employee)
        {
            if(emp !=null && this.department.equalsIgnoreCase(emp.getDepartment()))
            {
                for(Ticket t:emp.tickets)
                {
                    if(t != null )
                    {
                        if(t.ticketId==ticketId)
                        {
                            return t;
                        }
                    }   
                }
            }    
        }
        return null;
}
void supportWorkLoad()
{
    for(Employee semp : supportTeam)
    {
        if(semp != null)
        {
            int activeTickets = 0;

            for(Employee emp : employee)
            {
                if(emp != null)
                {
                    for(Ticket ticket : emp.tickets)
                    {
                        if(ticket != null &&
                           !ticket.status.equals("CLOSED") &&
                           ticket.assignedTo == semp)
                        {
                            activeTickets++;
                        }
                    }
                }
            }

            int workload = calculateWorkload(semp);

            System.out.println(semp.getEmployeeName()+ " -> Active Tickets: " + activeTickets+ ", Workload Points: " + workload);
        }
    }
}
int getPoints(String priority)
{
    if(priority.equals("LOW"))
    {
        return 1;
    }
    else if(priority.equals("MEDIUM"))
    {
            return 2;
    }
    else if(priority.equals("HIGH"))
    {
        return 3;

    }
    else if(priority.equals("CRITICAL"))
    {
        return 5;
    }

    return 0;
}
int calculateWorkload(Employee employeeSupport)
{
    int totalWorkload=0;
    if(employeeSupport.tickets == null)
    {
        return 0;
    }
    for(Ticket ticket:employeeSupport.tickets)
    {
        if(ticket != null && ticket.status != null)
        {
              if(ticket.status.equals("ASSIGNED") || ticket.status.equals("IN_PROGRESS"))
                {
                    String priority=ticket.priority;
                    totalWorkload+=getPoints(priority);
                }
        }
    }
    return totalWorkload;
}
Employee leastWorkLoad(Ticket ticket)
{
    int workload=0;
    int least=Integer.MAX_VALUE;
    Employee bestSupport=null; 
    for(Employee supportEmployee:supportTeam)
    {
        if(supportEmployee != null)
        {
            workload=calculateWorkload(supportEmployee);
            if(workload<least)
            {
                least=workload;
                bestSupport= supportEmployee;
            }
        }
    }
    return bestSupport;
    
}
void prioritybased(Ticket ticket)
{
    Employee emp=leastWorkLoad(ticket);
    if(emp!=null)
    {
        assignTicket(ticket, emp);
        
    }
    else
    {
        System.out.println("no least workload employee");
    }
}

}